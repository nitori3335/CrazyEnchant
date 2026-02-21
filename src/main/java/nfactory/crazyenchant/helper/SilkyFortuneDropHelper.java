package nfactory.crazyenchant.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SilkyFortuneDropHelper {

    public static void spawnSilkyDrop(
            ServerLevel level, BlockPos pos, BlockState state, ItemStack tool, int fortuneLevel, boolean keepNBT
    ) {
        ItemStack base = createBaseDrop(level, pos, state, tool, keepNBT);

        int bonus = level.random.nextInt(fortuneLevel + 2);
        int count = 1 + bonus;

        base.setCount(count);

        level.addFreshEntity(new ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                base
        ));
    }

    private static ItemStack createBaseDrop(ServerLevel level, BlockPos pos, BlockState state, ItemStack tool, boolean keepNBT) {
        ItemStack normal = getVanillaDrop(level, pos, state, tool);
        ItemStack clone = state.getCloneItemStack(
                new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false),
                level, pos, null
        );

        if (normal.isEmpty() || normal.getItem() != clone.getItem()) {
            return applyNBTIfNeeded(level, pos, state, clone, keepNBT);
        }

        if (!ItemStack.isSameItemSameTags(normal, clone)) {
            return normal.copy();
        }

        return applyNBTIfNeeded(level, pos, state, clone, keepNBT);
    }

    private static ItemStack getVanillaDrop(ServerLevel level, BlockPos pos, BlockState state, ItemStack tool) {
        LootParams.Builder builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.BLOCK_STATE, state)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos))
                .withParameter(LootContextParams.TOOL, tool);

        List<ItemStack> drops = state.getDrops(builder);
        if (drops.isEmpty()) return ItemStack.EMPTY;

        return drops.get(0).copy();
    }

    private static ItemStack applyNBTIfNeeded(ServerLevel level, BlockPos pos, BlockState state, ItemStack stack, boolean keepNBT) {
        if (!keepNBT) return stack;

        if (!state.hasBlockEntity()) return stack;

        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) return stack;

        try {
            be.saveToItem(stack);
        } catch (Exception ignored) {}

        return stack;
    }

}
