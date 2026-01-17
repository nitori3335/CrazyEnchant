package nfactory.crazyenchant.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class FishHaterEnchantment extends Enchantment {

    public FishHaterEnchantment() {
        super(
                Rarity.COMMON,
                EnchantmentCategory.FISHING_ROD,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND}
        );
    }

    @Override
    public int getMinCost(int level) {
        return 1 + level * 5;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 4;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
