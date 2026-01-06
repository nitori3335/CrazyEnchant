package org.n_factory.crazyenchant.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class BarrierBlock extends Block {
    public BarrierBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .sound(SoundType.GLASS)
                .strength(-1.0F, 3.4e38F)
                .noOcclusion()
                .isRedstoneConductor((s, w, p) -> false)
                .isSuffocating((s, w, p) -> false)
        );
    }
}
