package com.elfmcys.yesstevemodel;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Fabric bootstrap for OpenYSM.
 *
 * <p>The codebase is still shared with the original Forge implementation, but this entrypoint lets a
 * Fabric loader discover the mod, initialize the native library layer, and expose the same mixin set
 * declared in {@code fabric.mod.json}.</p>
 */
public final class YesSteveModelFabric implements ModInitializer {
    public static final String MOD_ID = "yes_steve_model";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        try {
            NativeLibLoader.init();
        } catch (IOException e) {
            LOGGER.error("Failed to initialize OpenYSM native library on Fabric", e);
        }

        if (!NativeLibLoader.isAvailable()) {
            LOGGER.error(NativeLibLoader.getErrorMessage());
        }

        String loaderVersion = FabricLoader.getInstance()
                .getModContainer("fabricloader")
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
        LOGGER.info("OpenYSM Fabric bootstrap loaded with Fabric Loader {}", loaderVersion);
    }
}
