package nfactory.crazyenchant.procedure;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nfactory.crazyenchant.CrazyEnchant;
import nfactory.crazyenchant.config.CrazyEnchantConfig;
import nfactory.crazyenchant.data.dto.EffectFilterRule;
import nfactory.crazyenchant.data.dto.StolenEffectData;
import nfactory.crazyenchant.data.result.StealResult;
import nfactory.crazyenchant.helper.DamageSourceHelper;
import nfactory.crazyenchant.helper.StealEffectHelper;
import nfactory.crazyenchant.init.ModEnchantments;
import nfactory.crazyenchant.util.CombatUtil;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = CrazyEnchant.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RandomEffectStealAttackProcedure {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity attacker = CombatUtil.getDirectMeleeAttacker(event.getSource(), event.getEntity().level());
        if (attacker == null) return;

        if (DamageSourceHelper.EXTRA_HIT_MSG_ID.equals(event.getSource().getMsgId())) return;

        LivingEntity victim = event.getEntity();

        ItemStack weapon = attacker.getMainHandItem();
        int level = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.STEALEFFECT.get(), weapon);

        if (level <= 0) return;

        execute(attacker, victim, level);
    }

    private static void execute(LivingEntity attacker, LivingEntity victim, int enchantLevel) {

        List<MobEffectInstance> victimEffect = new ArrayList<>(victim.getActiveEffects());
        List<MobEffectInstance> attackerEffect = new ArrayList<>(attacker.getActiveEffects());

        if (victimEffect.isEmpty()) return;

        EffectFilterRule filterRule = new EffectFilterRule(
                CrazyEnchantConfig.StealEffect.stealPositiveCache,
                CrazyEnchantConfig.StealEffect.stealNegativeCache,
                CrazyEnchantConfig.StealEffect.stealNeutralCache,
                CrazyEnchantConfig.StealEffect.stealWhitelistCache,
                CrazyEnchantConfig.StealEffect.stealBlacklistCache
        );

        StealResult result = StealEffectHelper.process(attackerEffect, victimEffect, enchantLevel, filterRule);

        if (!result.isAnyStolen())  return;

        for (StolenEffectData data : result.getStolenEffects()) {

            if (data.removeFromVictim()) {
                victim.removeEffect(data.effect());
            }

            MobEffectInstance newEffect = new MobEffectInstance(data.effect(), data.duration(), data.amplifier());

            attacker.addEffect(newEffect);
        }
    }
}
