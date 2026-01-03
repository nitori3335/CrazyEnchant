package org.n_factory.crazyenchant.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.n_factory.crazyenchant.init.*;
import org.n_factory.crazyenchant.*;

import java.util.List;

public class SilkyFortuneProcedure {

    public static boolean execute(ServerLevel serverLevel, BlockPos pos, BlockState state, ItemStack tool) {

        int silkFortuneLevel = tool.getEnchantmentLevel(ModEnchantments.SILKFORTUNE.get());
        if (silkFortuneLevel <= 0) return false;

        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (blockId == null) return false;

        String idStr = blockId.toString();

        // Configキャッシュ取得
        String whitelistTagId = CrazyEnchantConfig.silkyFortuneWhitelistTagCache;
        String blacklistTagId = CrazyEnchantConfig.silkyFortuneBlacklistTagCache;

        List<String> whitelistBlocks = CrazyEnchantConfig.silkyFortuneWhitelistBlocksCache;

        boolean useWhitelistTag = !whitelistTagId.isEmpty();
        boolean useBlacklistTag = !blacklistTagId.isEmpty();

        boolean useWhitelistBlocks = !whitelistBlocks.isEmpty();

        TagKey<Block> whitelistTag = useWhitelistTag ? TagKey.create(Registries.BLOCK, new ResourceLocation(whitelistTagId)) : null;
        TagKey<Block> blacklistTag = useBlacklistTag ? TagKey.create(Registries.BLOCK, new ResourceLocation(blacklistTagId)) : null;

        // ホワイトリスト判定（リストが最優先）
        boolean isWhitelisted = false;

        // 1. ホワイトリストブロックチェック（最優先）
        if (useWhitelistBlocks && whitelistBlocks.contains(idStr)) {
            isWhitelisted = true;
        }

        // 2. ブラックリストチェック（リストで上書きされなかった場合のみ除外）
        boolean isBlacklisted = false;

        if (!isWhitelisted) {  // リストでOKならブラック無視
            if (useBlacklistTag) {
                Object tagContentsObj = serverLevel.registryAccess()
                        .registryOrThrow(Registries.BLOCK)
                        .getTagOrEmpty(blacklistTag);

                if (tagContentsObj instanceof HolderSet.Named<?> tagContents) {
                    for (Holder<?> holder : tagContents) {
                        if (holder.value() == state.getBlock()) {
                            isBlacklisted = true;
                            break;
                        }
                    }
                }
            }
        }

        // 3. ホワイトリストタグチェック（リスト/ブラックリストの後回し）
        if (!isWhitelisted && !isBlacklisted && useWhitelistTag) {
            Object tagContentsObj = serverLevel.registryAccess()
                    .registryOrThrow(Registries.BLOCK)
                    .getTagOrEmpty(whitelistTag);

            if (tagContentsObj instanceof HolderSet.Named<?> tagContents) {
                for (Holder<?> holder : tagContents) {
                    if (holder.value() == state.getBlock()) {
                        isWhitelisted = true;
                        break;
                    }
                }
            }
        }

        // タグもリストも空なら全ブロックOK（ここが抜けてた！）
        if (!useWhitelistTag && !useWhitelistBlocks) {
            isWhitelisted = true;
        }

        // 最終判定
        if (!isWhitelisted || isBlacklisted) {
            return false;  // ホワイトリストにヒットせず、またはブラックリストにヒット
        }

        // ここまで来たら増殖OK
        boolean keepNbt = CrazyEnchantConfig.silkyFortuneKeepNbtCache;

        int bonus = serverLevel.random.nextInt(silkFortuneLevel + 2);
        int count = 1 + bonus;

        ItemStack drop = new ItemStack(state.getBlock().asItem());
        drop.setCount(count);

        if (keepNbt && state.hasBlockEntity()) {
            BlockEntity be = serverLevel.getBlockEntity(pos);
            if (be != null) {
                // 1. クローンアイテムを取得（バニラ標準）
                BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
                Player player = null;
                ItemStack stack = state.getCloneItemStack(hitResult, serverLevel, pos, player);

                // 2. NBT取得（全データ）
                CompoundTag tag = be.saveWithFullMetadata();

                // 3. 不要キー除去（Mekanism風 + スタック対策）
                tag.remove("Pos");           // 座標
                tag.remove("x");
                tag.remove("y");
                tag.remove("z");
                tag.remove("LastUpdate");    // タイムスタンプ
                tag.remove("id");            // ID（再生成される）

                // 4. 空のデータ除去（スタック可能に）
                if (tag.contains("Items")) {
                    ListTag items = tag.getList("Items", 10);
                    if (items.isEmpty()) {
                        tag.remove("Items");
                    }
                }
                if (tag.contains("SpawnData")) {
                    CompoundTag spawnData = tag.getCompound("SpawnData");
                    if (spawnData.isEmpty()) {
                        tag.remove("SpawnData");
                    }
                }

                // 5. タグが空ならコピーしない（スタック完全可能）
                if (!tag.isEmpty()) {
                    drop.getOrCreateTag().put("BlockEntityTag", tag);
                }
            }
        }

        serverLevel.addFreshEntity(new ItemEntity(
                serverLevel,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                drop
        ));

        return true;
    }
}