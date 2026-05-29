package com.elfmcys.yesstevemodel;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Fabric key-binding registration mirroring the Forge default controls.
 *
 * <p>The full Forge key actions still depend on Forge capability/network/config classes. Phase two can
 * move those actions behind shared services; this class makes the Fabric Controls screen expose the
 * same default bindings now and provides a safe tick hook for later behavior.</p>
 */
public final class YesSteveModelFabricKeyMappings {
    private static final String CATEGORY = "key.category.yes_steve_model";
    private static final AtomicBoolean REGISTERED = new AtomicBoolean();
    private static final List<KeyBinding> EXTRA_ANIMATION_KEYS = new ArrayList<>();

    private static KeyBinding playerModel;
    private static KeyBinding animationRoulette;
    private static KeyBinding lockRoulette;
    private static KeyBinding debugAnimation;
    private static KeyBinding extraPlayerRender;

    private YesSteveModelFabricKeyMappings() {
    }

    public static void register() {
        if (!REGISTERED.compareAndSet(false, true)) {
            return;
        }

        playerModel = registerKey("key.yes_steve_model.player_model.desc", GLFW.GLFW_KEY_Y);
        animationRoulette = registerKey("key.yes_steve_model.animation_roulette.desc", GLFW.GLFW_KEY_Z);
        lockRoulette = registerKey("key.yes_steve_model.lock_roulette.desc", GLFW.GLFW_KEY_L);
        debugAnimation = registerKey("key.yes_steve_model.debug_animation.desc", GLFW.GLFW_KEY_UNKNOWN);
        extraPlayerRender = registerKey("key.yes_steve_model.open_extra_player_render.desc", GLFW.GLFW_KEY_P);

        for (int i = 0; i <= 7; i++) {
            EXTRA_ANIMATION_KEYS.add(registerKey(String.format("key.yes_steve_model.extra_animation.%d.desc", i), GLFW.GLFW_KEY_UNKNOWN));
        }
    }

    public static void tick() {
        if (!REGISTERED.get()) {
            return;
        }
        drain(playerModel, "player model screen");
        drain(animationRoulette, "animation roulette");
        drain(lockRoulette, "animation lock");
        drain(debugAnimation, "debug animation screen");
        drain(extraPlayerRender, "extra player render screen");
        for (int i = 0; i < EXTRA_ANIMATION_KEYS.size(); i++) {
            drain(EXTRA_ANIMATION_KEYS.get(i), "extra animation " + i);
        }
    }

    public static List<KeyBinding> extraAnimationKeys() {
        return Collections.unmodifiableList(EXTRA_ANIMATION_KEYS);
    }

    private static KeyBinding registerKey(String translationKey, int keyCode) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(translationKey, InputUtil.Type.KEYSYM, keyCode, CATEGORY));
    }

    private static void drain(KeyBinding keyBinding, String actionName) {
        if (keyBinding == null) {
            return;
        }
        while (keyBinding.wasPressed()) {
            YesSteveModelFabric.LOGGER.debug("OpenYSM Fabric key '{}' pressed; action migration is pending", actionName);
        }
    }
}
