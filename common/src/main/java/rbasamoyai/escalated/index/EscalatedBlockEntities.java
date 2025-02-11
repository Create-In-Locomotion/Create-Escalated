package rbasamoyai.escalated.index;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import rbasamoyai.escalated.walkways.WalkwayTerminalBlockEntity;
import rbasamoyai.escalated.walkways.WalkwayTerminalInstance;
import rbasamoyai.escalated.walkways.WalkwayTerminalRenderer;

import static rbasamoyai.escalated.CreateEscalated.REGISTRATE;

public class EscalatedBlockEntities {

    public static final BlockEntityEntry<WalkwayTerminalBlockEntity> WALKWAY_TERMINAL = REGISTRATE
            .blockEntity("walkway_terminal", WalkwayTerminalBlockEntity::new)
            .instance(() -> WalkwayTerminalInstance::new)
            .renderer(() -> WalkwayTerminalRenderer::new)
            .validBlocks(EscalatedBlocks.WALKWAY_TERMINAL)
            .register();

    public static void register() {}

}
