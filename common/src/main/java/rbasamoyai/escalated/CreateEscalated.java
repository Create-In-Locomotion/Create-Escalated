package rbasamoyai.escalated;

import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateEscalated {

    public static final String MOD_ID = "escalated";
    public static final String NAME = "Create: Escalated";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CreateEscalated.MOD_ID);

    public static void init() {
        EscalatedBlocks.register();
    }

    public static ResourceLocation id(String path) { return new ResourceLocation(MOD_ID, path); }

}
