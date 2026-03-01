package nfactory.crazyenchant.util;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class CombatUtil {

    public static LivingEntity getDirectMeleeAttacker(DamageSource source, Level level) {
        if (level.isClientSide()) return null;

        Entity attacker = source.getEntity();
        Entity directAttacker = source.getDirectEntity();

        if (!(attacker instanceof LivingEntity attackerEntity)) return null;
        if (directAttacker != attacker) return null;

        return attackerEntity;
    }

    public static boolean didDamage(LivingDamageEvent event) {
        return event.getAmount() > 0;
    }
}
