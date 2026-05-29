package com.elfmcys.yesstevemodel;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.concurrent.atomic.AtomicBoolean;

/** Client-side Fabric API hook registration for the phase-one bootstrap. */
public final class YesSteveModelFabricClientLifecycle {
    private static final AtomicBoolean REGISTERED = new AtomicBoolean();
    private static boolean firstTickLogged;

    private YesSteveModelFabricClientLifecycle() {
    }

    public static void register() {
        if (!REGISTERED.compareAndSet(false, true)) {
            return;
        }

        ClientLifecycleEvents.CLIENT_STARTED.register(client ->
                YesSteveModelFabric.LOGGER.info("OpenYSM Fabric client lifecycle started"));

        ClientLifecycleEvents.CLIENT_STOPPING.register(client ->
                YesSteveModelFabric.LOGGER.info("OpenYSM Fabric client lifecycle stopping"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!firstTickLogged) {
                firstTickLogged = true;
                YesSteveModelFabric.LOGGER.debug("OpenYSM Fabric first client tick observed; full client feature migration is pending");
            }
        });
    }
}
