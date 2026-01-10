package org.n_factory.crazyenchant.handler;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.n_factory.crazyenchant.Crazyenchant;
import org.n_factory.crazyenchant.config.MinionConfig;
import org.n_factory.crazyenchant.init.ModBlocks;
import org.n_factory.crazyenchant.ritual.Ritualkey;
import org.n_factory.crazyenchant.ritual.SummoningGate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Mod.EventBusSubscriber(modid = Crazyenchant.MODID)
public class SummoningEventHandler {
    private static final int ARENA_RADIUS = 16;

    // ミニオン湧き + 脱走チェック（次元を考慮するよう修正）
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.getServer().getTickCount() % 40 != 0) return;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            // ACTIVE_RITUALS をコピーして反復（ConcurrentModification を避ける）
            List<Entry<Ritualkey.RitualKey, Boolean>> entries = new ArrayList<>(Crazyenchant.ACTIVE_RITUALS.entrySet());
            for (Entry<Ritualkey.RitualKey, Boolean> entry : entries) {
                if (!entry.getValue()) continue;

                Ritualkey.RitualKey key = entry.getKey();
                // 次元が一致しないならスキップ
                if (!key.dimension().equals(level.dimension())) continue;

                BlockPos beaconPos = key.pos();

                // ミニオン湧き(各ゲートから)
                for (SummoningGate gate : level.getEntitiesOfClass(SummoningGate.class, new AABB(beaconPos).inflate(50))) {
                    if (!gate.isAlive()) continue;

                    double offsetX = level.random.nextGaussian() * 3;
                    double offsetZ = level.random.nextGaussian() * 3;
                    double spawnY = gate.getY();

                    // ランダム選択（JSONから）
                    EntityType<?> randomType = MinionConfig.getRandomMinion();
                    Mob minion = (Mob) randomType.create(level);
                    if (minion != null) {
                        minion.moveTo(gate.getX() + offsetX, spawnY, gate.getZ() + offsetZ);
                        minion.addTag("enchantor_minion");
                        minion.setPersistenceRequired();
                        minion.getPersistentData().putString("DeathLootTable", "minecraft:empty");  // ドロップなし
                        level.addFreshEntity(minion);
                    }
                }

                // 2. 脱走チェック（同一次元のプレイヤーのみ確認）
                boolean allEscaped = true;
                for (ServerPlayer player : level.players()) {
                    double distSq = player.distanceToSqr(beaconPos.getX() + 0.5, beaconPos.getY() + 0.5, beaconPos.getZ() + 0.5);
                    if (distSq <= 20 * 20) {  // 半径20以内
                        allEscaped = false;
                        break;
                    }
                }

                if (allEscaped) {
                    // 強制終了（同一次元にいるボスのみ対象）
                    for (IronGolem golem : level.getEntitiesOfClass(IronGolem.class, new AABB(beaconPos).inflate(30))) {
                        if (golem.getTags().contains("enchantor_boss")) {
                            golem.discard();
                        }
                    }
                    Crazyenchant.ACTIVE_RITUALS.remove(key);
                    clearArenaBarrier(level, beaconPos);
                    level.players().forEach(p -> p.sendSystemMessage(Component.literal("§c全員脱走！ボス戦強制終了").withStyle(ChatFormatting.RED)));
                }
            }
        }
    }

    // ボス死亡時(壁消去 + 保護解除) — 次元を考慮して最も近い儀式を探す
    @SubscribeEvent
    public static void onBossDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof IronGolem golem)) return;
        if (!golem.getTags().contains("enchantor_boss")) return;

        ServerLevel level = (ServerLevel) golem.level();

        // 同一次元、かつゴーレムに近い儀式を探す（最小距離）
        Ritualkey.RitualKey matched = findNearestActiveRitual(level, golem.blockPosition(), 100);
        if (matched == null) {
            Crazyenchant.LOGGER.warn("No active ritual found on boss death in this dimension!");
            return;
        }
        BlockPos beaconPos = matched.pos();

        // 壁消去（既存）
        clearArenaBarrier(level, beaconPos);

        // 保護解除（既存）
        Crazyenchant.ACTIVE_RITUALS.remove(matched);

        // ★ 新規追加：周辺のSummoningGateをすべて削除
        AABB searchBox = new AABB(beaconPos).inflate(ARENA_RADIUS + 10);  // 少し広めに検索
        for (SummoningGate gate : level.getEntitiesOfClass(SummoningGate.class, searchBox)) {
            if (gate.isAlive()) {
                gate.discard();  // 即削除
            }
        }

        // 演出（音・メッセージ）
        level.playSound(null, beaconPos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 3.0F, 0.8F);
        level.players().forEach(p -> p.sendSystemMessage(
                Component.literal("§aボス撃破！儀式終了、ゲートも消滅しました").withStyle(ChatFormatting.GREEN)
        ));
    }

    // 壁消去メソッド（ServerLevel と中心座標）
    private static void clearArenaBarrier(ServerLevel level, BlockPos center) {
        int radius = ARENA_RADIUS;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distSq = x * x + z * z;
                if (distSq > radius * radius) continue;
                for (int y = -5; y <= 20; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    if (x == 0 && z == 0 && y == 0) continue;  //beacon保護
                    if (level.getBlockState(pos).is(ModBlocks.BARRIER_BLOCK.get())) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    // 床破壊保護 — 近い儀式（同一次元）を探して判定
    @SubscribeEvent
    public static void onBlockBleak(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;

        ServerLevel level = (ServerLevel) event.getLevel();
        BlockPos brokenPos = event.getPos();

        // brokenPos の近傍で同一次元の儀式を探す（ARENA_RADIUS を上限に）
        Ritualkey.RitualKey matched = findNearestActiveRitual(level, brokenPos, ARENA_RADIUS + 5);
        if (matched == null) return;

        BlockPos beaconCenter = matched.pos();
        int floorY = beaconCenter.getY() - 1;
        BlockPos floorCenter = beaconCenter.below();
        double distSq = brokenPos.distSqr(floorCenter);

        if (distSq > ARENA_RADIUS * ARENA_RADIUS) return;
        boolean isFloor = (brokenPos.getY() == floorY) && (distSq <= ARENA_RADIUS * ARENA_RADIUS);
        // ビーコン本体チェック（Y一致 + 同一位置）
        boolean isBeacon = brokenPos.equals(beaconCenter);

        if (isFloor || isBeacon) {
            event.setCanceled(true);
            event.getPlayer().sendSystemMessage(
                    Component.literal("§c儀式の床またはビーコンは破壊できません！").withStyle(ChatFormatting.RED)
            );
        }
    }

    // 爆発保護 — 近い儀式（同一次元）を探して保護対象を作る
    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        if (event.getLevel().isClientSide) return;

        ServerLevel level = (ServerLevel) event.getLevel();
        // event には影響を受けたブロックリストが入るが、まず近傍の儀式を探す
        Ritualkey.RitualKey matched = findNearestActiveRitual(level, BlockPos.ZERO, ARENA_RADIUS);
        // 上の取得は汎用。より正確にしたければ爆発中心を渡す仕組みにしてください。
        if (matched == null) return;

        BlockPos beaconCenter = matched.pos();

        List<BlockPos> protect = new ArrayList<>();
        int radius = ARENA_RADIUS;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distSq = x * x + z * z;
                if (distSq > radius * radius) continue;
                protect.add(beaconCenter.offset(x, -1, z));
            }
        }

        protect.add(beaconCenter);

        event.getAffectedBlocks().removeIf(protect::contains);
    }

    // 指定位置（pos）を基準に、同一次元の最も近い ACTIVE_RITUALS を返す。なければ null。
    private static Ritualkey.RitualKey findNearestActiveRitual(ServerLevel level, BlockPos pos, int maxDistance) {
        double best = Double.MAX_VALUE;
        Ritualkey.RitualKey bestKey = null;
        int maxSq = maxDistance * maxDistance;

        for (Map.Entry<Ritualkey.RitualKey, Boolean> entry : Crazyenchant.ACTIVE_RITUALS.entrySet()) {
            if (!entry.getValue()) continue;
            Ritualkey.RitualKey key = entry.getKey();
            if (!key.dimension().equals(level.dimension())) continue;

            double d = key.pos().distSqr(pos);
            if (d <= maxSq && d < best) {
                best = d;
                bestKey = key;
            }
        }
        return bestKey;
    }

    @SubscribeEvent
    public static void onMinionDeath(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide) return;

        LivingEntity entity = event.getEntity();
        if (!entity.getTags().contains("enchantor_minion")) return;

        // 安全にドロップリストをクリア（nullチェック）
        Collection<ItemEntity> drops = event.getDrops();
        if (drops != null) {
            drops.clear();
        }
    }
}