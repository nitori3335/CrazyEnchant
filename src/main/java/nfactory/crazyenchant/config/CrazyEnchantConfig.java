package nfactory.crazyenchant.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import nfactory.crazyenchant.CrazyEnchant;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = CrazyEnchant.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CrazyEnchantConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec SPEC;

    public static class RandomEffect {

        // category filter
        public static ForgeConfigSpec.BooleanValue INCLUDE_POSITIVE;
        public static ForgeConfigSpec.BooleanValue INCLUDE_NEGATIVE;
        public static ForgeConfigSpec.BooleanValue INCLUDE_NEUTRAL;

        // base parameter
        public static ForgeConfigSpec.IntValue DURATION;
        public static ForgeConfigSpec.IntValue LEVEL;
        public static ForgeConfigSpec.DoubleValue PROBABILITY;

        // individual adjustment
        public static ForgeConfigSpec.ConfigValue<List<? extends String>> WHITELIST_EFFECTS;
        public static ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST_EFFECTS;
    }

    public static class SilkFortune {

        // tag filter
        public static ForgeConfigSpec.ConfigValue<String> WHITELIST_TAG;
        public static ForgeConfigSpec.ConfigValue<String> BLACKLIST_TAG;

        // individual adjustment
        public static ForgeConfigSpec.ConfigValue<List<? extends String>> WHITELIST_BLOCKS;

        // nbt
        public static ForgeConfigSpec.BooleanValue KEEP_NBT;

        // cache
        public static String whitelistTagCache = "";
        public static String blacklistTagCache = "";
        public static List<String> whitelistBlocksCache = new ArrayList<>();
        public static boolean keepNbtCache = false;

        // reload
        public static void reloadCache() {
            whitelistTagCache = WHITELIST_TAG.get();
            blacklistTagCache = BLACKLIST_TAG.get();
            whitelistBlocksCache = new ArrayList<>(WHITELIST_BLOCKS.get());
            keepNbtCache = KEEP_NBT.get();
        }
    }

    static {
        BUILDER.comment("CrazyEnchant configuration");

        BUILDER.push("random_effects");

        RandomEffect.INCLUDE_POSITIVE = BUILDER.comment("Whether positive effects can be selected").define("include_positive_effect", true);
        RandomEffect.INCLUDE_NEGATIVE = BUILDER.comment("Whether negative effects can be selected").define("include_negative_effect", true);
        RandomEffect.INCLUDE_NEUTRAL = BUILDER.comment("Whether neutral effects can be selected").define("include_neutral_effect", false);

        RandomEffect.DURATION = BUILDER.comment("Effect duration in tick (20 ticks = 1 second)").defineInRange("duration", 200, 20, 12000);
        RandomEffect.LEVEL = BUILDER.comment("Base effect level").defineInRange("level", 1, 1, 10);
        RandomEffect.PROBABILITY = BUILDER.comment("Chance to apply an effect per hit").defineInRange("probability", 0.5, 0.0, 1.0);

        RandomEffect.WHITELIST_EFFECTS = BUILDER.comment("Effect IDs that are always allowed")
                .comment("These will be included regardless of category setting")
                .comment("Example: minecraft:speed, minecraft:strength")
                .defineList("whitelist_effects", List.of(), o -> o instanceof String);
        RandomEffect.BLACKLIST_EFFECTS = BUILDER.comment("Effect IDs that are always excluded")
                .comment("Blacklist has priority over whitelist")
                .comment("Example: minecraft:poison, minecraft:weakness")
                .defineList("blacklist_effects", List.of(), o -> o instanceof String);

        BUILDER.pop();

        BUILDER.push("silky_fortune");

        SilkFortune.WHITELIST_TAG = BUILDER
                .comment("Tag ID of blocks that can be increased by Silky Fortune (optional)")
                .comment("If you want to restrict targets using a tag, set for example: crazyenchant:increasable_blocks")
                .comment("Then define the tag contents in:")
                .comment("data/minecraft/tags/blocks/increasable_blocks.json")
                .define("whitelist_tag", "");

        SilkFortune.BLACKLIST_TAG = BUILDER
                .comment("Tag ID of blocks that must NEVER be increased (highest priority)")
                .comment("If you want to block specific blocks via tag, set for example: crazyenchant:non_increasable_blocks")
                .comment("Then define the tag contents in:")
                .comment("data/minecraft/tags/blocks/non_increasable_blocks.json")
                .define("blacklist_tag", "");

        SilkFortune.WHITELIST_BLOCKS = BUILDER
                .comment("Direct whitelist of block IDs that can be increased")
                .comment("This list has HIGHER priority than the blacklist tag")
                .comment("Format example: [\"minecraft:spawner\", \"create:mechanical_drill\"]")
                .defineList("whitelist_blocks", List.of(), o -> o instanceof String);

        SilkFortune.KEEP_NBT = BUILDER
                .comment("Preserve BlockEntity NBT using saveToItem().")
                .comment("May duplicate inventories and break stacking.")
                .comment("Disable for vanilla-like behavior and better mod compatibility.")
                .define("keep_nbt", false);

        BUILDER.pop();


        SPEC = BUILDER.build();

    }
}
