package rbasamoyai.escalated.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import rbasamoyai.escalated.CreateEscalated;

@Mod(CreateEscalated.MOD_ID)
public class CreateEscalatedForge {

    public CreateEscalatedForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        CreateEscalated.REGISTRATE.registerEventListeners(modBus);

        CreateEscalated.init();
        ModGroupImpl.registerForge(modBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> EscalatedClientForge.prepareClient(modBus, forgeBus));
    }

}
