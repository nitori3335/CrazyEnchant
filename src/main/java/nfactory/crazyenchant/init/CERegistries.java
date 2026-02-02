package nfactory.crazyenchant.init;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nfactory.crazyenchant.CrazyEnchant;
import nfactory.crazyenchant.handler.ConfigEventHandler;
import nfactory.crazyenchant.handler.CEReloadLister;

@Mod.EventBusSubscriber(modid = CrazyEnchant.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CERegistries {

    public static void init(IEventBus modEventbus) {
        modEventbus.register(ConfigEventHandler.class);

        MinecraftForge.EVENT_BUS.register(ForgeEvents.class);

        CrazyEnchant.LOGGER.info("CERegistries initialized");
    }

    @Mod.EventBusSubscriber(modid = CrazyEnchant.MODID)
    public static class ForgeEvents {

        @SubscribeEvent
        public static void onAddReloadListener(AddReloadListenerEvent event) {
            event.addListener(new CEReloadLister());
            CrazyEnchant.LOGGER.info("CEReloadListener registered");
        }
    }
}
