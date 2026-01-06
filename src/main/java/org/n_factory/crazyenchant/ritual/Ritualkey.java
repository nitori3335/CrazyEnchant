package org.n_factory.crazyenchant.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class Ritualkey {
    public record RitualKey(ResourceKey<Level> dimension, BlockPos pos) { }
}
