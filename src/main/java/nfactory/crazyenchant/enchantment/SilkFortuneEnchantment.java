package nfactory.crazyenchant.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

public class SilkFortuneEnchantment extends Enchantment {

    public SilkFortuneEnchantment() {
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
    protected boolean checkCompatibility(@NotNull Enchantment other) {
        if (other == Enchantments.SILK_TOUCH) {
            return false;
        }
        if (other == Enchantments.BLOCK_FORTUNE) {
            return false;
        }
        return super.checkCompatibility(other);
    }
}