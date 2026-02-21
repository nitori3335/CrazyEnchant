package nfactory.crazyenchant.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class MultihitsEnchantment extends Enchantment {

    public MultihitsEnchantment() {
        super(
                Rarity.VERY_RARE,
                EnchantmentCategory.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND}
        );
    }

    @Override
    public int getMinCost(int level) {
        if (level <= 5) {
            return 5 + level * 10;
        } else {
            return 1024;
        }
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 5;
    }

    @Override
    public int getMaxLevel() {
        return 100;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

}
