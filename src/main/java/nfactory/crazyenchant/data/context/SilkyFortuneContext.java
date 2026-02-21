package nfactory.crazyenchant.data.context;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import nfactory.crazyenchant.CrazyEnchant;
import nfactory.crazyenchant.config.CrazyEnchantConfig;
import org.jetbrains.annotations.Nullable;

public class SilkyFortuneContext {

    public static TagKey<Block> WHITELIST_TAG = null;
    public static TagKey<Block> BLACKLIST_TAG = null;

    public static void rebuild() {
        CrazyEnchantConfig.SilkFortune.reloadCache();
        CrazyEnchant.LOGGER.info("[Silky] ===== rebuild start =====");
        CrazyEnchant.LOGGER.info("[Silky] whitelistTagCache = {}", CrazyEnchantConfig.SilkFortune.whitelistTagCache);
        CrazyEnchant.LOGGER.info("[Silky] blacklistTagCache = {}", CrazyEnchantConfig.SilkFortune.blacklistTagCache);

        CrazyEnchant.LOGGER.info("[SilkFortune] Reload Cache: keepNBT = {}, whitelist = {}",
                CrazyEnchantConfig.SilkFortune.keepNbtCache,
                CrazyEnchantConfig.SilkFortune.whitelistBlocksCache.size()
        );

        WHITELIST_TAG = parseTag(CrazyEnchantConfig.SilkFortune.whitelistTagCache);
        BLACKLIST_TAG = parseTag(CrazyEnchantConfig.SilkFortune.blacklistTagCache);

        CrazyEnchant.LOGGER.info("[Silky] WHITELIST_TAG = {}", WHITELIST_TAG);
        CrazyEnchant.LOGGER.info("[Silky] BLACKLIST_TAG = {}", BLACKLIST_TAG);
        CrazyEnchant.LOGGER.info("[Silky] ===== rebuild end =====");
    }

    private static @Nullable TagKey<Block> parseTag(String id) {
        if (id.isEmpty()) return null;

        try {
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(CrazyEnchant.MODID, id);

            CrazyEnchant.LOGGER.info("[Silky] parse Tag -> {}", loc);

            return TagKey.create(Registries.BLOCK, loc);
        }  catch (Exception e) {
            CrazyEnchant.LOGGER.warn("[Silky] Invalid tag id: {}", id);
            return null;
        }

    }

    public static boolean canApply(BlockState state, Level level) {
        var whitelistBlocksCache = CrazyEnchantConfig.SilkFortune.whitelistBlocksCache;

        ResourceLocation key = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (key == null) return false;

        String id = key.toString();

        boolean useWhitelistTag = WHITELIST_TAG != null;
        boolean useBlacklistTag = BLACKLIST_TAG != null;
        boolean useWhitelistBlocks = !whitelistBlocksCache.isEmpty();

        boolean isWhitelisted = useWhitelistBlocks && whitelistBlocksCache.contains(id);
        boolean isBlacklisted = false;


        if (!isWhitelisted && useBlacklistTag) {
            var tag = level.registryAccess().registryOrThrow(Registries.BLOCK).getTagOrEmpty(BLACKLIST_TAG);

            for (var holder : tag) {
                if (holder.value() == state.getBlock()) {
                    isBlacklisted = true;
                    break;
                }
            }
        }

        if (!isWhitelisted && !isBlacklisted && useWhitelistTag) {
            var tag = level.registryAccess().registryOrThrow(Registries.BLOCK).getTagOrEmpty(WHITELIST_TAG);

            for (var holder : tag) {
                if (holder.value() == state.getBlock()) {
                    isWhitelisted = true;
                    break;
                }
            }
        }

        if (!useWhitelistTag && !useWhitelistBlocks) {
            isWhitelisted = true;
        }

        return isWhitelisted && !isBlacklisted;
    }
}
