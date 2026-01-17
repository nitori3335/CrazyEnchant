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
}
