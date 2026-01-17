package nfactory.crazyenchant;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nfactory.crazyenchant.init.ModEnchantments;

@Mod(CrazyEnchant.MODID)
public class CrazyEnchant {
    public static final String MODID = "crazyenchant";

    public CrazyEnchant() {
        IEventBus modEventbus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEnchantments.ENCHANTMENT.register(modEventbus);
    }
}
