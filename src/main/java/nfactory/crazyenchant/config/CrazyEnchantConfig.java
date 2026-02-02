package nfactory.crazyenchant.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import nfactory.crazyenchant.CrazyEnchant;

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

        SPEC = BUILDER.build();

    }
}
