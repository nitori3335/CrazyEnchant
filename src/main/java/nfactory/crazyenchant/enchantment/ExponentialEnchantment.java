package nfactory.crazyenchant.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ExponentialEnchantment extends Enchantment {

    public ExponentialEnchantment() {
        super(
                Rarity.VERY_RARE,
                EnchantmentCategory.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND}
        );
    }

    @Override
    public int getMaxLevel() { return 5; }

    @Override
    public boolean isTradeable() { return false; }

    @Override
    public boolean isTreasureOnly() { return true; }
}
