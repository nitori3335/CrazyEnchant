package nfactory.crazyenchant.handler;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

public class CEReloadLister implements ResourceManagerReloadListener {

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager manager) {
        JsonEventHandler.reload(manager);
    }
}
