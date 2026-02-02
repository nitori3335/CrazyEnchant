package nfactory.crazyenchant.handler;

import net.minecraft.server.packs.resources.ResourceManager;
import nfactory.crazyenchant.data.FishingEnchantLevelLimit;

public class JsonEventHandler {

    public static void reload(ResourceManager manager) {
        FishingEnchantLevelLimit.reload(manager);
    }
}
