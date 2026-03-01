package nfactory.crazyenchant.procedure;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nfactory.crazyenchant.helper.RandomPotionHelper;
import nfactory.crazyenchant.init.ModEnchantments;
import nfactory.crazyenchant.util.CombatUtil;

@Mod.EventBusSubscriber
public class RandomEffectAttackProcedure {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity attacker = CombatUtil.getDirectMeleeAttacker(event.getSource(), event.getEntity().level());
        if (attacker == null) return;
        if (!(CombatUtil.didDamage(event))) return;

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
