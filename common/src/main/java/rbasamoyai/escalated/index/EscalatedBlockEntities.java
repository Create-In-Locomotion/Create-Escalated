package rbasamoyai.escalated.index;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import rbasamoyai.escalated.handrails.HandrailBlockEntity;
import rbasamoyai.escalated.handrails.HandrailInstance;
import rbasamoyai.escalated.handrails.HandrailRenderer;
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
                    EscalatedBlocks.METAL_NARROW_ESCALATOR, EscalatedBlocks.METAL_WIDE_ESCALATOR_SIDE, EscalatedBlocks.METAL_WIDE_ESCALATOR_CENTER,
                    EscalatedBlocks.WOODEN_WALKWAY_TERMINAL, EscalatedBlocks.WOODEN_NARROW_WALKWAY,
                    EscalatedBlocks.WOODEN_WIDE_WALKWAY_SIDE, EscalatedBlocks.WOODEN_WIDE_WALKWAY_CENTER,
                    EscalatedBlocks.WOODEN_NARROW_ESCALATOR, EscalatedBlocks.WOODEN_WIDE_ESCALATOR_SIDE, EscalatedBlocks.WOODEN_WIDE_ESCALATOR_CENTER)
            .register();

    public static final BlockEntityEntry<HandrailBlockEntity> HANDRAIL = REGISTRATE
            .blockEntity("handrail", HandrailBlockEntity::new)
            .instance(() -> HandrailInstance::new)
            .renderer(() -> HandrailRenderer::new)
            .validBlocks(EscalatedBlocks.METAL_WALKWAY_HANDRAIL, EscalatedBlocks.METAL_ESCALATOR_HANDRAIL,
                    EscalatedBlocks.WOODEN_WALKWAY_HANDRAIL, EscalatedBlocks.WOODEN_ESCALATOR_HANDRAIL,
                    EscalatedBlocks.GLASS_WALKWAY_HANDRAIL, EscalatedBlocks.GLASS_ESCALATOR_HANDRAIL)
            .register();

    public static void register() {}

}
