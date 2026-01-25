package nfactory.crazyenchant.mixin.fishing;

import net.minecraft.world.level.storage.loot.entries.LootItem;
import nfactory.crazyenchant.access.IHateContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LootItem.class)
public abstract class LootItemMixin implements IHateContainer {

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
