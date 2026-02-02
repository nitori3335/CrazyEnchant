package nfactory.crazyenchant.handler;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nfactory.crazyenchant.CrazyEnchant;
import nfactory.crazyenchant.helper.EnchantLevelRerollHelper;

@Mod.EventBusSubscriber(modid = CrazyEnchant.MODID)
public class FishingEnchantHandler {

    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent event) {
        if (event.getEntity().level().isClientSide) return;

        for (ItemStack stack : event.getDrops()) {
            EnchantLevelRerollHelper.rerollIfExceeded(stack, event.getEntity().getRandom());
        }
    }
}
