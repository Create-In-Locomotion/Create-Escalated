package rbasamoyai.escalated.fabric;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.config.EscalatedConfigs;

public class CreateEscalatedFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CreateEscalated.init();
        CreateEscalated.REGISTRATE.register();

        EscalatedConfigs.registerConfigs((t, c) -> ForgeConfigRegistry.INSTANCE.register(CreateEscalated.MOD_ID, t, c));

        ModConfigEvents.loading(CreateEscalated.MOD_ID).register(EscalatedConfigs::onLoad);
        ModConfigEvents.reloading(CreateEscalated.MOD_ID).register(EscalatedConfigs::onReload);

        ServerTickEvents.END_SERVER_TICK.register(this::onServerTickEnd);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
    }

    private void onServerTickEnd(MinecraftServer server) { CreateEscalated.onServerTick(server); }

    private void onServerStopping(MinecraftServer server) { CreateEscalated.onServerStopping(); }

}
