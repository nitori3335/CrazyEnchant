package org.n_factory.crazyenchant;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.n_factory.crazyenchant.config.MinionConfig;
import org.n_factory.crazyenchant.init.ModBlocks;
import org.n_factory.crazyenchant.init.ModEnchantments;
import org.n_factory.crazyenchant.init.ModEntities;
import org.n_factory.crazyenchant.ritual.Ritualkey;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Mod(Crazyenchant.MODID)
public class Crazyenchant {

    public static final String MODID = "crazyenchant";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Random RANDOM = new Random();

    public static final Map<Ritualkey.RitualKey, Boolean> ACTIVE_RITUALS = new ConcurrentHashMap<>();

    // DeferredRegister（エンチャント登録だけ残す）
    public Crazyenchant() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        // 各種登録（initから）
        ModEnchantments.ENCHANTMENT.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);

        // Config登録（Configuration版に置き換え済みならここ）
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CrazyEnchantConfig.SPEC, "crazyenchant.toml");
    }

    // 他のイベント（例: サーバー開始時ログなど）は任意で残す/削除
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("CrazyEnchant ready on server!");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // ★ ここで直接同期実行！（非同期じゃないのでファイルI/O確実に動く）
        MinionConfig.load();
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