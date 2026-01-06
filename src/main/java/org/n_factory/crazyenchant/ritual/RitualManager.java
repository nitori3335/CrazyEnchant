package org.n_factory.crazyenchant.ritual;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.n_factory.crazyenchant.Crazyenchant;
import org.n_factory.crazyenchant.init.ModBlocks;
import org.n_factory.crazyenchant.init.ModEntities;

import java.util.List;

public class RitualManager {
    private static final int INNER_RADIUS = 15; //内側空き空間
    private static final int OUTER_RADIUS = 16; //壁の外側
    private static final int MIN_Y = -5; //下方向
    private static final int MAX_Y = 20; //天井高さ

    private static final BlockPos[] PYLON_OFFSETS = {
            new BlockPos(18, 1, 0), new BlockPos(0, 1, 18),
            new BlockPos(-18, 1, 0), new BlockPos(0, 1, -18),
            new BlockPos(13, 1, 13), new BlockPos(-13, 1, -13),
            new BlockPos(13, 1, -13), new BlockPos(-13, 1, 13)
    };

    /**
     * 儀式実行(Beaconに鉄ingotをShift右クリック時)
     */
    public static boolean performRitual(ServerLevel level, BlockPos beaconPos, ServerPlayer player) {
        // 新しいキー（次元 + 座標）
        Ritualkey.RitualKey key = new Ritualkey.RitualKey(level.dimension(), beaconPos);

        // 1. 既に儀式がアクティブかチェック
        if (Crazyenchant.ACTIVE_RITUALS.containsKey(key) && Crazyenchant.ACTIVE_RITUALS.get(key)) {
            player.sendSystemMessage(Component.literal("§cこの場所ですでにボス戦が進行中！").withStyle(ChatFormatting.RED));
            return false;
        }

        // 2. Pylon 8個チェック
        int pylonCount = 0;
        List<EndCrystal> crystals = level.getEntitiesOfClass(EndCrystal.class, new AABB(beaconPos).inflate(25));
        for (BlockPos offset : PYLON_OFFSETS) {
            BlockPos expected = beaconPos.offset(offset);
            for (EndCrystal crystal : crystals) {
                if (crystal.blockPosition().equals(expected)) {
                    pylonCount++;
                    break;
                }
            }
        }
        if (pylonCount != 8) {
            player.sendSystemMessage(Component.literal("§cPylonが8個必要！ (" + pylonCount + "/8)").withStyle(ChatFormatting.RED));
            spawnErrorParticles(level, beaconPos);
            return false;
        }

        // 3. 床チェック
        if (!checkFloorSolid(level, beaconPos)) {
            player.sendSystemMessage(Component.literal("§c床が不完全！").withStyle(ChatFormatting.RED));
            spawnErrorParticles(level, beaconPos);
            return false;
        }

        // 4.上空空きチェック
        if (!checkSkyClear(level, beaconPos)) {
            player.sendSystemMessage(Component.literal("§c上空にブロックがある！").withStyle(ChatFormatting.RED));
            spawnErrorParticles(level, beaconPos);
            return false;
        }

        // 成功！

        player.getItemInHand(player.getUsedItemHand()).shrink(1); //鉄ingot消費

        // 壁生成
        generateArenaBarrier(level, beaconPos);

        // ゲート生成
        generateGates(level, beaconPos);

        // ボス召喚
        IronGolem boss = EntityType.IRON_GOLEM.create(level);
        if (boss != null) {
            boss.moveTo(beaconPos.getX() + 0.5, beaconPos.getY() + 2, beaconPos.getZ() + 0.5);
            boss.addTag("enchantor_boss");
            level.addFreshEntity(boss);
        }

        // 儀式アクティブ化（次元込みのキーで保存）
        Crazyenchant.ACTIVE_RITUALS.put(key, true);
        Crazyenchant.LOGGER.info("Active ritual saved: {} in {}", beaconPos, level.dimension());

        // 演出
        level.playSound(null, beaconPos, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 1.0F, 0.8F);
        spawnSuccessParticles(level, beaconPos);

        player.sendSystemMessage(Component.literal("§6[Enchantor] 儀式成功！ボス出現！").withStyle(ChatFormatting.GOLD));

        return true;
    }

    //床チェック
    private static boolean checkFloorSolid(ServerLevel level, BlockPos center) {
        for (int x = -INNER_RADIUS; x <= INNER_RADIUS; x++) {
            for (int z = -INNER_RADIUS; z <= INNER_RADIUS; z++) {
                if (x * x + z * z > INNER_RADIUS * INNER_RADIUS) continue;
                BlockPos floor = center.offset(x, -1, z);
                BlockState state = level.getBlockState(floor);
                if (state.isAir() || ! state.isSolidRender(level, floor)) return false;
            }
        }
        return true;
    }

    //上空空きチェック
    private static boolean checkSkyClear(ServerLevel level, BlockPos center) {
        for (int y = 1; y <= 10; y++) {
            if (!level.getBlockState(center.offset(0, y, 0)).isAir()) return false;
        }
        return true;
    }

    //壁生成
    private static void generateArenaBarrier(ServerLevel level, BlockPos center) {
        for (int x = -OUTER_RADIUS; x <= OUTER_RADIUS; x++) {
            for (int z = -OUTER_RADIUS; z <= OUTER_RADIUS; z++) {
                double distSq = x * x + z * z;
                if (distSq > INNER_RADIUS * INNER_RADIUS && distSq <= OUTER_RADIUS * OUTER_RADIUS) {
                    for (int y = MIN_Y; y <= MAX_Y; y++) {
                        BlockPos pos = center.offset(x, y, z);
                        if (level.getBlockState(pos).isAir()) {
                            level.setBlock(pos, ModBlocks.BARRIER_BLOCK.get().defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    //ゲート生成
    private static void generateGates(ServerLevel level, BlockPos beaconPos) {
        int[][] offsets = {
                {13, 0}, {-13, 0}, {0, 13}, {0, -13},
                {9, 9}, {9, -9}, {-9, -9}, {-9, 9}
        };

        for (int[] offset : offsets) {
            BlockPos gatePos = beaconPos.offset(offset[0], 1, offset[1]);

            SummoningGate gate = ModEntities.SUMMONING_GATE.get().create(level);
            if (gate != null) {
                gate.moveTo(gatePos.getX() + 0.5, gatePos.getY(), gatePos.getZ() + 0.5);
                level.addFreshEntity(gate);
            }
        }
        level.playSound(null, beaconPos, SoundEvents.PORTAL_TRIGGER, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    //エラー演出
    private static void spawnErrorParticles(Level level, BlockPos pos) {
        for (int i = 0; i < 80; i++) {
            double dx = (level.random.nextDouble() - 0.5) * 20;
            double dy = level.random.nextDouble() * 12;
            double dz = (level.random.nextDouble() - 0.5) * 20;

            level.addParticle(ParticleTypes.ANGRY_VILLAGER, pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz, 0, 0, 0);
        }
    }

    //成功演出
    private static void spawnSuccessParticles(Level level, BlockPos pos) {
        for (int i = 0; i < 200; i++) {
            double dx = (level.random.nextDouble() - 0.5) * 10;
            double dy = level.random.nextDouble() * 5;
            double dz = (level.random.nextDouble() - 0.5) * 10;

            level.addParticle(ParticleTypes.ENCHANTED_HIT, pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz, 0, 0, 0);
        }
    }
}