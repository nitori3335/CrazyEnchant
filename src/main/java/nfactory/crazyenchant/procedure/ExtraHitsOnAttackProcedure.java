package nfactory.crazyenchant.procedure;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nfactory.crazyenchant.helper.DamageSourceHelper;
import nfactory.crazyenchant.init.ModEnchantments;
import nfactory.crazyenchant.util.CombatUtil;

@Mod.EventBusSubscriber
public class ExtraHitsOnAttackProcedure {

    private static final String EXTRA_HIT_MSG_ID = "extra_hit";  // 共通のMsgId

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity attacker = CombatUtil.getDirectMeleeAttacker(event.getSource(), event.getEntity().level());
        if (attacker == null) return;
        if (!(CombatUtil.didDamage(event))) return;

        // 追加ヒットはスキップ（MsgIdで判定 → 他のMODからも参照可能）
        if (EXTRA_HIT_MSG_ID.equals(event.getSource().getMsgId())) return;

        ItemStack weapon = attacker.getMainHandItem();
        int level = weapon.getEnchantmentLevel(ModEnchantments.MULTIHITS.get());
        if (level <= 1) return;

        float baseDamage = event.getAmount();
        int extraHits = Math.min(level - 1, 100);

        LivingEntity target = event.getEntity();

        for (int i = 0; i < extraHits; i++) {
            target.invulnerableTime = 0;

            DamageSource extra = DamageSourceHelper.extraHit(attacker);
            target.hurt(extra, baseDamage);
        }
    }
}
