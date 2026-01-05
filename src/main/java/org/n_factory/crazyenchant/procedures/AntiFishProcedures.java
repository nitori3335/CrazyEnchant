package org.n_factory.crazyenchant.procedures;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.n_factory.crazyenchant.init.ModEnchantments;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AntiFishProcedures {

    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent event) {
        Player player = event.getEntity();
        if (player == null) return;

        ItemStack rod = player.getMainHandItem();
        int level = rod.getEnchantmentLevel(ModEnchantments.FISHHATER.get());
        if (level <= 0) return;

        event.setCanceled(true);

        ItemStack result = getLootWithReducedFish(player, level);

        if (!result.isEmpty()) {
            // 変更点: 浮き（bobber）の位置からアイテムを飛ばす
            FishingHook bobber = event.getHookEntity();
            spawnFlyingItem((ServerLevel) player.level(), bobber.getX(), bobber.getY(), bobber.getZ(), result, player);
        }
    }

    private static ItemStack getLootWithReducedFish(Player player, int level) {
        var server = player.level().getServer();
        if (server == null) return ItemStack.EMPTY;

        var lootManager = server.getLootData();
        var lootTable = lootManager.getLootTable(BuiltInLootTables.FISHING);

        LootParams params = new LootParams.Builder((ServerLevel) player.level())
                .withParameter(LootContextParams.ORIGIN, player.position())
                .withParameter(LootContextParams.TOOL, player.getMainHandItem())
                .withLuck(player.getLuck())
                .create(LootContextParamSets.FISHING);

        Random rand = new Random();

        for (int i = 0; i < 10; i++) {
            List<ItemStack> results = lootTable.getRandomItems(params);

            double fishChance = Math.max(0.0, 1.0 - level * 0.2);

            for (ItemStack s : results) {
                if (s.is(ItemTags.FISHES)) {
                    if (rand.nextDouble() < fishChance) {
                        return s;
                    } else {
                        continue;
                    }
                } else {
                    return s;
                }
            }
        }
        return new ItemStack(Items.STICK);
    }

    // 新規追加: 浮き位置からプレイヤーに向かってアイテムを飛ばすメソッド（バニラのFishingBobberEntity.use/retrieveを模倣）
    private static void spawnFlyingItem(ServerLevel world, double bobberX, double bobberY, double bobberZ, ItemStack stack, Player player) {
        ItemEntity itemEntity = new ItemEntity(world, bobberX, bobberY, bobberZ, stack);

        // プレイヤー位置 - 浮き位置 で方向ベクター計算
        double dx = player.getX() - bobberX;
        double dy = player.getY() - bobberY;
        double dz = player.getZ() - bobberZ;
        double dist = Mth.sqrt((float) (dx * dx + dy * dy + dz * dz));  // 距離

        // 速度設定: バニラ準拠（x/z: 0.1倍, y: 0.1倍 + sqrt(dist)*0.08, ランダムガウス追加）
        double speed = 0.1;  // バニラの基本速度
        itemEntity.setDeltaMovement(
                dx * speed + world.random.nextGaussian() * 0.01,
                dy * speed + Mth.sqrt((float) dist) * 0.08 + world.random.nextGaussian() * 0.01,
                dz * speed + world.random.nextGaussian() * 0.01
        );

        world.addFreshEntity(itemEntity);  // ワールドに追加
    }
}