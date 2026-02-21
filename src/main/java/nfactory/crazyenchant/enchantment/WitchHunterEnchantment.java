package nfactory.crazyenchant.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class WitchHunterEnchantment extends Enchantment {
    public WitchHunterEnchantment() {
        super(
                Rarity.RARE,
                EnchantmentCategory.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 10 + level * 5;
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
