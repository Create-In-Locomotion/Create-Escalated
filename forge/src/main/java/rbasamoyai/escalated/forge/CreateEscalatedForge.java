package rbasamoyai.escalated.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.config.EscalatedConfigs;

@Mod(CreateEscalated.MOD_ID)
public class CreateEscalatedForge {

    public CreateEscalatedForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        ModLoadingContext mlContext = ModLoadingContext.get();

        CreateEscalated.REGISTRATE.registerEventListeners(modBus);

        CreateEscalated.init();
        ModGroupImpl.registerForge(modBus);

        EscalatedConfigs.registerConfigs(mlContext::registerConfig);

        modBus.addListener(this::onLoadConfig);
        modBus.addListener(this::onReloadConfig);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> EscalatedClientForge.prepareClient(modBus, forgeBus));
    }

    private void onLoadConfig(ModConfigEvent.Loading evt) { EscalatedConfigs.onLoad(evt.getConfig()); }

    private void onReloadConfig(ModConfigEvent.Reloading evt) { EscalatedConfigs.onReload(evt.getConfig()); }

}
