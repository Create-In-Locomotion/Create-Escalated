package rbasamoyai.escalated.walkways;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

/**
 * Adapted from {@link com.simibubi.create.content.kinetics.belt.BeltHelper}
 */
public class WalkwayHelper {

    public static WalkwayBlockEntity getSegmentBE(LevelAccessor level, BlockPos pos) {
        if (level instanceof Level l && !l.isLoaded(pos))
            return null;
        return level.getBlockEntity(pos) instanceof WalkwayBlockEntity walkway ? walkway : null;
    }

    public static WalkwayBlockEntity getControllerBE(LevelAccessor level, BlockPos pos) {
        WalkwayBlockEntity segment = getSegmentBE(level, pos);
        if (segment == null)
            return null;
        BlockPos controllerPos = segment.controller;
        return controllerPos == null ? null : getSegmentBE(level, controllerPos);
    }

    private WalkwayHelper() {}

}
