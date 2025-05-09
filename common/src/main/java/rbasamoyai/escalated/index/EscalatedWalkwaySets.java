package rbasamoyai.escalated.index;

import rbasamoyai.escalated.walkways.WalkwaySet;

public class EscalatedWalkwaySets {

    public static WalkwaySet metalWalkwaySet() {
        return new WalkwaySet.Impl(EscalatedBlocks.METAL_NARROW_WALKWAY::get, EscalatedBlocks.METAL_WIDE_WALKWAY_SIDE::get,
                EscalatedBlocks.METAL_WIDE_WALKWAY_CENTER::get, EscalatedBlocks.METAL_WALKWAY_TERMINAL::get, EscalatedBlocks.METAL_WALKWAY_HANDRAIL::get);
    }
    
    public static WalkwaySet metalEscalatorSet() {
        return new WalkwaySet.Impl(EscalatedBlocks.METAL_NARROW_ESCALATOR::get, EscalatedBlocks.METAL_WIDE_ESCALATOR_SIDE::get,
                EscalatedBlocks.METAL_WIDE_ESCALATOR_CENTER::get, EscalatedBlocks.METAL_WALKWAY_TERMINAL::get, EscalatedBlocks.METAL_ESCALATOR_HANDRAIL::get);
    }

    public static WalkwaySet woodenWalkwaySet() {
        return new WalkwaySet.Impl(EscalatedBlocks.WOODEN_NARROW_WALKWAY::get, EscalatedBlocks.WOODEN_WIDE_WALKWAY_SIDE::get,
                EscalatedBlocks.WOODEN_WIDE_WALKWAY_CENTER::get, EscalatedBlocks.WOODEN_WALKWAY_TERMINAL::get, EscalatedBlocks.WOODEN_WALKWAY_HANDRAIL::get);
    }

    public static WalkwaySet woodenEscalatorSet() {
        return new WalkwaySet.Impl(EscalatedBlocks.WOODEN_NARROW_ESCALATOR::get, EscalatedBlocks.WOODEN_WIDE_ESCALATOR_SIDE::get,
                EscalatedBlocks.WOODEN_WIDE_ESCALATOR_CENTER::get, EscalatedBlocks.WOODEN_WALKWAY_TERMINAL::get, EscalatedBlocks.WOODEN_ESCALATOR_HANDRAIL::get);
    }

}
