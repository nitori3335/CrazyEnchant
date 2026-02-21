package nfactory.crazyenchant;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nfactory.crazyenchant.init.CEConfigRegister;
import nfactory.crazyenchant.init.CERegistries;
import nfactory.crazyenchant.init.ModEnchantments;
import nfactory.crazyenchant.init.ModModifiers;
import org.slf4j.Logger;

@SuppressWarnings("removal")
@Mod(CrazyEnchant.MODID)
public class CrazyEnchant {
    public static final String MODID = "crazyenchant";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CrazyEnchant() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEnchantments.ENCHANTMENT.register(modEventBus);
        ModModifiers.MODIFIERS.register(modEventBus);

        CERegistries.init(modEventBus);
        CEConfigRegister.register();
    }
}
