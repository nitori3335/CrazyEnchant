package nfactory.crazyenchant.mixin.fishing;

import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import nfactory.crazyenchant.CrazyEnchant;
import nfactory.crazyenchant.access.IHateContainer;
import nfactory.crazyenchant.util.LootContextHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer$EntryBase")
public abstract class EntryBaseMixin {

    @Unique
    private static final ThreadLocal<Integer> CURRENT_ENCHANT_LEVEL = ThreadLocal.withInitial(() -> 0);

    @Inject(method = "getWeight", at = @At("RETURN"), cancellable = true)
    private void adjustWeight(float luck, CallbackInfoReturnable<Integer> cir) {
        if (LootContextHelper.getEnchantLevel() <= 0) return;

        int base = cir.getReturnValue();

        try {
            java.lang.reflect.Field outerField = this.getClass().getDeclaredField("this$0");
            outerField.setAccessible(true);
            LootPoolSingletonContainer container = (LootPoolSingletonContainer) outerField.get(this);

            if (!(container instanceof IHateContainer)) {
                return;
            }

            int hate = ((IHateContainer) container).crazyenchant$getHate();
            int level = LootContextHelper.getEnchantLevel();

            int hateWeight = 100 + hate * level;

            if (hateWeight < 0) {
                cir.setReturnValue(0);
            } else {
                cir.setReturnValue(base * hateWeight / 100);
            }

            System.out.println("[CrazyEnchant] LootItem weight adjusted! base=" + base + ", hate=" + hate + ", level=" + level + ", final=" + (base * hateWeight / 100));
        } catch (Exception e) {
            CrazyEnchant.LOGGER.error("[CrazyEnchant] Error adjusting weight", e);
        }
    }
}