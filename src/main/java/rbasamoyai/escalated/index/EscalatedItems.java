package rbasamoyai.escalated.index;

import com.tterrag.registrate.util.entry.ItemEntry;
import rbasamoyai.escalated.ModGroup;
import rbasamoyai.escalated.datagen.EscalatedBuilderTransformers;
import rbasamoyai.escalated.walkways.WalkwayConnectorItem;

import static rbasamoyai.escalated.CreateEscalated.REGISTRATE;

public class EscalatedItems {

    static { ModGroup.useModTab(ModGroup.MAIN_TAB_KEY); }

    public static final ItemEntry<WalkwayConnectorItem> METAL_WALKWAY_STEPS = REGISTRATE
            .item("metal_walkway_steps", p -> new WalkwayConnectorItem(EscalatedBlocks.METAL_WALKWAY_TERMINAL.get(), p,
                    EscalatedBlocks.METAL_NARROW_WALKWAY.get(), EscalatedBlocks.METAL_NARROW_ESCALATOR.get(),
                    EscalatedBlocks.METAL_WIDE_WALKWAY_SIDE.get(), EscalatedBlocks.METAL_WIDE_WALKWAY_CENTER.get(),
                    EscalatedBlocks.METAL_WIDE_ESCALATOR_SIDE.get(), EscalatedBlocks.METAL_WIDE_ESCALATOR_CENTER.get()))
            .transform(EscalatedBuilderTransformers.existingItemModel())
            .register();

    public static final ItemEntry<WalkwayConnectorItem> WOODEN_WALKWAY_STEPS = REGISTRATE
            .item("wooden_walkway_steps", p -> new WalkwayConnectorItem(EscalatedBlocks.WOODEN_WALKWAY_TERMINAL.get(), p,
                    EscalatedBlocks.WOODEN_NARROW_WALKWAY.get(), EscalatedBlocks.WOODEN_NARROW_ESCALATOR.get(),
                    EscalatedBlocks.WOODEN_WIDE_WALKWAY_SIDE.get(), EscalatedBlocks.WOODEN_WIDE_WALKWAY_CENTER.get(),
                    EscalatedBlocks.WOODEN_WIDE_ESCALATOR_SIDE.get(), EscalatedBlocks.WOODEN_WIDE_ESCALATOR_CENTER.get()))
            .transform(EscalatedBuilderTransformers.existingItemModel())
            .register();

    public static void register() {}

}
