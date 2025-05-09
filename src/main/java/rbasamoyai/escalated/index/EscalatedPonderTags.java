package rbasamoyai.escalated.index;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import rbasamoyai.escalated.CreateEscalated;

public class EscalatedPonderTags {
    public static final ResourceLocation

    WALKWAYS = CreateEscalated.resource("walkways");

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);
        helper.registerTag(WALKWAYS)
            .item(EscalatedItems.METAL_WALKWAY_STEPS.get(), true, true)
            .title("Walkways and Escalators")
            .description("How to build walkways and escalators to move around efficiently")
            .addToIndex();

        HELPER.addToTag(EscalatedPonderTags.WALKWAYS)
                .add(EscalatedItems.METAL_WALKWAY_STEPS)
                .add(EscalatedItems.WOODEN_WALKWAY_STEPS);
    }
}
