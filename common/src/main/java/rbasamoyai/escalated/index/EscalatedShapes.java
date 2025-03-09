package rbasamoyai.escalated.index;

import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import rbasamoyai.escalated.walkways.EscalatorVoxelShaper;
import rbasamoyai.escalated.walkways.WalkwaySlope;

import static net.minecraft.world.level.block.Block.box;
import static rbasamoyai.escalated.walkways.EscalatorVoxelShaper.createEscalatorStepShapes;

public class EscalatedShapes {

    public static final VoxelShaper
            NARROW_WALKWAY = VoxelShaper.forHorizontal(Shapes.or(box(0, 0, 0, 16, 15.5, 16),
                    box(0, 15.5, 0, 2, 16, 16), box(14, 15.5, 0, 16, 16, 16)), Direction.NORTH),
            WIDE_WALKWAY_SIDE_LEFT = VoxelShaper.forHorizontal(Shapes.or(box(0, 0, 0, 16, 15.5, 16),
                    box(0, 15.5, 0, 2, 16, 16)), Direction.NORTH),
            WIDE_WALKWAY_SIDE_RIGHT = VoxelShaper.forHorizontal(Shapes.or(box(0, 0, 0, 16, 15.5, 16),
                    box(14, 15.5, 0, 16, 16, 16)), Direction.NORTH),
            NARROW_ESCALATOR_BOTTOM = VoxelShaper.forHorizontal(createBottomEscalatorShape(true, true), Direction.SOUTH),
            NARROW_ESCALATOR_MIDDLE = VoxelShaper.forHorizontal(createMiddleEscalatorShape(true, true), Direction.SOUTH),
            NARROW_ESCALATOR_TOP = VoxelShaper.forHorizontal(createTopEscalatorShape(true, true), Direction.SOUTH),
            // TODO narrow rails
            WIDE_ESCALATOR_SIDE_BOTTOM_LEFT = VoxelShaper.forHorizontal(createBottomEscalatorShape(true, false), Direction.SOUTH),
            WIDE_ESCALATOR_SIDE_MIDDLE_LEFT = VoxelShaper.forHorizontal(createMiddleEscalatorShape(true, false), Direction.SOUTH),
            WIDE_ESCALATOR_SIDE_TOP_LEFT = VoxelShaper.forHorizontal(createTopEscalatorShape(true, false), Direction.SOUTH),
            WIDE_ESCALATOR_SIDE_BOTTOM_RIGHT = VoxelShaper.forHorizontal(createBottomEscalatorShape(false, true), Direction.SOUTH),
            WIDE_ESCALATOR_SIDE_MIDDLE_RIGHT = VoxelShaper.forHorizontal(createMiddleEscalatorShape(false, true), Direction.SOUTH),
            WIDE_ESCALATOR_SIDE_TOP_RIGHT = VoxelShaper.forHorizontal(createTopEscalatorShape(false, true), Direction.SOUTH),
            // TODO wide escalator side rails
            WIDE_ESCALATOR_CENTER_BOTTOM = VoxelShaper.forHorizontal(createBottomEscalatorShape(false, false), Direction.SOUTH),
            WIDE_ESCALATOR_CENTER_MIDDLE = VoxelShaper.forHorizontal(createMiddleEscalatorShape(false, false), Direction.SOUTH),
            WIDE_ESCALATOR_CENTER_TOP = VoxelShaper.forHorizontal(createTopEscalatorShape(false, false), Direction.SOUTH);

    public static final VoxelShape WIDE_WALKWAY_CENTER = box(0, 0, 0, 16, 15.5, 16);

    public static final EscalatorVoxelShaper
            NARROW_ESCALATOR_BOTTOM_STEPPED = createEscalatorStepShapes(NARROW_ESCALATOR_BOTTOM.get(Direction.NORTH), Direction.NORTH, WalkwaySlope.BOTTOM),
            NARROW_ESCALATOR_MIDDLE_STEPPED = createEscalatorStepShapes(NARROW_ESCALATOR_MIDDLE.get(Direction.NORTH), Direction.NORTH, WalkwaySlope.MIDDLE),
            NARROW_ESCALATOR_TOP_STEPPED = createEscalatorStepShapes(NARROW_ESCALATOR_TOP.get(Direction.NORTH), Direction.NORTH, WalkwaySlope.TOP),
            // TODO narrow escalator rail voxel shapers
            WIDE_ESCALATOR_SIDE_BOTTOM_LEFT_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_SIDE_BOTTOM_LEFT.get(Direction.NORTH), Direction.NORTH, WalkwaySlope.BOTTOM),
            WIDE_ESCALATOR_SIDE_MIDDLE_LEFT_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_SIDE_MIDDLE_LEFT.get(Direction.NORTH), Direction.NORTH, WalkwaySlope.MIDDLE),
            WIDE_ESCALATOR_SIDE_TOP_LEFT_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_SIDE_TOP_LEFT.get(Direction.NORTH), Direction.NORTH, WalkwaySlope.TOP),
            WIDE_ESCALATOR_SIDE_BOTTOM_RIGHT_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_SIDE_BOTTOM_RIGHT.get(Direction.NORTH), Direction.NORTH, WalkwaySlope.BOTTOM),
            WIDE_ESCALATOR_SIDE_MIDDLE_RIGHT_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_SIDE_MIDDLE_RIGHT.get(Direction.NORTH), Direction.NORTH, WalkwaySlope.MIDDLE),
            WIDE_ESCALATOR_SIDE_TOP_RIGHT_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_SIDE_TOP_RIGHT.get(Direction.NORTH), Direction.NORTH, WalkwaySlope.TOP),
            // TODO wide escalator side rail voxel shapers
            WIDE_ESCALATOR_CENTER_BOTTOM_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_CENTER_BOTTOM.get(Direction.NORTH), Direction.NORTH, WalkwaySlope.BOTTOM),
            WIDE_ESCALATOR_CENTER_MIDDLE_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_CENTER_MIDDLE.get(Direction.NORTH), Direction.NORTH, WalkwaySlope.MIDDLE),
            WIDE_ESCALATOR_CENTER_TOP_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_CENTER_TOP.get(Direction.NORTH), Direction.NORTH, WalkwaySlope.TOP);

    private static VoxelShape createBottomEscalatorShape(boolean left, boolean right) {
        VoxelShape shape = box(0, 0, 0, 16, 15.5, 16);
        VoxelShape rail = Shapes.or(box(0, 15.5, 0, 2, 16, 16), box(0, 16, 9, 2, 17, 16), box(0, 17, 10, 2, 18, 16),
                box(0, 18, 11, 2, 19, 16), box(0, 19, 12, 2, 20, 16), box(0, 20, 13, 2, 21, 16), box(0, 21, 14, 2, 22, 16),
                box(0, 22, 15, 2, 23, 16));
        if (left)
            shape = Shapes.or(shape, rail.move(14 / 16f, 0, 0));
        if (right)
            shape = Shapes.or(shape, rail);
        return shape;
    }

    private static VoxelShape createMiddleEscalatorShape(boolean left, boolean right) {
        VoxelShape shape = Shapes.empty();
        VoxelShape mainSlice = box(0, -14, 0, 16, -1, 1);
        VoxelShape rightSlice = box(0, -1, 0, 2, 8, 1);
        VoxelShape leftSlice = box(14, -1, 0, 16, 8, 1);
        for (int i = 0; i < 16; ++i) {
            float f = i / 16f;
            shape = Shapes.or(shape, mainSlice.move(0, f, f));
            if (left)
                shape = Shapes.or(shape, leftSlice.move(0, f, f));
            if (right)
                shape = Shapes.or(shape, rightSlice.move(0, f, f));
        }
        return shape;
    }

    private static VoxelShape createTopEscalatorShape(boolean left, boolean right) {
        VoxelShape shape = box(0, 0, 8, 16, 6, 16);
        if (left)
            shape = Shapes.or(box(14, 6, 8, 16, 16, 16));
        if (right)
            shape = Shapes.or(box(0, 6, 8, 2, 16, 16));
        VoxelShape mainSlice = box(0, -14, 0, 16, -1, 1);
        VoxelShape leftSlice = box(14, -1, 0, 16, 8, 1);
        VoxelShape rightSlice = box(0, -1, 0, 2, 8, 1);
        for (int i = 0; i < 9; ++i) {
            float f = i / 16f;
            shape = Shapes.or(shape, mainSlice.move(0, f, f));
            if (left)
                shape = Shapes.or(shape, leftSlice.move(0, f, f));
            if (right)
                shape = Shapes.or(shape, rightSlice.move(0, f, f));
        }
        for (int i = 9; i < 15; ++i) {
            float f = i / 16f;
            shape = Shapes.or(shape, mainSlice.move(0, f, f));
        }
        shape = Shapes.or(shape, box(0, 0, 15, 16, 13.5, 16));
        return shape;
    }

    private EscalatedShapes() {}

}
