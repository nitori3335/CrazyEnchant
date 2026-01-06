package org.n_factory.crazyenchant.client.init;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.n_factory.crazyenchant.Crazyenchant;
import org.n_factory.crazyenchant.client.renderer.SummoningGateRenderer;
import org.n_factory.crazyenchant.init.ModEntities;

@Mod.EventBusSubscriber(modid = Crazyenchant.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Renderer登録
            EntityRenderers.register(ModEntities.SUMMONING_GATE.get(), SummoningGateRenderer::new);
        });
    }
}