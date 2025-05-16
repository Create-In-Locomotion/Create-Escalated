package rbasamoyai.escalated.forge;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.EscalatedClientCommon;
import rbasamoyai.escalated.config.EscalatedConfigs;
import rbasamoyai.escalated.index.EscalatedBlockPartials;

public class EscalatedClientForge {

    public static void prepareClient(IEventBus modBus, IEventBus forgeBus) {
        EscalatedBlockPartials.init();

        modBus.addListener(EscalatedClientForge::onClientSetup);
        modBus.addListener(EscalatedClientForge::onLoadComplete);

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

    public static void onLoadComplete(FMLLoadCompleteEvent evt) {
        ModContainer container = ModList.get()
                .getModContainerById(CreateEscalated.MOD_ID)
                .orElseThrow(() -> new IllegalStateException("Create: Escalated mod container missing on LoadComplete"));
        container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> EscalatedConfigs.createConfigScreen(screen)));
    }

}
