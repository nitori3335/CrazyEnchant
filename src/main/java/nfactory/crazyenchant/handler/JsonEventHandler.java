package nfactory.crazyenchant.handler;

import net.minecraft.server.packs.resources.ResourceManager;
import nfactory.crazyenchant.data.manager.FishingEnchantLevelLimit;
import nfactory.crazyenchant.data.context.SilkyFortuneContext;

public class JsonEventHandler {

    public static void reload(ResourceManager manager) {
        SilkyFortuneContext.rebuild();
        FishingEnchantLevelLimit.reload(manager);
    }
}
