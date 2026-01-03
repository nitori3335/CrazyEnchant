package org.n_factory.crazyenchant;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import org.n_factory.crazyenchant.init.*;

@Mod(Crazyenchant.MODID)
public class Crazyenchant {

    public static final String MODID = "crazyenchant";
    public static final Logger LOGGER = LogUtils.getLogger();

    // DeferredRegister（エンチャント登録だけ残す）
    public Crazyenchant() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // エンチャント登録（ModEnchantmentsから）
        ModEnchantments.ENCHANTMENT.register(modEventBus);

        // イベント登録（必要に応じて）
        MinecraftForge.EVENT_BUS.register(this);

        // Config登録（Configuration版に置き換え済みならここ）
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CrazyEnchantConfig.SPEC, "crazyenchant.toml");

        // 他のDeferredRegisterがあればここに追加（ブロック/アイテムなしなら不要）
    }

    // 必要なら共通セットアップ（例: 初期化ログ）
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("CrazyEnchant loaded!");
    }

    // 他のイベント（例: サーバー開始時ログなど）は任意で残す/削除
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("CrazyEnchant ready on server!");
    }

    // クライアント側イベント（任意）
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("CrazyEnchant client setup complete!");
        }
    }
}