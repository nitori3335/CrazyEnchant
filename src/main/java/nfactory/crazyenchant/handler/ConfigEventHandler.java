package nfactory.crazyenchant.handler;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import nfactory.crazyenchant.CrazyEnchant;

public class ConfigEventHandler {

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        CrazyEnchant.LOGGER.info("Config loaded: {}", event.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        CrazyEnchant.LOGGER.info("Config reloaded: {}", event.getConfig().getFileName());
    }
}
