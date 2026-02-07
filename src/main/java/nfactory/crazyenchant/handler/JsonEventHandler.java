package nfactory.crazyenchant.handler;

import net.minecraft.server.packs.resources.ResourceManager;
import nfactory.crazyenchant.data.FishingEnchantLevelLimit;
import nfactory.crazyenchant.data.SilkyFortuneContext;

public class JsonEventHandler {

    public static void reload(ResourceManager manager) {
        SilkyFortuneContext.rebuild();
        FishingEnchantLevelLimit.reload(manager);
    }
}
