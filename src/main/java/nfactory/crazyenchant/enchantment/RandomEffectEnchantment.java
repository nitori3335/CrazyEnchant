package nfactory.crazyenchant.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class RandomEffectEnchantment extends Enchantment {

    public RandomEffectEnchantment() {
        super(
                Rarity.RARE,
                EnchantmentCategory.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND}
        );
    }

    @Override
    public int getMinCost(int level) {
        if (level <= 5) {
            return level * 6;
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
}
