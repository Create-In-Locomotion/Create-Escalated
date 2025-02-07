package rbasamoyai.escalated.fabric;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.fabricmc.api.ModInitializer;
import rbasamoyai.escalated.CreateEscalated;

public class CreateEscalatedFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CreateEscalated.init();
        CreateEscalated.LOGGER.info(EnvExecutor.unsafeRunForDist(
                () -> () -> "{} is accessing Porting Lib on a Fabric client!",
                () -> () -> "{} is accessing Porting Lib on a Fabric server!"
                ), CreateEscalated.NAME);
        // on fabric, Registrates must be explicitly finalized and registered.
        CreateEscalated.REGISTRATE.register();
    }

}
