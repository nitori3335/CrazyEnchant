package nfactory.crazyenchant.handler;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import nfactory.crazyenchant.CrazyEnchant;

import java.util.Map;

@Mod.EventBusSubscriber(modid = CrazyEnchant.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        Map<Enchantment, Integer> enchantmentIntegerMap = EnchantmentHelper.getEnchantments(stack);

        if (enchantmentIntegerMap.isEmpty()) return;

        for (Enchantment enchantment : enchantmentIntegerMap.keySet()) {
            ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);

            if (id != null && id.getNamespace().equals(CrazyEnchant.MODID)) {
                String key = "tooltip." + id.getNamespace() + "." + id.getPath();
                event.getToolTip().add(Component.translatable(key).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }
}
