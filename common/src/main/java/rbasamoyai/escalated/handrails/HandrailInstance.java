package rbasamoyai.escalated.handrails;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LightLayer;
import rbasamoyai.escalated.index.EscalatedMaterialSpecs;
import rbasamoyai.escalated.walkways.WalkwaySlope;

public class HandrailInstance extends BlockEntityInstance<HandrailBlockEntity> implements DynamicInstance {

    protected HandrailData leftHandrail;
    protected HandrailData rightHandrail;
    private float v0;
    private float v1;
    private Direction facing;
    private DyeColor handrailColor;

    public HandrailInstance(MaterialManager materialManager, HandrailBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    public void init() {
        super.init();

        AbstractHandrailBlock handrail = (AbstractHandrailBlock) this.blockState.getBlock();
        WalkwaySlope slope = handrail.getHandrailSlope(this.blockState);
        boolean end = handrail.isEndHandrail(this.blockState);
        AbstractHandrailBlock.Side side = this.blockState.getValue(AbstractHandrailBlock.SIDE);

        this.handrailColor = this.blockEntity.getHandrailColor();

        PartialModel partial = HandrailRenderer.getHandrailModel(slope, end);
        SpriteShiftEntry spriteShift = HandrailRenderer.getSpriteShift(this.handrailColor);
        Instancer<HandrailData> handrailModel = this.materialManager.defaultSolid()
                .material(EscalatedMaterialSpecs.HANDRAIL)
                .getModel(partial, this.blockState);
        this.v0 = spriteShift.getTarget().getV0();
        this.v1 = spriteShift.getTarget().getV1();
        this.facing = this.blockState.getValue(AbstractHandrailBlock.FACING);

        if (side == AbstractHandrailBlock.Side.LEFT || side == AbstractHandrailBlock.Side.BOTH) {
            this.leftHandrail = this.setup(handrailModel.createInstance(), true, spriteShift);
        }
        if (side == AbstractHandrailBlock.Side.RIGHT || side == AbstractHandrailBlock.Side.BOTH) {
            this.rightHandrail = this.setup(handrailModel.createInstance(), false, spriteShift);
        }
    }

    private HandrailData setup(HandrailData data, boolean left, SpriteShiftEntry spriteShift) {
        Direction offset = left ? this.facing.getCounterClockWise() : this.facing.getClockWise();
        data.setScrollTexture(spriteShift)
                .setScrollOffset(0)
                .setRotation(Axis.YN.rotationDegrees(this.facing.getOpposite().toYRot()))
                .setPosition(this.getInstancePosition())
                .nudge(offset.getStepX() * 7 / 16f, 0, offset.getStepZ() * 7 / 16f)
                .setBlockLight(this.world.getBrightness(LightLayer.BLOCK, this.pos))
                .setSkyLight(this.world.getBrightness(LightLayer.SKY, this.pos));
        return data;
    }

    @Override
    public void beginFrame() {
        float partialTick = AnimationTickHolder.getPartialTicks(this.world);
        float scroll = HandrailRenderer.getScrollOffset(this.blockEntity, partialTick, this.facing, this.v0, this.v1);
        if (this.leftHandrail != null)
            this.leftHandrail.setScrollOffset(scroll);
        if (this.rightHandrail != null)
            this.rightHandrail.setScrollOffset(scroll);
    }

    @Override
    public boolean shouldReset() {
        if (super.shouldReset())
            return true;
        return this.blockEntity.getHandrailColor() != this.handrailColor;
    }

    @Override
    public void updateLight() {
        super.updateLight();
        if (this.leftHandrail != null)
            this.relight(this.pos, this.leftHandrail);
        if (this.rightHandrail != null)
            this.relight(this.pos, this.rightHandrail);
    }

    @Override
    protected void remove() {
        if (this.leftHandrail != null)
            this.leftHandrail.delete();
        this.leftHandrail = null;
        if (this.rightHandrail != null)
            this.rightHandrail.delete();
        this.rightHandrail = null;
    }

}
