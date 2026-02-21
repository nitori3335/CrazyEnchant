package nfactory.crazyenchant.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import nfactory.crazyenchant.data.dto.EffectFilterRule;

public class MobEffectUtil {

    public static boolean isAllowed(MobEffect effect, EffectFilterRule filterRule) {
        String id = getId(effect);

        if (filterRule.blacklist().contains(id)) { return false; }
        if (filterRule.whitelist().contains(id)) { return true; }

        return isAllowedByCategory(effect, filterRule);
    }

    public static boolean isAllowedByCategory(MobEffect effect, EffectFilterRule filterRule) {
        return switch (effect.getCategory()) {
            case BENEFICIAL -> filterRule.allowBeneficial();
            case HARMFUL ->  filterRule.allowHarmful();
            case NEUTRAL ->   filterRule.allowNeutral();
        };
    }

    public static String getId(MobEffect effect) {
        var key = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        return key != null ? key.toString() : "unknown";
    }
}
