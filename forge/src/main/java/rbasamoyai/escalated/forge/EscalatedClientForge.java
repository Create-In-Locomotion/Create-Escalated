package rbasamoyai.escalated.forge;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import rbasamoyai.escalated.EscalatedClientCommon;
import rbasamoyai.escalated.index.EscalatedBlockPartials;

public class EscalatedClientForge {

    public static void prepareClient(IEventBus modBus, IEventBus forgeBus) {
        EscalatedBlockPartials.init();

        modBus.addListener(EscalatedClientForge::onClientSetup);

        forgeBus.addListener(EscalatedClientForge::onClientGameTick);
    }

    public static void onClientSetup(FMLClientSetupEvent evt) {
        evt.enqueueWork(() -> {
            EscalatedClientCommon.onClientSetup();
        });
    }

    public static void onClientGameTick(TickEvent.ClientTickEvent evt) {
        EscalatedClientCommon.onClientGameTick(Minecraft.getInstance());
    }

}
