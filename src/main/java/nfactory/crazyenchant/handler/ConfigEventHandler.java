package nfactory.crazyenchant.handler;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import nfactory.crazyenchant.CrazyEnchant;
import nfactory.crazyenchant.config.CrazyEnchantConfig;

public class ConfigEventHandler {

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == CrazyEnchantConfig.SPEC) {
            CrazyEnchantConfig.StealEffect.updateCache();
        }
        CrazyEnchant.LOGGER.info("Config loaded: {}", event.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == CrazyEnchantConfig.SPEC) {
            CrazyEnchantConfig.StealEffect.updateCache();
            CrazyEnchantConfig.StealEffect.validateConflict(true);
        }
        CrazyEnchant.LOGGER.info("Config reloaded: {}", event.getConfig().getFileName());
    }
}
