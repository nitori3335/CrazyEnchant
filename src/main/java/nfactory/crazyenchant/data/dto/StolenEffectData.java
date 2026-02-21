package nfactory.crazyenchant.data.dto;

import net.minecraft.world.effect.MobEffect;

public record StolenEffectData(
        MobEffect effect,
        int duration,
        int amplifier,
        boolean removeFromVictim
) {}
