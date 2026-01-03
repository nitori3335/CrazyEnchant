package org.n_factory.crazyenchant.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.*;

public class SilkfortuneEnchantment extends Enchantment {

    public SilkfortuneEnchantment() {
        super(
                Rarity.VERY_RARE,
                EnchantmentCategory.DIGGER,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND}
        );
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public int getMaxLevel() {
        return 100;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        if (other == Enchantments.SILK_TOUCH) {
            return false;
        }
        if (other == Enchantments.BLOCK_FORTUNE) {
            return false;
        }
        return super.checkCompatibility(other);
    }
}
