package nfactory.crazyenchant.procedure;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import nfactory.crazyenchant.config.CrazyEnchantConfig;
import nfactory.crazyenchant.data.context.SilkyFortuneContext;
import nfactory.crazyenchant.helper.SilkyFortuneDropHelper;
import nfactory.crazyenchant.init.ModEnchantments;

public class SilkyFortuneProcedure {

    public static boolean execute(ServerLevel serverLevel, BlockPos pos, BlockState state, ItemStack tool) {

        int silkFortuneLevel = tool.getEnchantmentLevel(ModEnchantments.SILKFORTUNE.get());
        if (silkFortuneLevel <= 0) return false;

        if (!SilkyFortuneContext.canApply(state, serverLevel)) {
            return false;
        }

        boolean keepNBT = CrazyEnchantConfig.SilkFortune.keepNbtCache;

        SilkyFortuneDropHelper.spawnSilkyDrop(serverLevel, pos, state, tool, silkFortuneLevel, keepNBT);

        return true;
    }
}
