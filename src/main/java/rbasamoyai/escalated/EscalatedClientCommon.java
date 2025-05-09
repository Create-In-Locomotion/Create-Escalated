package rbasamoyai.escalated;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.Minecraft;
import rbasamoyai.escalated.index.*;
import rbasamoyai.escalated.walkways.WalkwayConnectorHandler;

public class EscalatedClientCommon {

    public static void onClientSetup() {
        EscalatedMaterialSpecs.init();
        EscalatedInstanceFormats.init();
        EscalatedSpriteShiftEntries.init();
        EscalatedBlockPartials.resolveDeferredModels();
        PonderIndex.addPlugin(new EscalatedPonderPlugin());
    }

    public static void onClientGameTick(Minecraft minecraft) {
        if (minecraft.level == null || minecraft.player == null)
            return;

        WalkwayConnectorHandler.tick();
    }

}
