package rbasamoyai.escalated.index;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import rbasamoyai.escalated.walkways.WalkwayBlockEntity;
import rbasamoyai.escalated.walkways.WalkwayInstance;
import rbasamoyai.escalated.walkways.WalkwayRenderer;

import static rbasamoyai.escalated.CreateEscalated.REGISTRATE;

public class EscalatedBlockEntities {

    public static final BlockEntityEntry<WalkwayBlockEntity> WALKWAY = REGISTRATE
            .blockEntity("walkway", WalkwayBlockEntity::new)
            .instance(() -> WalkwayInstance::new)
            .renderer(() -> WalkwayRenderer::new)
            .validBlocks(EscalatedBlocks.METAL_WALKWAY_TERMINAL, EscalatedBlocks.METAL_NARROW_WALKWAY,
                    EscalatedBlocks.METAL_WIDE_WALKWAY_SIDE, EscalatedBlocks.METAL_WIDE_WALKWAY_CENTER,
                    EscalatedBlocks.METAL_NARROW_ESCALATOR)
            .register();

    // TODO escalator blocks
    // TODO wooden walkway/escalator blocks

    public static void register() {}

}
