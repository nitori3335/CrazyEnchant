package nfactory.crazyenchant.init;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import nfactory.crazyenchant.config.CrazyEnchantConfig;

public class CEConfig {

    @SuppressWarnings("removal")
    public static void register() {
        ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON,
                CrazyEnchantConfig.SPEC,
                "crazyenchant.toml"
        );
    }
}
