package com.elfmcys.yesstevemodel;

import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

/**
 * Fabric-side config bootstrap mirroring the Forge config keys and defaults.
 *
 * <p>This class intentionally avoids Fabric API dependencies so it can run during the earliest Fabric
 * entrypoint phase. Later migration work can wire these values into the shared client/server logic once
 * the Forge-only config holders are moved behind a platform abstraction.</p>
 */
public final class YesSteveModelFabricConfig {
    public static final String MOD_ID = "yes_steve_model";
    public static final String CLIENT_CONFIG_FILE = "yes_steve_model-client.properties";
    public static final String SERVER_CONFIG_FILE = "yes_steve_model-server.properties";

    public static final List<String> MODEL_DIRECTORIES = List.of(
            "built",
            "custom",
            "auth",
            "export",
            "cache",
            "cache/client",
            "cache/server"
    );

    private YesSteveModelFabricConfig() {
    }

    public static Snapshot load(Path configDir, Logger logger) throws IOException {
        Path modelRoot = configDir.resolve(MOD_ID);
        ensureModelDirectories(modelRoot);

        Properties client = loadOrCreate(configDir.resolve(CLIENT_CONFIG_FILE), clientDefaults(), "OpenYSM Fabric client config. Keys mirror the Forge client config defaults.");
        Properties server = loadOrCreate(configDir.resolve(SERVER_CONFIG_FILE), serverDefaults(), "OpenYSM Fabric server config. Keys mirror the Forge server config defaults.");

        Snapshot snapshot = new Snapshot(modelRoot, client, server);
        logger.info("OpenYSM Fabric config loaded from {}", configDir.toAbsolutePath().normalize());
        logger.info("OpenYSM Fabric model root prepared at {}", modelRoot.toAbsolutePath().normalize());
        return snapshot;
    }

    private static void ensureModelDirectories(Path modelRoot) throws IOException {
        for (String directory : MODEL_DIRECTORIES) {
            Files.createDirectories(modelRoot.resolve(directory));
        }
    }

    private static Properties loadOrCreate(Path path, Properties defaults, String comment) throws IOException {
        Files.createDirectories(path.getParent());
        Properties properties = new Properties(defaults);
        if (Files.isRegularFile(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                properties.load(reader);
            }
        }
        try (Writer writer = Files.newBufferedWriter(path)) {
            properties.store(writer, comment);
        }
        return properties;
    }

    private static Properties clientDefaults() {
        Properties properties = new Properties();
        properties.setProperty("general.DisclaimerShow", "true");
        properties.setProperty("general.PrintAnimationRouletteMsg", "false");
        properties.setProperty("general.DisableSelfModel", "false");
        properties.setProperty("general.DisableOtherModel", "false");
        properties.setProperty("general.DisableSelfHands", "false");
        properties.setProperty("general.DisableProjectileModel", "false");
        properties.setProperty("general.DisableVehicleModel", "false");
        properties.setProperty("general.DisableExternalFirstPersonAnim", "false");
        properties.setProperty("general.UseCompatibilityRenderer", "false");
        properties.setProperty("general.SoundVolume", "100.0");
        properties.setProperty("general.ShowModelIdFirst", "false");
        properties.setProperty("Integration.SophisticatedBackpack", "true");
        properties.setProperty("Integration.Parcool", "true");
        properties.setProperty("extra_player_render.DisablePlayerRender", "false");
        properties.setProperty("extra_player_render.PlayerPosX", "10");
        properties.setProperty("extra_player_render.PlayerPosY", "10");
        properties.setProperty("extra_player_render.PlayerScale", "40.0");
        properties.setProperty("extra_player_render.PlayerYawOffset", "5.0");
        properties.setProperty("loading_state_screen.DisableLoadingStateScreen", "false");
        properties.setProperty("loading_state_screen.LoadingStatePosition", "TOP_CENTER");
        return properties;
    }

    private static Properties serverDefaults() {
        Properties properties = new Properties();
        properties.setProperty("DefaultModelId", "default");
        properties.setProperty("DefaultModelTexture", "default");
        properties.setProperty("CanSwitchModel", "true");
        properties.setProperty("ClientNotDisplayModels", "[]");
        properties.setProperty("server_scheduler.ThreadCount", "0");
        properties.setProperty("server_scheduler.BandwidthLimit", "5");
        properties.setProperty("server_scheduler.PlayerSyncTimeout", "0");
        properties.setProperty("server_scheduler.LowBandwidthUsage", "false");
        properties.setProperty("server_scheduler.AcceptSoundFX", "0");
        return properties;
    }

    public record Snapshot(Path modelRoot, Properties client, Properties server) {
    }
}
