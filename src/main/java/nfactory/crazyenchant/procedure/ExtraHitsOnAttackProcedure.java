package nfactory.crazyenchant.procedure;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nfactory.crazyenchant.init.ModEnchantments;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber
public class ExtraHitsOnAttackProcedure {

    private static final String EXTRA_HIT_MSG_ID = "extra_hit";  // 共通のMsgId

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        // 追加ヒットはスキップ（MsgIdで判定 → 他のMODからも参照可能）
        if (EXTRA_HIT_MSG_ID.equals(event.getSource().getMsgId())) return;

        Entity attackerEntity = event.getSource().getEntity();
        if (!(attackerEntity instanceof LivingEntity attacker)) return;

        ItemStack weapon = attacker.getMainHandItem();
        int level = weapon.getEnchantmentLevel(ModEnchantments.MULTIHITS.get());
        if (level <= 1) return;

        float baseDamage = event.getAmount();
        int extraHits = Math.min(level - 1, 100);

        LivingEntity target = event.getEntity();

        for (int i = 0; i < extraHits; i++) {
            target.invulnerableTime = 0;

            // カスタムDamageSource（MsgIdを維持）
            DamageSource extra = new CustomExtraDamageSource(attacker);

            target.hurt(extra, baseDamage);
        }
    }

    // カスタムDamageSource（MsgIdを固定）
    private static class CustomExtraDamageSource extends DamageSource {
        public CustomExtraDamageSource(LivingEntity attacker) {
            super(attacker.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypes.GENERIC), attacker);
        }

        @Override
        public @NotNull String getMsgId() {
            return EXTRA_HIT_MSG_ID;  // ← ここで固定！ 他のクラスからも参照可能
        }
    }
}
