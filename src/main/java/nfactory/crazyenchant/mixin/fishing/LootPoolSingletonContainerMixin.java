package nfactory.crazyenchant.mixin.fishing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import nfactory.crazyenchant.CrazyEnchant;
import nfactory.crazyenchant.access.IHateContainer;
import nfactory.crazyenchant.init.ModEnchantments;
import nfactory.crazyenchant.util.LootContextHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(LootPoolSingletonContainer.class)
public abstract class LootPoolSingletonContainerMixin implements IHateContainer {

    @Unique
    private int crazyenchant$hate;

    @Override
    public int crazyenchant$getHate() {
        return this.crazyenchant$hate;
    }

    @Override
    public void crazyenchant$setHate(int hate) {
        this.crazyenchant$hate = hate;
    }

    @Inject(method = "expand", at = @At("HEAD"))
    private void captureEnchantmentLevel(LootContext lootContext, Consumer<LootPoolEntry> consumer, CallbackInfoReturnable<Boolean> cir) {
        try {
            ResourceLocation tableId = lootContext.getQueriedLootTableId();
            if (!tableId.getPath().startsWith("gameplay/fishing")) {
                LootContextHelper.setEnchantLevel(0);
                return;
            }

            // TOOL (釣竿) から直接エンチャントレベルを取得
            ItemStack tool = lootContext.getParamOrNull(LootContextParams.TOOL);
            if (tool != null) {
                int level = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.FISHHATER.get(), tool);
                LootContextHelper.setEnchantLevel(level);
                System.out.println("[CrazyEnchant] Captured enchant level from TOOL: " + level);
            } else {
                LootContextHelper.setEnchantLevel(0);
            }
        } catch (Exception e) {
            LootContextHelper.setEnchantLevel(0);
            CrazyEnchant.LOGGER.error("[CrazyEnchant] Error capturing enchantment level", e);
        }
    }
}
