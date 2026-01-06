package org.n_factory.crazyenchant.init;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.n_factory.crazyenchant.Crazyenchant;
import org.n_factory.crazyenchant.block.BarrierBlock;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Crazyenchant.MODID);

    public static final RegistryObject<Block> BARRIER_BLOCK = BLOCKS.register("barrier_block", BarrierBlock::new);
}
