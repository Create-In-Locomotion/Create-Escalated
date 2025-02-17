package rbasamoyai.escalated.walkways;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import rbasamoyai.escalated.index.EscalatedBlockPartials;

public class WalkwayRenderer extends KineticBlockEntityRenderer<WalkwayBlockEntity> {

    public WalkwayRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(WalkwayBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (Backend.canUseInstancing(be.getLevel()))
            return;
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
                        getAngleForTe(be, be.getBlockPos(), Direction.Axis.Y), light).renderInto(ms, cons);

            Direction left = Direction.fromAxisAndDirection(kinetic.getRotationAxis(state), Direction.AxisDirection.POSITIVE);
            Direction right = left.getOpposite();
            if (kinetic.hasShaftTowards(level, pos, state, left))
                renderRotatingBuffer(be, this.getHalfShaftRotatedModel(be, state, left), ms, cons, light);
            if (kinetic.hasShaftTowards(level, pos, state, right))
                renderRotatingBuffer(be, this.getHalfShaftRotatedModel(be, state, right), ms, cons, light);

            // TODO render steps
            boolean isTerminal = walkway.isTerminal(state);
            boolean flag = facing == Direction.NORTH || facing == Direction.EAST;
            boolean isController = be.isController();
            Direction stepFacing = isTerminal && isController ? facing.getOpposite() : facing;

            PartialModel stepModel = this.getStepModel(be);
            if (!isTerminal || flag) { // Render back step
                CachedBufferer.partialFacing(stepModel, state, stepFacing)
                        .light(light)
                        .translate(getStepOffset(be, stepFacing, BlockPos.ZERO, false))
                        .renderInto(ms, cons);
            }
            if (!isTerminal || !flag) { // Render front step
                CachedBufferer.partialFacing(stepModel, state, stepFacing)
                        .light(light)
                        .translate(getStepOffset(be, stepFacing, BlockPos.ZERO, true))
                        .renderInto(ms, cons);
            }
        }
    }

    protected SuperByteBuffer getHalfShaftRotatedModel(KineticBlockEntity be, BlockState state, Direction dir) {
        return CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, state, dir);
    }

    /**
     * Override for your own mod's implementation of the walkway block entity.
     */
    protected PartialModel getStepModel(WalkwayBlockEntity be) {
        return baseGetStepModel(be);
    }

    public static Vector3f getStepOffset(WalkwayBlockEntity be, Direction facing, BlockPos pos, boolean frontStep) {
        // TODO escalator
        Direction.AxisDirection axisDir = facing.getAxis() == Direction.Axis.X ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE;
        facing = Direction.fromAxisAndDirection(facing.getAxis(), axisDir);

        Vector3f offset = new Vector3f(pos.getX(), pos.getY() + 15.5f / 16f, pos.getZ());

        float partialTick = be.getSpeed() == 0 ? 0 : AnimationTickHolder.getPartialTicks(be.getLevel());
        float stepOffset = be.getVisualProgress() + partialTick * be.getWalkwayMovementSpeed();
        if (Math.abs(stepOffset) > 0.5f)
            stepOffset = Math.signum(stepOffset) * (Math.abs(stepOffset) - 0.5f);
        if (frontStep)
            stepOffset += 0.5f;
        stepOffset -= 0.25f;
        offset.add(facing.step().mul(stepOffset));
        return offset;
    }

    public static PartialModel baseGetStepModel(WalkwayBlockEntity be) {
        return EscalatedBlockPartials.METAL_WALKWAY_STEP;

        // TODO wide steps
        // TODO escalator steps
        // TODO dyed steps
        // TODO wooden steps
    }

}
