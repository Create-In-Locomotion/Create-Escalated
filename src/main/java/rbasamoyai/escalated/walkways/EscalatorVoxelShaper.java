package rbasamoyai.escalated.walkways;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.level.block.Block.box;

public class EscalatorVoxelShaper {

    private static final int STEP_COUNT = 32; // 1 step => 1/2 block => 1/64 resolution

    protected final List<VoxelShaper> shapes;

    protected EscalatorVoxelShaper(List<VoxelShaper> shapes) {
        this.shapes = shapes;
    }

    public VoxelShape getShape(float visualOffset, Direction facing) {
        int sz = this.shapes.size();
        if (facing == Direction.NORTH || facing == Direction.EAST)
            visualOffset *= -1;
        int index = Mth.floor(((visualOffset % 0.5f) + 1) % 0.5f / 0.5f * sz) % sz; // Normalize value to [0, sz)
        return this.shapes.get(index).get(facing);
    }

    public static EscalatorVoxelShaper createEscalatorStepShapes(VoxelShape baseShape, Direction facing, WalkwaySlope slope) {
        return createEscalatorStepShapes(baseShape, facing, slope, STEP_COUNT);
    }

    public static EscalatorVoxelShaper createEscalatorStepShapes(VoxelShape baseShape, Direction facing, WalkwaySlope slope, int stepCount) {
        if (facing.getAxis().isVertical())
            throw new IllegalStateException("Cannot initialize escalator step shaper with facing={UP,DOWN}");
        if (slope == WalkwaySlope.HORIZONTAL || slope == WalkwaySlope.TERMINAL)
            throw new IllegalStateException("Cannot initialize escalator step shaper with slope={HORIZONTAL,TERMINAL}");

        List<VoxelShaper> shapes = new ArrayList<>();

        VoxelShape stepShape = box(0, 0, 0, 16, 8, 8);
        Vector3f dirVec = Direction.SOUTH.step();
        VoxelShape mask = box(0, -16, 0, 16, 32, 16); // 3-tall box, centered

        for (int offset = 0; offset < stepCount; ++offset) {
            float visualOffset = 0.5f * (float) offset / (float) stepCount;
            VoxelShape finalShape = baseShape;
            for (int step = -1; step < 2; ++step) { // One step down, two steps up
                float f = step * 0.5f + visualOffset; // in blocks
                Vector3f horizOffset = new Vector3f(dirVec).mul(f);
                float vertOffset = f;
                if (slope == WalkwaySlope.TOP)
                    vertOffset = Math.min(vertOffset, 0.5f);
                if (slope == WalkwaySlope.BOTTOM)
                    vertOffset = Math.max(vertOffset, 0.5f);
                Vector3f offsetVec = new Vector3f().add(horizOffset).add(0, vertOffset - 0.5f / 16f, 0);
                VoxelShape copyStep = stepShape.move(offsetVec.x, offsetVec.y, offsetVec.z);
                copyStep = Shapes.join(copyStep, mask, BooleanOp.AND);
                finalShape = Shapes.or(finalShape, copyStep);
            }
            shapes.add(VoxelShaper.forHorizontal(finalShape, Direction.SOUTH));
        }
        return new EscalatorVoxelShaper(shapes);
    }

}
