package org.n_factory.crazyenchant.procedures;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.n_factory.crazyenchant.init.*;
import org.n_factory.crazyenchant.*;

@Mod.EventBusSubscriber(modid = Crazyenchant.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RandomEffectStealAttackProcedure {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        if ("extra_hit".equals(event.getSource().getMsgId())) return;

        Entity source = event.getSource().getEntity();
        if (!(source instanceof LivingEntity attacker)) return;

        LivingEntity victim = event.getEntity();

        ItemStack weapon = attacker.getMainHandItem();
        int level = EnchantmentHelper.getItemEnchantmentLevel(
               ModEnchantments.STEALEFFECT.get(), weapon);

        if (level <= 0) return;

        execute(attacker, victim, level);
    }

    private static void execute(LivingEntity attacker, LivingEntity victim, int level) {
        List<MobEffectInstance> effects = new ArrayList<>(victim.getActiveEffects());

        if (effects.isEmpty()) return;

        Collections.shuffle(effects);

        int stealCount = Math.min(level, effects.size());

        Set<MobEffect> stolen = new HashSet<>();

        // キャッシュ値を使う（.get()を呼ばない）
        boolean allowPositive = CrazyEnchantConfig.stealPositiveCache;
        boolean allowNegative = CrazyEnchantConfig.stealNegativeCache;
        boolean allowNeutral  = CrazyEnchantConfig.stealNeutralCache;

        List<String> whitelist = CrazyEnchantConfig.stealWhitelistCache;
        List<String> blacklist = CrazyEnchantConfig.stealBlacklistCache;

        boolean useWhitelist = !whitelist.isEmpty();

        for (int i = 0; i < stealCount && !effects.isEmpty(); ) {
            MobEffectInstance original = effects.get(i);
            MobEffect effect = original.getEffect();
            String idStr = ForgeRegistries.MOB_EFFECTS.getKey(effect).toString();

            // ブラックリスト
            if (blacklist.contains(idStr)) {
                effects.remove(i);
                continue;
            }

            // ホワイトリスト優先
            boolean forceSteal = useWhitelist && whitelist.contains(idStr);

            // カテゴリチェック
            boolean canSteal = forceSteal ||
                    (effect.isBeneficial() && allowPositive) ||
                    (!effect.isBeneficial() && allowNegative) ||
                    (!effect.isBeneficial() && allowNeutral);

            if (!canSteal) {
                effects.remove(i);
                continue;
            }

            if (stolen.contains(effect)) {
                effects.remove(i);
                continue;
            }
            stolen.add(effect);

            MobEffectInstance copied = new MobEffectInstance(
                    original.getEffect(),
                    original.getDuration(),
                    original.getAmplifier(),
                    original.isAmbient(),
                    original.isVisible(),
                    original.showIcon()
            );

            attacker.addEffect(copied);
            victim.removeEffect(effect);

            victim.invulnerableTime = 0;

            attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                    SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.8f, 1.0f);

            i++;
        }
    }
}