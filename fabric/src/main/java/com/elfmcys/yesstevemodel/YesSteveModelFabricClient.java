package com.elfmcys.yesstevemodel;

import net.fabricmc.api.ClientModInitializer;

/** Client-side Fabric entrypoint. */
public final class YesSteveModelFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        YesSteveModelFabricClientLifecycle.register();
        YesSteveModelFabric.LOGGER.info("OpenYSM Fabric client bootstrap loaded");
    }
}
