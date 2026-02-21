package nfactory.crazyenchant.mixin.fishing;

import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import nfactory.crazyenchant.access.IHateContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LootPoolEntryContainer.class)
public abstract class LootPoolEntryContainerMixin implements IHateContainer {
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
}
