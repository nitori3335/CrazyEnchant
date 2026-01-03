package org.n_factory.crazyenchant;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.n_factory.crazyenchant.init.*;
import org.n_factory.crazyenchant.procedures.*;

@Mod.EventBusSubscriber
public class SilkyFortuneEvent {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        Player player = event.getPlayer();
        if (player == null) return;

        ItemStack tool = player.getMainHandItem();
        if (tool.isEmpty()) return;

        // Silk Touch優先
        if (EnchantmentHelper.hasSilkTouch(tool)) return;

        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        boolean handled = SilkyFortuneProcedure.execute(serverLevel, pos, state, tool);

        if (handled) {
            // 対象ブロック → キャンセルして手動ドロップ + 削除
            event.setCanceled(true);
            serverLevel.removeBlock(pos, false);  // ゴースト防止
        }
        // handled = false → キャンセルせず → バニラの通常ドロップ + 破壊
    }
}