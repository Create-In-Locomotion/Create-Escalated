package rbasamoyai.escalated;

import net.minecraft.client.Minecraft;
import rbasamoyai.escalated.index.EscalatedBlockPartials;
import rbasamoyai.escalated.walkways.WalkwayConnectorHandler;

public class EscalatedClientCommon {

    public static void onClientSetup() {
        EscalatedBlockPartials.resolveDeferredModels();
    }

    public static void onClientGameTick(Minecraft minecraft) {
        if (minecraft.level == null || minecraft.player == null)
            return;

        WalkwayConnectorHandler.tick();
    }

}
