package nfactory.crazyenchant.procedure;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nfactory.crazyenchant.helper.RandomPotionHelper;
import nfactory.crazyenchant.init.ModEnchantments;

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
                ModEnchantments.RANDOMEFFECT.get(),
                weapon
        );

        if (level > 0) {
            RandomPotionHelper.applyRandomEffect(event.getEntity(), level);
        }
    }
}
