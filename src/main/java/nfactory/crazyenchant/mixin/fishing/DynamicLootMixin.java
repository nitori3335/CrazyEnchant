package nfactory.crazyenchant.mixin.fishing;

import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import nfactory.crazyenchant.access.IHateContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DynamicLoot.class)
public abstract class DynamicLootMixin implements IHateContainer {

    @Unique
    private int crazyenchant$hate;


    @Override
    public int crazyenchant$getHate() {
        return crazyenchant$hate;
    }

    @Override
    public void crazyenchant$setHate(int hate) {
        crazyenchant$hate = hate;
    }
}
