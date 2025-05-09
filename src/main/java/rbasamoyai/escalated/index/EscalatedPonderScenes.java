package rbasamoyai.escalated.index;

import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.ponder.WalkwayPonders;

public class EscalatedPonderScenes {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);
        HELPER.forComponents(EscalatedItems.METAL_WALKWAY_STEPS, EscalatedItems.WOODEN_WALKWAY_STEPS)
                .addStoryBoard("walkways/creating_walkways", WalkwayPonders::creatingWalkways)
                .addStoryBoard("walkways/adding_shafts_to_walkways", WalkwayPonders::addingShaftsToWalkways)
                .addStoryBoard("walkways/customizing_walkways", WalkwayPonders::customizingWalkways)
                .addStoryBoard("walkways/widening_walkways", WalkwayPonders::wideningWalkways);
    }
}
