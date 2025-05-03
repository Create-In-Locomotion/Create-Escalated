package rbasamoyai.escalated;

import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rbasamoyai.escalated.advancements.WalkwayTravelTracker;
import rbasamoyai.escalated.index.EscalatedBlockEntities;
import rbasamoyai.escalated.index.EscalatedBlocks;
import rbasamoyai.escalated.index.EscalatedItems;
import rbasamoyai.escalated.index.EscalatedTriggers;

public class CreateEscalated {

    public static final String MOD_ID = "escalated";
    public static final String NAME = "Create: Escalated";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CreateEscalated.MOD_ID);

    public static void init() {
        ModGroup.register();

        EscalatedBlocks.register();
        EscalatedItems.register();
        EscalatedBlockEntities.register();

        EscalatedTriggers.register();
    }

    public static ResourceLocation resource(String path) { return new ResourceLocation(MOD_ID, path); }

    public static void onServerTick(MinecraftServer server) {
        WalkwayTravelTracker.tick();
    }

    public static void onServerStopping() {
        WalkwayTravelTracker.clearList();
    }

}
