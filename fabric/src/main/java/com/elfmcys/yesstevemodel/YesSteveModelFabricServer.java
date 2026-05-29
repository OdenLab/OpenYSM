package com.elfmcys.yesstevemodel;

import net.fabricmc.api.DedicatedServerModInitializer;

/** Dedicated-server Fabric entrypoint for server-side migration hooks. */
public final class YesSteveModelFabricServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        YesSteveModelFabricConfig.Snapshot snapshot = YesSteveModelFabric.getConfig();
        if (snapshot == null) {
            YesSteveModelFabric.LOGGER.warn("OpenYSM Fabric server entrypoint loaded before config bootstrap completed");
            return;
        }
        YesSteveModelFabric.LOGGER.info("OpenYSM Fabric dedicated server model root: {}", snapshot.modelRoot().toAbsolutePath().normalize());
    }
}
