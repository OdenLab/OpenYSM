package com.elfmcys.yesstevemodel;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fabric client resource reload hook for the staged client port.
 *
 * <p>Forge currently performs model/resource refresh work through Forge client events and managers.
 * This Fabric hook is deliberately lightweight until those managers are moved behind platform-neutral
 * services, but it already runs on the real Fabric resource reload pipeline.</p>
 */
public final class YesSteveModelFabricClientResources {
    private static final Identifier RELOAD_LISTENER_ID = Identifier.of(YesSteveModelFabric.MOD_ID, "client_resources");
    private static final AtomicBoolean REGISTERED = new AtomicBoolean();
    private static final AtomicInteger RELOAD_COUNT = new AtomicInteger();

    private YesSteveModelFabricClientResources() {
    }

    public static void register() {
        if (!REGISTERED.compareAndSet(false, true)) {
            return;
        }

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return RELOAD_LISTENER_ID;
            }

            @Override
            public CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer,
                                                  ResourceManager manager,
                                                  Profiler prepareProfiler,
                                                  Profiler applyProfiler,
                                                  Executor prepareExecutor,
                                                  Executor applyExecutor) {
                return synchronizer.whenPrepared(null).thenRunAsync(() -> {
                    int count = RELOAD_COUNT.incrementAndGet();
                    YesSteveModelFabric.LOGGER.debug("OpenYSM Fabric client resource reload #{} observed; model reload migration is pending", count);
                }, applyExecutor);
            }
        });
    }

    public static int getReloadCount() {
        return RELOAD_COUNT.get();
    }
}
