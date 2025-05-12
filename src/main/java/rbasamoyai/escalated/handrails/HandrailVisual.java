package rbasamoyai.escalated.handrails;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import rbasamoyai.escalated.index.EscalatedInstanceTypes;
import rbasamoyai.escalated.walkways.WalkwaySlope;

import java.util.function.Consumer;

public class HandrailVisual extends AbstractBlockEntityVisual<HandrailBlockEntity> implements SimpleDynamicVisual {

    protected final HandrailInstance leftHandrail;
    protected final HandrailInstance rightHandrail;
    private final Direction facing;
    private float v0;
    private float v1;
    private DyeColor handrailColor;

    public HandrailVisual(VisualizationContext ctx, HandrailBlockEntity blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick);

        AbstractHandrailBlock.Side side = this.blockState.getValue(AbstractHandrailBlock.SIDE);
        this.facing = this.blockState.getValue(AbstractHandrailBlock.FACING);

        this.handrailColor = this.blockEntity.getHandrailColor();

        SpriteShiftEntry spriteShift = HandrailRenderer.getSpriteShift(this.handrailColor);
        this.v0 = spriteShift.getTarget().getV0();
        this.v1 = spriteShift.getTarget().getV1();

        Instancer<HandrailInstance> instancer = this.getHandrailInstancer();
        if (side == AbstractHandrailBlock.Side.LEFT || side == AbstractHandrailBlock.Side.BOTH) {
            this.leftHandrail = this.setup(instancer.createInstance(), true, spriteShift);
        } else {
            this.leftHandrail = this.getEmptyHandrailInstancer().createInstance();
        }
        if (side == AbstractHandrailBlock.Side.RIGHT || side == AbstractHandrailBlock.Side.BOTH) {
            this.rightHandrail = this.setup(instancer.createInstance(), false, spriteShift);
        } else {
            this.rightHandrail = this.getEmptyHandrailInstancer().createInstance();
        }
    }

    private Instancer<HandrailInstance> getHandrailInstancer() {
        AbstractHandrailBlock handrail = (AbstractHandrailBlock) this.blockState.getBlock();
        WalkwaySlope slope = handrail.getHandrailSlope(this.blockState);
        boolean end = handrail.isEndHandrail(this.blockState);
        return this.instancerProvider().instancer(EscalatedInstanceTypes.HANDRAIL,
                Models.partial(HandrailRenderer.getHandrailModel(slope, end), this.facing.getOpposite()));
    }

    private Instancer<HandrailInstance> getEmptyHandrailInstancer() {
        return this.instancerProvider().instancer(EscalatedInstanceTypes.HANDRAIL, Models.block(Blocks.AIR.defaultBlockState()));
    }

    private void resetHandrailTexture() {
        AbstractHandrailBlock.Side side = this.blockState.getValue(AbstractHandrailBlock.SIDE);

        SpriteShiftEntry spriteShift = HandrailRenderer.getSpriteShift(this.handrailColor);
        this.v0 = spriteShift.getTarget().getV0();
        this.v1 = spriteShift.getTarget().getV1();

        if (side == AbstractHandrailBlock.Side.LEFT || side == AbstractHandrailBlock.Side.BOTH)
            this.leftHandrail.setScrollTexture(spriteShift).setChanged();
        if (side == AbstractHandrailBlock.Side.RIGHT || side == AbstractHandrailBlock.Side.BOTH)
            this.rightHandrail.setScrollTexture(spriteShift).setChanged();
    }

    private HandrailInstance setup(HandrailInstance data, boolean left, SpriteShiftEntry spriteShift) {
        Direction offset = left ? this.facing.getCounterClockWise() : this.facing.getClockWise();
        data.setScrollTexture(spriteShift)
                .setScrollOffset(0)
                .position(this.getVisualPosition())
                .translatePosition(offset.getStepX() * 7 / 16f, 0, offset.getStepZ() * 7 / 16f);
        return data;
    }

    @Override
    public void beginFrame(DynamicVisual.Context ctx) {
        float partialTick = ctx.partialTick();
        float scroll = HandrailRenderer.getScrollOffset(this.blockEntity, partialTick, this.facing, this.v0, this.v1);
        this.leftHandrail.setScrollOffset(scroll).setChanged();
        this.rightHandrail.setScrollOffset(scroll).setChanged();
    }

    @Override
    public void update(float partialTick) {
        super.update(partialTick);
        if (this.blockEntity.getHandrailColor() != this.handrailColor) {
            this.handrailColor = this.blockEntity.getHandrailColor();
            this.resetHandrailTexture();
        }
    }

    @Override
    public void updateLight(float partialTick) {
        this.relight(this.pos, this.leftHandrail);
        this.relight(this.pos, this.rightHandrail);
    }

    @Override
    protected void _delete() {
        this.leftHandrail.delete();
        this.rightHandrail.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(this.leftHandrail);
        consumer.accept(this.rightHandrail);
    }

}
