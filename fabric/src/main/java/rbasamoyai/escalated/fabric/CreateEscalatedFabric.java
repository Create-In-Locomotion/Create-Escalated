package rbasamoyai.escalated.fabric;

import net.fabricmc.api.ModInitializer;
import rbasamoyai.escalated.CreateEscalated;

public class CreateEscalatedFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CreateEscalated.init();
        CreateEscalated.REGISTRATE.register();
    }

}
