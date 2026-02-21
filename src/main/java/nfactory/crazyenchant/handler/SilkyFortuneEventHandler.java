package nfactory.crazyenchant.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nfactory.crazyenchant.CrazyEnchant;
import nfactory.crazyenchant.procedure.SilkyFortuneProcedure;

@Mod.EventBusSubscriber(modid = CrazyEnchant.MODID)
public class SilkyFortuneEventHandler {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {

        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        Player player = event.getPlayer();
        if (player == null) return;

        ItemStack tool = player.getMainHandItem();
        if (tool.isEmpty()) return;

        if (EnchantmentHelper.hasSilkTouch(tool)) return;

        BlockPos pos =event.getPos();
        BlockState state = event.getState();

        boolean handled = SilkyFortuneProcedure.execute(serverLevel, pos, state, tool);

        if (handled) {
            event.setCanceled(true);
            serverLevel.removeBlock(pos, false);
        }
    }
}
