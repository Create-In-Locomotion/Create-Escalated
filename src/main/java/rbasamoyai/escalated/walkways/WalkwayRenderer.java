package rbasamoyai.escalated.walkways;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Vector3f;
import rbasamoyai.escalated.index.EscalatedBlockPartials;
import rbasamoyai.escalated.index.EscalatedBlocks;

public class WalkwayRenderer extends KineticBlockEntityRenderer<WalkwayBlockEntity> {

    public WalkwayRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(WalkwayBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization(be.getLevel())) return;
        BlockState state = this.getRenderedBlockState(be);
        KineticBlock kinetic = (KineticBlock) state.getBlock();
        WalkwayBlock walkway = (WalkwayBlock) state.getBlock();
        Direction facing = walkway.getFacing(state);

        Level level = be.getLevel();
        BlockPos pos = be.getBlockPos();

        RenderType type = this.getRenderType(be, state);
        if (type != null) {
            VertexConsumer cons = buffer.getBuffer(type);
            if (kinetic.hasShaftTowards(level, pos, state, Direction.DOWN))
                kineticRotationTransform(this.getHalfShaftRotatedModel(be, state, Direction.DOWN), be, Direction.Axis.Y,
                        getAngleForBe(be, be.getBlockPos(), Direction.Axis.Y), light).renderInto(ms, cons);

            Direction left = Direction.fromAxisAndDirection(kinetic.getRotationAxis(state), Direction.AxisDirection.POSITIVE);
            Direction right = left.getOpposite();
            if (kinetic.hasShaftTowards(level, pos, state, left))
                renderRotatingBuffer(be, this.getHalfShaftRotatedModel(be, state, left), ms, cons, light);
            if (kinetic.hasShaftTowards(level, pos, state, right))
                renderRotatingBuffer(be, this.getHalfShaftRotatedModel(be, state, right), ms, cons, light);

            boolean isTerminal = walkway.getWalkwaySlope(state) == WalkwaySlope.TERMINAL;
            boolean flag = facing == Direction.NORTH || facing == Direction.EAST;
            boolean isController = be.isController();
            Direction stepFacing = isTerminal && isController ? facing.getOpposite() : facing;

            PartialModel stepModel = this.getStepModel(be);
            SuperByteBuffer buf = CachedBuffers.partialFacing(stepModel, state, stepFacing);
            if (!isTerminal || flag) { // Render back step
                buf
                        .light(light)
                        .translate(getStepOffset(be, stepFacing, BlockPos.ZERO, false))
                        .renderInto(ms, cons);
            }
            if (!isTerminal || !flag) { // Render front step
                buf
                        .light(light)
                        .translate(getStepOffset(be, stepFacing, BlockPos.ZERO, true))
                        .renderInto(ms, cons);
            }
        }
    }

    protected SuperByteBuffer getHalfShaftRotatedModel(KineticBlockEntity be, BlockState state, Direction dir) {
        return CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, state, dir);
    }

    /**
     * Override for your own mod's implementation of the walkway block entity.
     */
    protected PartialModel getStepModel(WalkwayBlockEntity be) {
        return baseGetStepModel(be);
    }

    public static Vector3f getStepOffset(WalkwayBlockEntity be, Direction facing, BlockPos pos, boolean frontStep) {
        Direction originalFacing = facing;
        Direction.AxisDirection axisDir = facing.getAxis() == Direction.Axis.X ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE;
        facing = Direction.fromAxisAndDirection(facing.getAxis(), axisDir);
        BlockState state = be.getBlockState();
        WalkwayBlock walkway = (WalkwayBlock) state.getBlock();
        WalkwaySlope slope = walkway.getWalkwaySlope(state);

        Vector3f offset = new Vector3f(pos.getX(), pos.getY() + 15.5f / 16f, pos.getZ());

        float partialTick = be.getSpeed() == 0 ? 0 : AnimationTickHolder.getPartialTicks(be.getLevel());
        float stepOffset = be.getVisualProgress() + partialTick * be.getWalkwayMovementSpeed();
        if (Math.abs(stepOffset) > 0.5f)
            stepOffset = Math.signum(stepOffset) * (Math.abs(stepOffset) - 0.5f);
        if (frontStep)
            stepOffset += 0.5f;
        stepOffset -= 0.25f;
        offset.add(facing.step().mul(stepOffset));

        if (slope == WalkwaySlope.TOP || slope == WalkwaySlope.MIDDLE || slope == WalkwaySlope.BOTTOM) {
            float f = stepOffset - 0.25f;
            if (originalFacing == Direction.NORTH || originalFacing == Direction.EAST) {
                f *= -1;
                f -= 0.5f;
            }
            if (slope == WalkwaySlope.TOP)
                f = Math.min(f, 0);
            if (slope == WalkwaySlope.BOTTOM)
                f = Math.max(f, 0);
            offset.add(0, f, 0);
        }
        return offset;
    }

    public static PartialModel baseGetStepModel(WalkwayBlockEntity be) {
        DyeColor color = be.getColor();
        BlockState state = be.getBlockState();
        Level level = be.getLevel();
        BlockPos pos = be.getBlockPos();

        boolean terminal = false;
        
        // Metal
        if (EscalatedBlocks.METAL_WALKWAY_TERMINAL.has(state)) {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            state = level.getBlockState(pos.relative(facing));
            terminal = true;
        }
        if (EscalatedBlocks.METAL_NARROW_WALKWAY.has(state))
            return EscalatedBlockPartials.DYED_METAL_WALKWAY_STEPS.getOrDefault(color, EscalatedBlockPartials.METAL_WALKWAY_STEP);
        if (EscalatedBlocks.METAL_WIDE_WALKWAY_SIDE.has(state)) {
            boolean left = state.getValue(WideWalkwaySideBlock.LEFT);
            return left ? EscalatedBlockPartials.DYED_METAL_WALKWAY_STEPS_RIGHT.getOrDefault(color, EscalatedBlockPartials.METAL_WALKWAY_STEP_RIGHT)
                    : EscalatedBlockPartials.DYED_METAL_WALKWAY_STEPS_LEFT.getOrDefault(color, EscalatedBlockPartials.METAL_WALKWAY_STEP_LEFT);
        }
        if (EscalatedBlocks.METAL_WIDE_WALKWAY_CENTER.has(state))
            return EscalatedBlockPartials.DYED_METAL_WALKWAY_STEPS_CENTER.getOrDefault(color, EscalatedBlockPartials.METAL_WALKWAY_STEP_CENTER);

        if (EscalatedBlocks.METAL_NARROW_ESCALATOR.has(state))
            return EscalatedBlockPartials.DYED_METAL_ESCALATOR_STEPS.getOrDefault(color, EscalatedBlockPartials.METAL_ESCALATOR_STEP);
        if (EscalatedBlocks.METAL_WIDE_ESCALATOR_SIDE.has(state)) {
            boolean left = state.getValue(WideEscalatorSideBlock.LEFT);
            return left ? EscalatedBlockPartials.DYED_METAL_ESCALATOR_STEPS_RIGHT.getOrDefault(color, EscalatedBlockPartials.METAL_ESCALATOR_STEP_RIGHT)
                    : EscalatedBlockPartials.DYED_METAL_ESCALATOR_STEPS_LEFT.getOrDefault(color, EscalatedBlockPartials.METAL_ESCALATOR_STEP_LEFT);
        }
        if (EscalatedBlocks.METAL_WIDE_ESCALATOR_CENTER.has(state))
            return EscalatedBlockPartials.DYED_METAL_ESCALATOR_STEPS_CENTER.getOrDefault(color, EscalatedBlockPartials.METAL_ESCALATOR_STEP_CENTER);

        // Wooden
        if (EscalatedBlocks.WOODEN_WALKWAY_TERMINAL.has(state)) {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            state = level.getBlockState(pos.relative(facing));
            terminal = true;
        }
        if (EscalatedBlocks.WOODEN_NARROW_WALKWAY.has(state))
            return EscalatedBlockPartials.DYED_WOODEN_WALKWAY_STEPS.getOrDefault(color, EscalatedBlockPartials.WOODEN_WALKWAY_STEP);
        if (EscalatedBlocks.WOODEN_WIDE_WALKWAY_SIDE.has(state)) {
            boolean left = state.getValue(WideWalkwaySideBlock.LEFT);
            return left ? EscalatedBlockPartials.DYED_WOODEN_WALKWAY_STEPS_RIGHT.getOrDefault(color, EscalatedBlockPartials.WOODEN_WALKWAY_STEP_RIGHT)
                    : EscalatedBlockPartials.DYED_WOODEN_WALKWAY_STEPS_LEFT.getOrDefault(color, EscalatedBlockPartials.WOODEN_WALKWAY_STEP_LEFT);
        }
        if (EscalatedBlocks.WOODEN_WIDE_WALKWAY_CENTER.has(state))
            return EscalatedBlockPartials.DYED_WOODEN_WALKWAY_STEPS_CENTER.getOrDefault(color, EscalatedBlockPartials.WOODEN_WALKWAY_STEP_CENTER);

        if (EscalatedBlocks.WOODEN_NARROW_ESCALATOR.has(state))
            return EscalatedBlockPartials.DYED_WOODEN_ESCALATOR_STEPS.getOrDefault(color, EscalatedBlockPartials.WOODEN_ESCALATOR_STEP);
        if (EscalatedBlocks.WOODEN_WIDE_ESCALATOR_SIDE.has(state)) {
            boolean left = state.getValue(WideEscalatorSideBlock.LEFT);
            return left ? EscalatedBlockPartials.DYED_WOODEN_ESCALATOR_STEPS_RIGHT.getOrDefault(color, EscalatedBlockPartials.WOODEN_ESCALATOR_STEP_RIGHT)
                    : EscalatedBlockPartials.DYED_WOODEN_ESCALATOR_STEPS_LEFT.getOrDefault(color, EscalatedBlockPartials.WOODEN_ESCALATOR_STEP_LEFT);
        }
        if (EscalatedBlocks.WOODEN_WIDE_ESCALATOR_CENTER.has(state))
            return EscalatedBlockPartials.DYED_WOODEN_ESCALATOR_STEPS_CENTER.getOrDefault(color, EscalatedBlockPartials.WOODEN_ESCALATOR_STEP_CENTER);

        if (terminal && Backend.canUseInstancing(level))
            be.lazyResetClientRender = true; // Reset next tick
        return EscalatedBlockPartials.DYED_METAL_WALKWAY_STEPS.get(DyeColor.RED); // Troubleshooting
    }

}
