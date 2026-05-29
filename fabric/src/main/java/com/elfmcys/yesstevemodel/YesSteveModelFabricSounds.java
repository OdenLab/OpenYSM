package com.elfmcys.yesstevemodel;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.concurrent.atomic.AtomicBoolean;

/** Fabric sound registrations matching the Forge `ModSoundEvents` registry. */
public final class YesSteveModelFabricSounds {
    public static final Identifier CUSTOM_ID = Identifier.of(YesSteveModelFabric.MOD_ID, "custom");
    public static final SoundEvent CUSTOM_SOUND = SoundEvent.of(CUSTOM_ID, 16.0f);

    private static final AtomicBoolean REGISTERED = new AtomicBoolean();

    private YesSteveModelFabricSounds() {
    }

    public static void register() {
        if (!REGISTERED.compareAndSet(false, true)) {
            return;
        }
        Registry.register(Registries.SOUND_EVENT, CUSTOM_ID, CUSTOM_SOUND);
        YesSteveModelFabric.LOGGER.debug("OpenYSM Fabric registered sound event {}", CUSTOM_ID);
    }
}
