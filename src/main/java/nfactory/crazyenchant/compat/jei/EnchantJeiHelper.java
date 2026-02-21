package nfactory.crazyenchant.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class EnchantJeiHelper {

    public static void registerAll(IRecipeRegistration registration, String modid) {

        for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS.getValues()) {

            ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
            if (id == null) continue;
            if (!id.getNamespace().equals(modid)) continue;

            String baseKey = "jei." + id.getNamespace() + "." + id.getPath();

            List<Component> list = new ArrayList<>();

            for (int i = 1; i <= 4; i++) {
                String key = baseKey + ".line" + i;

                if (!I18n.exists(key)) break;

                list.add(Component.translatable(key));
            }

            String configKey = baseKey + ".config";
            if (I18n.exists(configKey)) {
                list.add(Component.translatable(configKey).withStyle(ChatFormatting.BLUE));
            }

            if (list.isEmpty()) continue;

            Component[] components = list.toArray(Component[]::new);

            for (int level = 1; level <= enchantment.getMaxLevel(); level++ ) {
                registration.addIngredientInfo(
                        EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, level)),
                        VanillaTypes.ITEM_STACK,
                        components
                );
            }
        }
    }
}
