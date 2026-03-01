package nfactory.crazyenchant.helper;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class DamageSourceHelper {

    private DamageSourceHelper() {}

    public static final String EXTRA_HIT_MSG_ID = "extra_hit";

    public static DamageSource extraHit(LivingEntity attacker) {
        return new DamageSource(attacker.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(DamageTypes.GENERIC),
                attacker) {
            @Override
            public @NotNull String getMsgId() {
                return EXTRA_HIT_MSG_ID;
            }
        };
    }
}
