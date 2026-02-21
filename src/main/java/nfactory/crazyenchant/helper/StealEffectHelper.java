package nfactory.crazyenchant.helper;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import nfactory.crazyenchant.config.CrazyEnchantConfig;
import nfactory.crazyenchant.data.dto.EffectFilterRule;
import nfactory.crazyenchant.data.result.StealResult;
import nfactory.crazyenchant.data.dto.StolenEffectData;
import nfactory.crazyenchant.util.MobEffectUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StealEffectHelper {

    public static StealResult process(
            List<MobEffectInstance> attackerEffectList, List<MobEffectInstance> victimEffectList,
            int enchantLevel, EffectFilterRule filterRule
    ) {
        StealResult result = new StealResult();

        victimEffectList.removeIf(instance ->
                !MobEffectUtil.isAllowed(instance.getEffect(), filterRule)
        );

        Collections.shuffle(victimEffectList);

        Map<MobEffect, MobEffectInstance> attackerEffectMap =
                attackerEffectList.stream()
                        .collect(Collectors.toMap(
                                MobEffectInstance::getEffect,
                                Function.identity()
                        ));

        for (int stealCount = 0; stealCount < enchantLevel; stealCount++) {

            if (victimEffectList.isEmpty()) { break; }

            MobEffectInstance victim = victimEffectList.remove(0);
            MobEffectInstance attacker = attackerEffectMap.get(victim.getEffect());

            if (attacker == null) {
                result.addStolenEffect(new StolenEffectData(
                        victim.getEffect(),
                        victim.getDuration(),
                        victim.getAmplifier(),
                        true
                ));
                continue;
            }

            int finalAmplifier = calcAmplifier(attacker, victim);
            int finalDuration = calcDuration(attacker, victim);

            result.addStolenEffect(new StolenEffectData(
                    victim.getEffect(),
                    finalDuration,
                    finalAmplifier,
                    true
            ));
        }
        return  result;
    }

    private  static int calcAmplifier(
            MobEffectInstance attackerEffect, MobEffectInstance victimEffect
    ) {
        int attackerAmplifier = attackerEffect.getAmplifier();
        int victimAmplifier = victimEffect.getAmplifier();

        boolean attackerDuration = attackerEffect.getDuration() == -1;
        boolean victimDuration = victimEffect.getDuration() == -1;

        int baseAmplifier = Math.max(attackerAmplifier, victimAmplifier);

        boolean bothInfiniteAndSameLevel =
                (attackerDuration && victimDuration) && (attackerAmplifier == victimAmplifier);

        if (bothInfiniteAndSameLevel) {
            return Math.min(baseAmplifier + 1, CrazyEnchantConfig.StealEffect.stealMaxLevelCache);
        }

        return baseAmplifier;
    }

    private static int calcDuration(
            MobEffectInstance attackerEffect, MobEffectInstance victimEffect
    ) {
        int threshold = CrazyEnchantConfig.StealEffect.stealInfiniteThresholdCache * 20;
        double factor = CrazyEnchantConfig.StealEffect.stealLevelTimeFactorCache;

        int attackerDuration = virtualDuration(attackerEffect);
        int victimDuration = virtualDuration(victimEffect);

        int attackerAmplifier = attackerEffect.getAmplifier();
        int victimAmplifier = victimEffect.getAmplifier();

        int difference = Math.abs(attackerAmplifier - victimAmplifier);

        boolean attackerLower = attackerAmplifier < victimAmplifier;

        int lowerDuration = attackerLower ? attackerDuration : victimDuration;
        int higherDuration = attackerLower ? victimDuration : attackerDuration;

        int corrected = (int) (lowerDuration / Math.pow(factor, difference));

        int result = Math.max(higherDuration, corrected);

        return result >= threshold ? -1 : result;
    }

    private static int virtualDuration(MobEffectInstance instance) {
        return instance.getDuration() == -1
                ? CrazyEnchantConfig.StealEffect.stealInfiniteThresholdCache * 20
                : instance.getDuration();
    }
}
