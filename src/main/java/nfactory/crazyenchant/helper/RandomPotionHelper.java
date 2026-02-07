package nfactory.crazyenchant.helper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import nfactory.crazyenchant.config.CrazyEnchantConfig;

import java.util.*;

@SuppressWarnings("removal")
public class RandomPotionHelper {

    private static final Random RANDOM = new Random();

    public static void applyRandomEffect(LivingEntity target, int rolls) {
        if (target == null || rolls <= 0) return;

        // Config値取得
        boolean includePositive = CrazyEnchantConfig.RandomEffect.INCLUDE_POSITIVE.get();
        boolean includeNegative = CrazyEnchantConfig.RandomEffect.INCLUDE_NEGATIVE.get();
        boolean includeNeutral  = CrazyEnchantConfig.RandomEffect.INCLUDE_NEUTRAL.get();

        int duration = CrazyEnchantConfig.RandomEffect.DURATION.get();
        int baseLevel = CrazyEnchantConfig.RandomEffect.LEVEL.get();
        double probability = CrazyEnchantConfig.RandomEffect.PROBABILITY.get();

        List<String> whitelist = new ArrayList<>(CrazyEnchantConfig.RandomEffect.WHITELIST_EFFECTS.get());
        List<String> blacklist = new ArrayList<>(CrazyEnchantConfig.RandomEffect.BLACKLIST_EFFECTS.get());

        int amplifier = Math.max(0, baseLevel - 1);

        // 現在適用中の効果（重複防止）
        Set<MobEffect> activeEffects = new HashSet<>();
        for (MobEffectInstance e : target.getActiveEffects()) {
            activeEffects.add(e.getEffect());
        }

        // ステップ1: カテゴリでベース候補を作成
        List<MobEffect> candidates = new ArrayList<>();

        for (MobEffect effect : ForgeRegistries.MOB_EFFECTS) {
            if (activeEffects.contains(effect)) continue;

            ResourceLocation id = ForgeRegistries.MOB_EFFECTS.getKey(effect);
            if (id == null) continue;

            String idStr = id.toString();

            // ブラックリストにあれば除外（ホワイトリスト優先のため後で処理）
            if (blacklist.contains(idStr)) continue;

            // カテゴリチェック（ベースプール）
            if ((effect.isBeneficial() && includePositive) ||
                    (!effect.isBeneficial() && includeNegative) ||
                    (!effect.isBeneficial() && includeNeutral)) {
                candidates.add(effect);
            }
        }

        // ステップ2: ホワイトリストの効果を強制追加（重複チェック済み）
        for (String idStr : whitelist) {
            ResourceLocation loc = new ResourceLocation(idStr);
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(loc);

            if (effect != null && !activeEffects.contains(effect)) {
                candidates.add(effect);
            }
        }

        // 候補が空なら終了
        if (candidates.isEmpty()) return;

        // ステップ3: ロール回数分適用（確率判定あり）
        for (int i = 0; i < rolls && !candidates.isEmpty(); i++) {
            if (RANDOM.nextDouble() > probability) continue;

            int index = RANDOM.nextInt(candidates.size());
            MobEffect selected = candidates.remove(index);

            target.addEffect(new MobEffectInstance(selected, duration, amplifier));
        }
    }
}