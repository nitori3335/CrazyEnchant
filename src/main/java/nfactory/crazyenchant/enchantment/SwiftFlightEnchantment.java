package nfactory.crazyenchant.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SwiftFlightEnchantment extends Enchantment {
    public SwiftFlightEnchantment() {
        super(
                Enchantment.Rarity.COMMON,
                EnchantmentCategory.ARMOR_CHEST,
                new EquipmentSlot[]{EquipmentSlot.CHEST}
        );
    }

    @Override
    public int getMinCost(int level) {
        return 1 + level * 5;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 5;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}