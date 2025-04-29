package rbasamoyai.escalated.index;

import com.simibubi.create.foundation.ponder.PonderTag;
import rbasamoyai.escalated.CreateEscalated;

public class EscalatedPonderTags {

    public static final PonderTag WALKWAYS = create("walkways")
            .item(EscalatedItems.METAL_WALKWAY_STEPS.get(), true, true)
            .defaultLang("Walkways and Escalators", "How to build walkways and escalators to move around efficiently")
            .addToIndex();

    private static PonderTag create(String id) { return new PonderTag(CreateEscalated.resource(id)); }

    public static void register() {}

}
