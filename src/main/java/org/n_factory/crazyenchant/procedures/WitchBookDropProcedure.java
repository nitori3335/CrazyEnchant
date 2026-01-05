package org.n_factory.crazyenchant.procedures;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.n_factory.crazyenchant.init.ModEnchantments;

@Mod.EventBusSubscriber
public class WitchBookDropProcedure {

    private static final int MAX_LEVEL = 10;

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Witch)) return;

        var source = event.getSource().getEntity();
        if (!(source instanceof Player player)) return;

        ItemStack weapon = player.getMainHandItem();
        int level = weapon.getEnchantmentLevel(ModEnchantments.WITCHHUNTER.get());
        if (level <= 0) return;

        // 確率判定（ワールドのランダムを直接使用）
        if (player.level().random.nextDouble() > 1.0) return;  // 固定100%なので常に通るが、変えたい時はここ

        // エンチャントリスト取得
        var allEnchantments = ForgeRegistries.ENCHANTMENTS.getValues().stream().toList();
        if (allEnchantments.isEmpty()) return;

        // ランダムは**ワールド**のものを**直接**使う（LegacyRandomSource OK）
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);

        int enchCount = Math.min(level, 5);

        for (int i = 0; i < enchCount; i++) {
            var chosen = allEnchantments.get(player.level().random.nextInt(allEnchantments.size()));

            int maxAllowed = Math.min(chosen.getMaxLevel(), MAX_LEVEL);
            int enchLevel = 1 + player.level().random.nextInt(maxAllowed);

            book.enchant(chosen, enchLevel);
        }

        event.getDrops().add(new ItemEntity(
                player.level(),
                event.getEntity().getX(),
                event.getEntity().getY(),
                event.getEntity().getZ(),
                book
        ));
    }
}