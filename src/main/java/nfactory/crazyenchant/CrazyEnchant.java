package nfactory.crazyenchant;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nfactory.crazyenchant.init.ModEnchantments;
import org.slf4j.Logger;


@Mod(CrazyEnchant.MODID)
public class CrazyEnchant {
    public static final String MODID = "crazyenchant";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CrazyEnchant() {
        IEventBus modEventbus = FMLJavaModLoadingContext.get().getModEventBus();


        ModEnchantments.ENCHANTMENT.register(modEventbus);
    }
}
