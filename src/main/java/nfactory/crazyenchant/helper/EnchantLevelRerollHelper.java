package nfactory.crazyenchant.helper;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import nfactory.crazyenchant.data.manager.FishingEnchantLevelLimit;

import java.util.HashMap;
import java.util.Map;

public class EnchantLevelRerollHelper {

    public static void rerollIfExceeded(ItemStack stack, RandomSource random) {
        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);

        if (enchants.isEmpty()) return;

        boolean changed = false;
        Map<Enchantment, Integer> result = new HashMap<>(enchants);

        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();

            Integer limit = FishingEnchantLevelLimit.getLimit(enchantment);
            if (limit == null) continue;

            if (level > limit) {
                int newLevel = Mth.nextInt(random, 1, limit);
                result.put(enchantment, newLevel);
                changed = true;
            }
        }

        if (changed) {
            EnchantmentHelper.setEnchantments(result, stack);
        }

    }
}
