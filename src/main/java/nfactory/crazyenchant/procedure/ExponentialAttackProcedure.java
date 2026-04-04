package nfactory.crazyenchant.procedure;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nfactory.crazyenchant.init.ModEnchantments;
import nfactory.crazyenchant.util.CombatUtil;

@Mod.EventBusSubscriber
public class ExponentialAttackProcedure {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity attacker = CombatUtil.getDirectMeleeAttacker(event.getSource(), event.getEntity().level());
        if (attacker == null) return;
        if (!(CombatUtil.didDamage(event))) return;

        ItemStack weapon = attacker.getMainHandItem();
        int level = weapon.getEnchantmentLevel(ModEnchantments.EXPONENTIAL.get());
        if (level <= 1) return;

        float baseDamage = event.getAmount();

        double finalDamage = Math.pow(baseDamage, level);
        if (!Double.isFinite(finalDamage)) {
            finalDamage = Float.MAX_VALUE;
        }

        event.setAmount((float) finalDamage);
    }
}
