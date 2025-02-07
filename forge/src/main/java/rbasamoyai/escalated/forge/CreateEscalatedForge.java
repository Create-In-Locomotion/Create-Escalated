package rbasamoyai.escalated.forge;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import rbasamoyai.escalated.CreateEscalated;

@Mod(CreateEscalated.MOD_ID)
public class CreateEscalatedForge {

    public CreateEscalatedForge() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        CreateEscalated.REGISTRATE.registerEventListeners(eventBus);
        CreateEscalated.init();
    }

}
