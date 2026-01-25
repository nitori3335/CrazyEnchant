package nfactory.crazyenchant.mixin.fishing;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import nfactory.crazyenchant.access.IHateContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LootPoolSingletonContainer.Serializer.class)
public class LootPoolSingletonContainerSerializerMixin {
    @Inject(method = "deserializeCustom(Lcom/google/gson/JsonObject;Lcom/google/gson/JsonDeserializationContext;[Lnet/minecraft/world/level/storage/loot/predicates/LootItemCondition;)Lnet/minecraft/world/level/storage/loot/entries/LootPoolSingletonContainer;", at = @At("TAIL"))
    private void readHate(JsonObject json, JsonDeserializationContext ctx, LootItemCondition[] conditions, CallbackInfoReturnable<LootPoolSingletonContainer> cir) {
        LootPoolSingletonContainer container = cir.getReturnValue();

        int hate = GsonHelper.getAsInt(json, "hate", 0);

        ((IHateContainer) container).crazyenchant$setHate(hate);
    }
}
