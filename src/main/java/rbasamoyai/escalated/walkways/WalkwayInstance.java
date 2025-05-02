package rbasamoyai.escalated.walkways;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;

import static rbasamoyai.escalated.walkways.WalkwayRenderer.getStepOffset;

public class WalkwayInstance extends KineticBlockEntityInstance<WalkwayBlockEntity> implements DynamicInstance {

    private RotatingData leftShaft;
    private RotatingData rightShaft;
    private RotatingData bottomShaft;
    private OrientedData backStep;
    private OrientedData frontStep;
    private DyeColor color;

    public WalkwayInstance(MaterialManager materialManager, WalkwayBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    public void init() {
        super.init();
        KineticBlock kinetic = (KineticBlock) this.blockState.getBlock();
        WalkwayBlock walkway = (WalkwayBlock) this.blockState.getBlock();
        Level level = this.blockEntity.getLevel();
        Direction left = Direction.fromAxisAndDirection(kinetic.getRotationAxis(this.blockState), Direction.AxisDirection.POSITIVE);
        Direction right = left.getOpposite();

        if (kinetic.hasShaftTowards(level, this.pos, this.blockState, left)) {
            this.leftShaft = this.setup(this.getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, this.blockState, left)
                    .createInstance(), left.getAxis());
        } else {
            this.leftShaft = null;
        }
        if (kinetic.hasShaftTowards(level, this.pos, this.blockState, right)) {
            this.rightShaft = this.setup(this.getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, this.blockState, right)
                    .createInstance(), right.getAxis());
        } else {
            this.rightShaft = null;
        }
        if (kinetic.hasShaftTowards(level, this.pos, this.blockState, Direction.DOWN)) {
            this.bottomShaft = this.setup(this.getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, this.blockState, Direction.DOWN)
                    .createInstance(), Direction.Axis.Y);
        } else {
            this.bottomShaft = null;
        }

        boolean isTerminal = walkway.getWalkwaySlope(this.blockState) == WalkwaySlope.TERMINAL;
        boolean isController = this.blockEntity.isController();
        Direction facing = this.getFacing();
        BlockPos pos1 = this.getInstancePosition();

        this.color = this.blockEntity.getColor();

        PartialModel model = this.getStepModel();
        if (isTerminal) {
            boolean flag = facing == Direction.NORTH || facing == Direction.EAST;
            facing = isController ? facing.getOpposite() : facing;
            if (flag) {
                this.backStep = this.getOrientedMaterial().getModel(model, this.blockState, facing)
                        .createInstance().setPosition(getStepOffset(this.blockEntity, facing, pos1, false));
            } else {
                this.frontStep = this.getOrientedMaterial().getModel(model, this.blockState, facing)
                        .createInstance().setPosition(getStepOffset(this.blockEntity, facing, pos1, true));
            }
        } else {
            this.frontStep = this.getOrientedMaterial().getModel(model, this.blockState, facing)
                    .createInstance().setPosition(getStepOffset(this.blockEntity, facing, pos1, true));
            this.backStep = this.getOrientedMaterial().getModel(model, this.blockState, facing)
                    .createInstance().setPosition(getStepOffset(this.blockEntity, facing, pos1, false));
        }
    }

    @Override
    public void remove() {
        if (this.leftShaft != null)
            this.leftShaft.delete();
        this.leftShaft = null;
        if (this.rightShaft != null)
            this.rightShaft.delete();
        this.rightShaft = null;
        if (this.bottomShaft != null)
            this.bottomShaft.delete();
        this.bottomShaft = null;
        if (this.backStep != null)
            this.backStep.delete();
        this.backStep = null;
        if (this.frontStep != null)
            this.frontStep.delete();
        this.frontStep = null;
    }

    protected Direction getFacing() {
        return ((WalkwayBlock) this.blockState.getBlock()).getFacing(this.blockState);
    }

    @Override
    public void beginFrame() {
        Direction facing = this.getFacing();
        BlockPos pos = this.getInstancePosition();
        if (this.frontStep != null)
            this.frontStep.setPosition(getStepOffset(this.blockEntity, facing, pos, true));
        if (this.backStep != null)
            this.backStep.setPosition(getStepOffset(this.blockEntity, facing, pos, false));
    }

    @Override
    public void update() {
        super.update();
        if (this.leftShaft != null)
            this.updateRotation(this.leftShaft);
        if (this.rightShaft != null)
            this.updateRotation(this.rightShaft);
        if (this.bottomShaft != null)
            this.updateRotation(this.bottomShaft, Direction.Axis.Y);
    }

    @Override
    public boolean shouldReset() {
        if (super.shouldReset())
            return true;
        if (this.blockEntity.getColor() != this.color)
            return true;
        if (this.blockEntity.resetClientRender) {
            this.blockEntity.resetClientRender = false;
            return true;
        }
        return false;
    }

    @Override
    public void updateLight() {
        super.updateLight();
        if (this.leftShaft != null)
            this.relight(this.pos, this.leftShaft);
        if (this.rightShaft != null)
            this.relight(this.pos, this.rightShaft);
        if (this.bottomShaft != null)
            this.relight(this.pos, this.bottomShaft);
        if (this.backStep != null)
            this.relight(this.pos, this.backStep);
        if (this.frontStep != null)
            this.relight(this.pos, this.frontStep);
    }

    /**
     * Override for your own mod's implementation of the walkway block entity.
     */
    protected PartialModel getStepModel() { return WalkwayRenderer.baseGetStepModel(this.blockEntity); }

}
