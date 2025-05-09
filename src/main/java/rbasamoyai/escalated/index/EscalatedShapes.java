package rbasamoyai.escalated.index;

import net.createmod.catnip.math.VoxelShaper;
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
            WIDE_ESCALATOR_SIDE_BOTTOM_LEFT = VoxelShaper.forHorizontal(createBottomEscalatorShape(true, false), Direction.SOUTH),
            WIDE_ESCALATOR_SIDE_MIDDLE_LEFT = VoxelShaper.forHorizontal(createMiddleEscalatorShape(true, false), Direction.SOUTH),
            WIDE_ESCALATOR_SIDE_TOP_LEFT = VoxelShaper.forHorizontal(createTopEscalatorShape(true, false), Direction.SOUTH),
            WIDE_ESCALATOR_SIDE_BOTTOM_RIGHT = VoxelShaper.forHorizontal(createBottomEscalatorShape(false, true), Direction.SOUTH),
            WIDE_ESCALATOR_SIDE_MIDDLE_RIGHT = VoxelShaper.forHorizontal(createMiddleEscalatorShape(false, true), Direction.SOUTH),
            WIDE_ESCALATOR_SIDE_TOP_RIGHT = VoxelShaper.forHorizontal(createTopEscalatorShape(false, true), Direction.SOUTH),
            WIDE_ESCALATOR_CENTER_BOTTOM = VoxelShaper.forHorizontal(createBottomEscalatorShape(false, false), Direction.SOUTH),
            WIDE_ESCALATOR_CENTER_MIDDLE = VoxelShaper.forHorizontal(createMiddleEscalatorShape(false, false), Direction.SOUTH),
            WIDE_ESCALATOR_CENTER_TOP = VoxelShaper.forHorizontal(createTopEscalatorShape(false, false), Direction.SOUTH),
            WALKWAY_HANDRAIL_LEFT_START = VoxelShaper.forHorizontal(Shapes.or(box(0, 0, 0, 2, 15, 10), box(0, 15, 0, 2, 16, 9)), Direction.NORTH),
            WALKWAY_HANDRAIL_LEFT_HORIZONTAL = VoxelShaper.forHorizontal(box(0, 0, 0, 2, 16, 16), Direction.NORTH),
            WALKWAY_HANDRAIL_LEFT_END = VoxelShaper.forHorizontal(Shapes.or(box(0, 0, 6, 2, 15, 16), box(0, 15, 7, 2, 16, 16)), Direction.NORTH),
            WALKWAY_HANDRAIL_RIGHT_START = VoxelShaper.forHorizontal(Shapes.or(box(14, 0, 0, 16, 15, 10), box(14, 15, 0, 16, 16, 9)), Direction.NORTH),
            WALKWAY_HANDRAIL_RIGHT_HORIZONTAL = VoxelShaper.forHorizontal(box(14, 0, 0, 16, 16, 16), Direction.NORTH),
            WALKWAY_HANDRAIL_RIGHT_END = VoxelShaper.forHorizontal(Shapes.or(box(14, 0, 6, 16, 15, 16), box(14, 15, 7, 16, 16, 16)), Direction.NORTH),
            WALKWAY_HANDRAIL_BOTH_START = VoxelShaper.forHorizontal(Shapes.or(box(14, 0, 0, 16, 15, 10), box(14, 15, 0, 16, 16, 9),
                    box(0, 0, 0, 2, 15, 10), box(0, 15, 0, 2, 16, 9)), Direction.NORTH),
            WALKWAY_HANDRAIL_BOTH_HORIZONTAL = VoxelShaper.forHorizontal(Shapes.or(box(14, 0, 0, 16, 16, 16), box(0, 0, 0, 2, 16, 16)), Direction.NORTH),
            WALKWAY_HANDRAIL_BOTH_END = VoxelShaper.forHorizontal(Shapes.or(box(14, 0, 6, 16, 15, 16), box(14, 15, 7, 16, 16, 16),
                    box(0, 0, 6, 2, 15, 16), box(0, 15, 7, 2, 16, 16)), Direction.NORTH),
            ESCALATOR_HANDRAIL_LEFT_BOTTOM = VoxelShaper.forHorizontal(createBottomEscalatorHandrailShape(true, false), Direction.NORTH),
            ESCALATOR_HANDRAIL_LEFT_MIDDLE = VoxelShaper.forHorizontal(createMiddleEscalatorHandrailShape(true, false), Direction.NORTH),
            ESCALATOR_HANDRAIL_LEFT_TOP = VoxelShaper.forHorizontal(createTopEscalatorHandrailShape(true, false), Direction.NORTH),
            ESCALATOR_HANDRAIL_RIGHT_BOTTOM = VoxelShaper.forHorizontal(createBottomEscalatorHandrailShape(false, true), Direction.NORTH),
            ESCALATOR_HANDRAIL_RIGHT_MIDDLE = VoxelShaper.forHorizontal(createMiddleEscalatorHandrailShape(false, true), Direction.NORTH),
            ESCALATOR_HANDRAIL_RIGHT_TOP = VoxelShaper.forHorizontal(createTopEscalatorHandrailShape(false, true), Direction.NORTH),
            ESCALATOR_HANDRAIL_BOTH_BOTTOM = VoxelShaper.forHorizontal(createBottomEscalatorHandrailShape(true, true), Direction.NORTH),
            ESCALATOR_HANDRAIL_BOTH_MIDDLE = VoxelShaper.forHorizontal(createMiddleEscalatorHandrailShape(true, true), Direction.NORTH),
            ESCALATOR_HANDRAIL_BOTH_TOP = VoxelShaper.forHorizontal(createTopEscalatorHandrailShape(true, true), Direction.NORTH);

    public static final VoxelShape WIDE_WALKWAY_CENTER = box(0, 0, 0, 16, 15.5, 16);

    public static final EscalatorVoxelShaper
            NARROW_ESCALATOR_BOTTOM_STEPPED = createEscalatorStepShapes(NARROW_ESCALATOR_BOTTOM.get(Direction.SOUTH), Direction.SOUTH, WalkwaySlope.BOTTOM),
            NARROW_ESCALATOR_MIDDLE_STEPPED = createEscalatorStepShapes(NARROW_ESCALATOR_MIDDLE.get(Direction.SOUTH), Direction.SOUTH, WalkwaySlope.MIDDLE),
            NARROW_ESCALATOR_TOP_STEPPED = createEscalatorStepShapes(NARROW_ESCALATOR_TOP.get(Direction.SOUTH), Direction.SOUTH, WalkwaySlope.TOP),
            WIDE_ESCALATOR_SIDE_BOTTOM_LEFT_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_SIDE_BOTTOM_LEFT.get(Direction.SOUTH), Direction.SOUTH, WalkwaySlope.BOTTOM),
            WIDE_ESCALATOR_SIDE_MIDDLE_LEFT_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_SIDE_MIDDLE_LEFT.get(Direction.SOUTH), Direction.SOUTH, WalkwaySlope.MIDDLE),
            WIDE_ESCALATOR_SIDE_TOP_LEFT_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_SIDE_TOP_LEFT.get(Direction.SOUTH), Direction.SOUTH, WalkwaySlope.TOP),
            WIDE_ESCALATOR_SIDE_BOTTOM_RIGHT_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_SIDE_BOTTOM_RIGHT.get(Direction.SOUTH), Direction.SOUTH, WalkwaySlope.BOTTOM),
            WIDE_ESCALATOR_SIDE_MIDDLE_RIGHT_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_SIDE_MIDDLE_RIGHT.get(Direction.SOUTH), Direction.SOUTH, WalkwaySlope.MIDDLE),
            WIDE_ESCALATOR_SIDE_TOP_RIGHT_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_SIDE_TOP_RIGHT.get(Direction.SOUTH), Direction.SOUTH, WalkwaySlope.TOP),
            WIDE_ESCALATOR_CENTER_BOTTOM_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_CENTER_BOTTOM.get(Direction.SOUTH), Direction.SOUTH, WalkwaySlope.BOTTOM),
            WIDE_ESCALATOR_CENTER_MIDDLE_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_CENTER_MIDDLE.get(Direction.SOUTH), Direction.SOUTH, WalkwaySlope.MIDDLE),
            WIDE_ESCALATOR_CENTER_TOP_STEPPED = createEscalatorStepShapes(WIDE_ESCALATOR_CENTER_TOP.get(Direction.SOUTH), Direction.SOUTH, WalkwaySlope.TOP);

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
            shape = Shapes.or(shape, box(14, 6, 8, 16, 16, 16));
        if (right)
            shape = Shapes.or(shape, box(0, 6, 8, 2, 16, 16));
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

    private static VoxelShape createBottomEscalatorHandrailShape(boolean left, boolean right) {
        VoxelShape shape = Shapes.empty();
        if (left) {
            shape = Shapes.or(shape, box(0, 0, 8, 2, 16, 16));
            VoxelShape slice = box(0, 0, 7, 2, 17, 8);
            for (int i = 0; i < 8; ++i) {
                float f = i / 16f;
                shape = Shapes.or(shape, slice.move(0, f, -f));
            }
        }
        if (right) {
            shape = Shapes.or(shape, box(14, 0, 8, 16, 16, 16));
            VoxelShape slice = box(14, 0, 7, 16, 17, 8);
            for (int i = 0; i < 8; ++i) {
                float f = i / 16f;
                shape = Shapes.or(shape, slice.move(0, f, -f));
            }
        }
        return shape;
    }

    private static VoxelShape createMiddleEscalatorHandrailShape(boolean left, boolean right) {
        VoxelShape shape = Shapes.empty();
        if (left) {
            VoxelShape slice = box(0, -8, 15, 2, 9, 16);
            for (int i = 0; i < 16; ++i) {
                float f = i / 16f;
                shape = Shapes.or(shape, slice.move(0, f, -f));
            }
        }
        if (right) {
            VoxelShape slice = box(14, -8, 15, 16, 9, 16);
            for (int i = 0; i < 16; ++i) {
                float f = i / 16f;
                shape = Shapes.or(shape, slice.move(0, f, -f));
            }
        }
        return shape;
    }

    private static VoxelShape createTopEscalatorHandrailShape(boolean left, boolean right) {
        VoxelShape shape = Shapes.empty();
        if (left) {
            shape = Shapes.or(shape, box(0, 0, 0, 2, 16, 8));
            VoxelShape slice = box(0, -1, 8, 2, 16, 9);
            for (int i = 0; i < 8; ++i) {
                float f = i / 16f;
                shape = Shapes.or(shape, slice.move(0, -f, f));
            }
        }
        if (right) {
            shape = Shapes.or(shape, box(14, 0, 0, 16, 16, 8));
            VoxelShape slice = box(14, -1, 8, 16, 16, 9);
            for (int i = 0; i < 8; ++i) {
                float f = i / 16f;
                shape = Shapes.or(shape, slice.move(0, -f, f));
            }
        }
        return shape;
    }

    private EscalatedShapes() {}

}
