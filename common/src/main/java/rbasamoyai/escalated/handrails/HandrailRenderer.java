package rbasamoyai.escalated.handrails;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.escalated.index.EscalatedBlockPartials;
import rbasamoyai.escalated.index.EscalatedSpriteShiftEntries;
import rbasamoyai.escalated.walkways.WalkwaySlope;

import javax.annotation.Nullable;

public class HandrailRenderer extends SafeBlockEntityRenderer<HandrailBlockEntity> {

    public HandrailRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(HandrailBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (Backend.canUseInstancing(be.getLevel()))
            return;
        BlockState state = be.getBlockState();
        if (!(state.getBlock() instanceof AbstractHandrailBlock handrailBlock))
            return;
        VertexConsumer vcons = bufferSource.getBuffer(RenderType.solid());

        WalkwaySlope slope = handrailBlock.getHandrailSlope(state);
        boolean end = handrailBlock.isEndHandrail(state);
        AbstractHandrailBlock.Side side = state.getValue(AbstractHandrailBlock.SIDE);
        Direction facing = state.getValue(AbstractHandrailBlock.FACING);
        DyeColor handrailColor = be.getHandrailColor();

        PartialModel model = getHandrailModel(slope, end);
        SuperByteBuffer buf = CachedBufferer.partialFacing(model, state, facing.getOpposite());
        SpriteShiftEntry spriteShift = getSpriteShift(handrailColor);

        float scroll = getScrollOffset(be, partialTicks, facing, spriteShift.getTarget().getV0(), spriteShift.getTarget().getV1());

        // Render based on sides, applying appropriate transforms
        if (side == AbstractHandrailBlock.Side.LEFT || side == AbstractHandrailBlock.Side.BOTH) {
            ms.pushPose();
            Direction left = facing.getCounterClockWise();
            ms.translate(left.getStepX() * 7 / 16f, 0, left.getStepZ() * 7 / 16f);
            buf.light(light)
                    .shiftUVScrolling(spriteShift, scroll)
                    .renderInto(ms, vcons);
            ms.popPose();
        }
        if (side == AbstractHandrailBlock.Side.RIGHT || side == AbstractHandrailBlock.Side.BOTH) {
            ms.pushPose();
            Direction right = facing.getClockWise();
            ms.translate(right.getStepX() * 7 / 16f, 0, right.getStepZ() * 7 / 16f);
            buf.light(light)
                    .shiftUVScrolling(spriteShift, scroll)
                    .renderInto(ms, vcons);
            ms.popPose();
        }
    }

    public static PartialModel getHandrailModel(WalkwaySlope slope, boolean end) {
        return switch (slope) {
            case HORIZONTAL -> EscalatedBlockPartials.HANDRAIL_HORIZONTAL;
            case BOTTOM -> EscalatedBlockPartials.HANDRAIL_BOTTOM;
            case MIDDLE -> EscalatedBlockPartials.HANDRAIL_MIDDLE;
            case TOP -> EscalatedBlockPartials.HANDRAIL_TOP;
            case TERMINAL -> end ? EscalatedBlockPartials.HANDRAIL_END : EscalatedBlockPartials.HANDRAIL_START;
        };
    }

    public static SpriteShiftEntry getSpriteShift(@Nullable DyeColor color) {
        return color == null ? EscalatedSpriteShiftEntries.HANDRAIL :
                EscalatedSpriteShiftEntries.DYED_HANDRAILS.getOrDefault(color, EscalatedSpriteShiftEntries.HANDRAIL);
    }

    public static float getScrollOffset(HandrailBlockEntity be, float partialTicks, Direction facing, float v0, float v1) {
        float speed = be.getSpeed();
        float stepOffset = be.getVisualProgress() + partialTicks * speed;
        if (facing == Direction.NORTH || facing == Direction.EAST)
            stepOffset *= -1;
        stepOffset += 1;
        return (v1 - v0) * stepOffset / 3f;
    }

}
