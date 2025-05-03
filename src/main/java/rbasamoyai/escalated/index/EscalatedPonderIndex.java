package rbasamoyai.escalated.index;

import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.ponder.WalkwayPonders;

public class EscalatedPonderIndex {

    private static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(CreateEscalated.MOD_ID);

    public static void register() {
        HELPER.forComponents(EscalatedItems.METAL_WALKWAY_STEPS, EscalatedItems.WOODEN_WALKWAY_STEPS)
                .addStoryBoard("walkways/creating_walkways", WalkwayPonders::creatingWalkways)
                .addStoryBoard("walkways/adding_shafts_to_walkways", WalkwayPonders::addingShaftsToWalkways)
                .addStoryBoard("walkways/customizing_walkways", WalkwayPonders::customizingWalkways)
                .addStoryBoard("walkways/widening_walkways", WalkwayPonders::wideningWalkways);
    }

    public static void registerTags() {
        PonderRegistry.TAGS.forTag(EscalatedPonderTags.WALKWAYS)
                .add(EscalatedItems.METAL_WALKWAY_STEPS)
                .add(EscalatedItems.WOODEN_WALKWAY_STEPS);
    }

    public static void registerLang() {
        PonderLocalization.provideRegistrateLang(CreateEscalated.REGISTRATE);
    }

}
