package rbasamoyai.escalated.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import rbasamoyai.escalated.EscalatedClientCommon;

public class EscalatedClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EscalatedClientCommon.onClientSetup();

        ClientTickEvents.END_CLIENT_TICK.register(EscalatedClientFabric::onClientTick);
    }

    public static void onClientTick(Minecraft minecraft) {
        EscalatedClientCommon.onClientGameTick(minecraft);
    }

}
