package org.n_factory.crazyenchant.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.n_factory.crazyenchant.Crazyenchant;
import org.n_factory.crazyenchant.ritual.RitualManager;

@Mod.EventBusSubscriber(modid = Crazyenchant.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RitualEventHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) return;

        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack stack = player.getItemInHand(hand);

        if (!stack.is(Items.IRON_INGOT) || !player.isShiftKeyDown()) return;

        BlockPos pos = event.getPos();
        Level level = event.getLevel();

        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            boolean success = RitualManager.performRitual(serverLevel, pos, serverPlayer);
            if (success) {
                event.setCanceled(true);
            }
        }
    }
}
