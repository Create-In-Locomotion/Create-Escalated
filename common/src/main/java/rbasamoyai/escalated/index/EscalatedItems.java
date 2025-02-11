package rbasamoyai.escalated.index;

import com.tterrag.registrate.util.entry.ItemEntry;
import rbasamoyai.escalated.ModGroup;
import rbasamoyai.escalated.datagen.EscalatedBuilderTransformers;
import rbasamoyai.escalated.walkways.WalkwayConnectorItem;

import static rbasamoyai.escalated.CreateEscalated.REGISTRATE;

public class EscalatedItems {

    static { ModGroup.useModTab(ModGroup.MAIN_TAB_KEY); }

    public static final ItemEntry<WalkwayConnectorItem> METAL_WALKWAY_STEPS = REGISTRATE
            .item("metal_walkway_steps", p -> new WalkwayConnectorItem(p, false))
            .transform(EscalatedBuilderTransformers.metalWalkwaySteps())
            .register();

    public static void register() {}

}
