package rbasamoyai.escalated.walkways;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.escalated.index.EscalatedBlockPartials;

public class WalkwayTerminalRenderer extends KineticBlockEntityRenderer<WalkwayTerminalBlockEntity> {

    public WalkwayTerminalRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(WalkwayTerminalBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (Backend.canUseInstancing(be.getLevel()))
            return;
        BlockState state = this.getRenderedBlockState(be);
        RenderType type = this.getRenderType(be, state);
        if (type != null) {
            VertexConsumer cons = buffer.getBuffer(type);
            kineticRotationTransform(this.getHalfShaftRotatedModel(be, state, Direction.DOWN), be, Direction.Axis.Y,
                    getAngleForTe(be, be.getBlockPos(), Direction.Axis.Y), light).renderInto(ms, cons);

            Direction facing = state.getValue(WalkwayTerminalBlock.HORIZONTAL_FACING);
            WalkwayTerminalBlock.Caps caps = state.getValue(WalkwayTerminalBlock.CAPS);
            if (!caps.hasLeftCap())
                renderRotatingBuffer(be, this.getHalfShaftRotatedModel(be, state, facing.getCounterClockWise()), ms, cons, light);
            if (!caps.hasRightCap())
                renderRotatingBuffer(be, this.getHalfShaftRotatedModel(be, state, facing.getClockWise()), ms, cons, light);
        }

        // TODO render steps
        // TODO render dyed steps
        // TODO render wooden steps
    }

    protected SuperByteBuffer getHalfShaftRotatedModel(KineticBlockEntity be, BlockState state, Direction dir) {
        return CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, state, dir);
    }

}
