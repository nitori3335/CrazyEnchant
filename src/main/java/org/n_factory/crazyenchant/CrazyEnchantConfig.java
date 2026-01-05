package org.n_factory.crazyenchant;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Crazyenchant.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CrazyEnchantConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec SPEC;

    // カテゴリフィルタ（ベースプール）
    public static final ForgeConfigSpec.BooleanValue INCLUDE_POSITIVE;
    public static final ForgeConfigSpec.BooleanValue INCLUDE_NEGATIVE;
    public static final ForgeConfigSpec.BooleanValue INCLUDE_NEUTRAL;

    // 基本パラメータ
    public static final ForgeConfigSpec.IntValue DURATION;
    public static final ForgeConfigSpec.IntValue LEVEL;
    public static final ForgeConfigSpec.DoubleValue PROBABILITY;

    // 個別調整
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WHITELIST_EFFECTS;  // 強制追加（カテゴリ外でもOK）
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST_EFFECTS;  // 最終除外

    static {
        BUILDER.comment("CrazyEnchant の設定");

        BUILDER.push("random_effects");

        INCLUDE_POSITIVE = BUILDER.comment("ポジティブ効果を候補に含めるか").define("include_positive", true);
        INCLUDE_NEGATIVE = BUILDER.comment("ネガティブ効果を候補に含めるか").define("include_negative", true);
        INCLUDE_NEUTRAL  = BUILDER.comment("中立効果を候補に含めるか").define("include_neutral", false);

        DURATION = BUILDER.comment("効果時間（ティック、20=1秒）").defineInRange("duration", 200, 20, 12000);
        LEVEL = BUILDER.comment("基本レベル（効果レベル = これ - 1）").defineInRange("level", 1, 0, 10);
        PROBABILITY = BUILDER.comment("1回のヒットごとの適用確率（0.0〜1.0）").defineInRange("probability", 0.5, 0.0, 1.0);

        WHITELIST_EFFECTS = BUILDER.comment("強制的に追加したい効果ID（カテゴリ設定に関係なく候補に入る）")
                .comment("例: minecraft:speed, minecraft:strength")
                .defineList("whitelist_effects", List.of(), o -> o instanceof String);

        BLACKLIST_EFFECTS = BUILDER.comment("必ず除外したい効果ID（ホワイトリスト優先）")
                .comment("例: minecraft:poison, minecraft:weakness")
                .defineList("blacklist_effects", List.of(), o -> o instanceof String);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    // Config項目（変更なし）
    public static final ForgeConfigSpec.BooleanValue STEAL_POSITIVE;
    public static final ForgeConfigSpec.BooleanValue STEAL_NEGATIVE;
    public static final ForgeConfigSpec.BooleanValue STEAL_NEUTRAL;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> STEAL_WHITELIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> STEAL_BLACKLIST;

    // キャッシュ変数（初期値はデフォルト）
    public static boolean stealPositiveCache = true;
    public static boolean stealNegativeCache = true;
    public static boolean stealNeutralCache = false;
    public static List<String> stealWhitelistCache = new ArrayList<>();
    public static List<String> stealBlacklistCache = new ArrayList<>();

    static {
        // BUILDER設定のみ（get()は絶対呼ばない！）
        BUILDER.comment("STEALEFFECT 設定");
        BUILDER.push("steal_effect");

        STEAL_POSITIVE = BUILDER.comment("ポジティブ効果を盗めるか").define("steal_positive", true);
        STEAL_NEGATIVE = BUILDER.comment("ネガティブ効果を盗めるか").define("steal_negative", true);
        STEAL_NEUTRAL  = BUILDER.comment("中立効果を盗めるか").define("steal_neutral", false);

        STEAL_WHITELIST = BUILDER.comment("強制追加の効果ID").defineList("steal_whitelist", List.of(), o -> o instanceof String);
        STEAL_BLACKLIST = BUILDER.comment("絶対除外の効果ID").defineList("steal_blacklist", List.of(), o -> o instanceof String);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    // Configロード完了時にキャッシュ初期化（これでエラー回避）
    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            updateCache();
            Crazyenchant.LOGGER.info("STEALEFFECT Config loaded: positive={}, negative={}, neutral={}",
                    stealPositiveCache, stealNegativeCache, stealNeutralCache);
        }
    }

    // リロード時も更新
    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            updateCache();
            Crazyenchant.LOGGER.info("STEALEFFECT Config reloaded: positive={}, negative={}, neutral={}",
                    stealPositiveCache, stealNegativeCache, stealNeutralCache);
        }
    }

    private static void updateCache() {
        stealPositiveCache = STEAL_POSITIVE.get();
        stealNegativeCache = STEAL_NEGATIVE.get();
        stealNeutralCache  = STEAL_NEUTRAL.get();
        stealWhitelistCache = new ArrayList<>(STEAL_WHITELIST.get());
        stealBlacklistCache = new ArrayList<>(STEAL_BLACKLIST.get());
    }

    // SilkyFortune設定
    public static final ForgeConfigSpec.ConfigValue<String> SILKYFORTUNE_WHITELIST_TAG;
    public static final ForgeConfigSpec.ConfigValue<String> SILKYFORTUNE_BLACKLIST_TAG;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> SILKYFORTUNE_WHITELIST_BLOCKS;
    public static final ForgeConfigSpec.BooleanValue SILKYFORTUNE_KEEP_NBT;

    // キャッシュ（クラッシュ防止）
    public static String silkyFortuneWhitelistTagCache = "";
    public static String silkyFortuneBlacklistTagCache = "";
    public static List<String> silkyFortuneWhitelistBlocksCache = new ArrayList<>();
    public static boolean silkyFortuneKeepNbtCache = true;

    static {
        BUILDER.push("silky_fortune");

        SILKYFORTUNE_WHITELIST_TAG = BUILDER
                .comment("増やせるブロックのタグID（空ならリスト優先）")
                .comment("対象を制限する場合はcrazyenchant:increasable_blocksを入力して")
                .comment("data/crazyenchant/tags/blocks/increasable_blocks.json")
                .comment("に指定したいタグやブロックIDを指定してください")
                .define("whitelist_tag", "");

        SILKYFORTUNE_BLACKLIST_TAG = BUILDER
                .comment("絶対に増やせないブロックのタグID（優先）")
                .comment("対象を制限する場合はcrazyenchant:non_increasable_blocksを入力して")
                .comment("data/crazyenchant/tags/blocks/non_increasable_blocks.json")
                .comment("に指定したいタグやブロックIDを指定してください")
                .define("blacklist_tag", "");

        SILKYFORTUNE_WHITELIST_BLOCKS = BUILDER
                .comment("増やせるブロックIDのホワイトリスト（タグ空時使用）")
                .comment("ここに指定したブロックはブラックリストよりも優先されます")
                .defineList("whitelist_blocks", List.of(), o -> o instanceof String);


        SILKYFORTUNE_KEEP_NBT = BUILDER
                .comment("NBT維持するか（チェスト中身など）")
                .define("keep_nbt", true);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    // Configロード/リロード時にキャッシュ更新（クラッシュ防止）
    @SubscribeEvent
    public static void onConfigChange(ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            silkyFortuneWhitelistTagCache = SILKYFORTUNE_WHITELIST_TAG.get();
            silkyFortuneBlacklistTagCache = SILKYFORTUNE_BLACKLIST_TAG.get();
            silkyFortuneWhitelistBlocksCache = new ArrayList<>((List<String>) SILKYFORTUNE_WHITELIST_BLOCKS.get());
            silkyFortuneKeepNbtCache = SILKYFORTUNE_KEEP_NBT.get();

            Crazyenchant.LOGGER.info("SilkyFortune Config updated");
        }
    }
}