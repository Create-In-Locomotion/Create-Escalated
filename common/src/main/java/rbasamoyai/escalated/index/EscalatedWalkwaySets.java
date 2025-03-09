package rbasamoyai.escalated.index;

import rbasamoyai.escalated.walkways.WalkwaySet;

public class EscalatedWalkwaySets {

    public static WalkwaySet metalWalkwaySet() {
        return new WalkwaySet.Impl(EscalatedBlocks.METAL_NARROW_WALKWAY::get, EscalatedBlocks.METAL_WIDE_WALKWAY_SIDE::get,
                EscalatedBlocks.METAL_WIDE_WALKWAY_CENTER::get, EscalatedBlocks.METAL_WALKWAY_TERMINAL::get);
    }
    
    public static WalkwaySet metalEscalatorSet() {
        return new WalkwaySet.Impl(EscalatedBlocks.METAL_NARROW_ESCALATOR::get, EscalatedBlocks.METAL_NARROW_ESCALATOR::get /* TODO wide escalator side */,
                EscalatedBlocks.METAL_NARROW_ESCALATOR::get /* TODO wide escalator center */, EscalatedBlocks.METAL_WALKWAY_TERMINAL::get);
    }

}
