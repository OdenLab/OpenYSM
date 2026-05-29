package com.elfmcys.yesstevemodel;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Fabric API lifecycle glue for the phase-one port.
 *
 * <p>This class intentionally keeps the callbacks lightweight: the Forge implementation still owns
 * the full model reload, network sync and player-state behavior, but registering the real Fabric
 * events here gives later stages stable hook points without adding Forge references to the Fabric
 * entrypoint.</p>
 */
public final class YesSteveModelFabricLifecycle {
    private static final AtomicBoolean REGISTERED = new AtomicBoolean();

    private YesSteveModelFabricLifecycle() {
    }

    public static void register() {
        if (!REGISTERED.compareAndSet(false, true)) {
            return;
        }

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            YesSteveModelFabricConfig.Snapshot snapshot = YesSteveModelFabric.getConfig();
            if (snapshot == null) {
                YesSteveModelFabric.LOGGER.warn("OpenYSM Fabric server starting before config bootstrap completed");
                return;
            }
            YesSteveModelFabric.LOGGER.info("OpenYSM Fabric server starting with model root {}", snapshot.modelRoot().toAbsolutePath().normalize());
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
                YesSteveModelFabric.LOGGER.info("OpenYSM Fabric server stopped"));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                YesSteveModelFabric.LOGGER.debug("OpenYSM Fabric observed player join; full model sync migration is pending"));

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                YesSteveModelFabric.LOGGER.debug("OpenYSM Fabric observed player disconnect; full model sync migration is pending"));
    }
}
