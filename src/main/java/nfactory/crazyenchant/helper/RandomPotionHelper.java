package nfactory.crazyenchant.helper;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import nfactory.crazyenchant.config.CrazyEnchantConfig;
import nfactory.crazyenchant.data.dto.EffectFilterRule;
import nfactory.crazyenchant.util.MobEffectUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("removal")
public class RandomPotionHelper {

    private static final Random RANDOM = new Random();

    public static void applyRandomEffect(LivingEntity target, int rolls) {
        if (target == null || rolls <= 0) return;

        int duration = CrazyEnchantConfig.RandomEffect.DURATION.get();
        int baseLevel = CrazyEnchantConfig.RandomEffect.LEVEL.get();
        double probability = CrazyEnchantConfig.RandomEffect.PROBABILITY.get();

        EffectFilterRule rule = new EffectFilterRule(
                CrazyEnchantConfig.RandomEffect.INCLUDE_POSITIVE.get(),
                CrazyEnchantConfig.RandomEffect.INCLUDE_NEGATIVE.get(),
                CrazyEnchantConfig.RandomEffect.INCLUDE_NEUTRAL.get(),
                new ArrayList<>(CrazyEnchantConfig.RandomEffect.WHITELIST_EFFECTS.get()),
                new ArrayList<>(CrazyEnchantConfig.RandomEffect.BLACKLIST_EFFECTS.get())
        );

        int amplifier = Math.max(0, baseLevel - 1);

        Set<MobEffect> activeEffects = target.getActiveEffects().stream()
                .map(MobEffectInstance::getEffect).collect(Collectors.toSet());

        List<MobEffect> candidates = ForgeRegistries.MOB_EFFECTS.getValues().stream()
                .filter(effect -> !activeEffects.contains(effect))
                .filter(effect -> MobEffectUtil.isAllowed(effect, rule)).toList();

        if (candidates.isEmpty()) return;

        List<MobEffect> mutable = new ArrayList<>(candidates);

        for (int i = 0; i < rolls && !mutable.isEmpty(); i++) {
            if (RANDOM.nextDouble() > probability) continue;

            MobEffect selected = mutable.remove(RANDOM.nextInt(mutable.size()));
            target.addEffect(new MobEffectInstance(selected, duration, amplifier));
        }
    }
}