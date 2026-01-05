package org.n_factory.crazyenchant.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.n_factory.crazyenchant.helpers.RandomPotionHelper;
import org.n_factory.crazyenchant.init.ModEnchantments;

@Mod.EventBusSubscriber
public class RandomEffectAttackProcedure {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) return;  // サーバー側のみ

        // ダメージが発生しない攻撃はスキップ（キャンセル済みや0ダメージ）
        if (event.isCanceled() || event.getAmount() <= 0) return;

        Entity source = event.getSource().getEntity();
        if (!(source instanceof LivingEntity attacker)) return;

        ItemStack weapon = attacker.getMainHandItem();
        int level = EnchantmentHelper.getTagEnchantmentLevel(
                ModEnchantments.RANDOM_EFFECT.get(),
                weapon
        );

        if (level > 0) {
            // ここでランダム効果適用（RandomPotionHelperはあなたのヘルパーだと思う）
            RandomPotionHelper.applyRandomEffect(event.getEntity(), level);
        }
    }
}