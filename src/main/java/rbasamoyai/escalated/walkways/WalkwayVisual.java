package rbasamoyai.escalated.walkways;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static rbasamoyai.escalated.walkways.WalkwayRenderer.getStepOffset;

public class WalkwayVisual extends KineticBlockEntityVisual<WalkwayBlockEntity> implements SimpleDynamicVisual {

    private RotatingInstance leftShaft;
    private RotatingInstance rightShaft;
    private RotatingInstance bottomShaft;
    private OrientedInstance backStep;
    private OrientedInstance frontStep;
    private DyeColor color;

    public WalkwayVisual(VisualizationContext ctx, WalkwayBlockEntity blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick);
        KineticBlock kinetic = (KineticBlock) this.blockState.getBlock();
        WalkwayBlock walkway = (WalkwayBlock) this.blockState.getBlock();
        Level level = this.blockEntity.getLevel();
        Direction left = Direction.fromAxisAndDirection(kinetic.getRotationAxis(this.blockState), Direction.AxisDirection.POSITIVE);
        Direction right = left.getOpposite();

        if (kinetic.hasShaftTowards(level, this.pos, this.blockState, left)) {
            this.leftShaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF, left)).createInstance();
            this.leftShaft.setup(blockEntity, left.getAxis()).setChanged();
        } else {
            this.leftShaft = null;
        }
        if (kinetic.hasShaftTowards(level, this.pos, this.blockState, right)) {
            this.rightShaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF, right)).createInstance();
            this.rightShaft.setup(blockEntity, right.getAxis()).setChanged();
        } else {
            this.rightShaft = null;
        }
        if (kinetic.hasShaftTowards(level, this.pos, this.blockState, Direction.DOWN)) {
            this.bottomShaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF, Direction.DOWN)).createInstance();
            this.bottomShaft.setup(blockEntity, Direction.Axis.Y).setChanged();
        } else {
            this.bottomShaft = null;
        }

        boolean isTerminal = walkway.getWalkwaySlope(this.blockState) == WalkwaySlope.TERMINAL;
        boolean isController = this.blockEntity.isController();
        Direction facing = this.getFacing();
        BlockPos pos1 = this.getVisualPosition();

        this.color = this.blockEntity.getColor();

        PartialModel model = this.getStepModel();
        if (isTerminal) {
            boolean flag = facing == Direction.NORTH || facing == Direction.EAST;
            facing = isController ? facing.getOpposite() : facing;
            if (flag) {
                this.backStep = instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(model, facing)).createInstance();
                this.backStep.position(getStepOffset(this.blockEntity, facing, pos1, false)).setChanged();
            } else {
                this.frontStep = instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(model, facing)).createInstance();
                this.frontStep.position(getStepOffset(this.blockEntity, facing, pos1, true)).setChanged();
            }
        } else {
            this.frontStep = instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(model, facing)).createInstance();
            this.frontStep.position(getStepOffset(this.blockEntity, facing, pos1, true)).setChanged();

            this.backStep = instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(model, facing)).createInstance();
            this.backStep.position(getStepOffset(this.blockEntity, facing, pos1, false)).setChanged();
        }
    }

    @Override
    public void _delete() {
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
    public void beginFrame(DynamicVisual.Context ctx) {
        Direction facing = this.getFacing();
        BlockPos pos = this.getVisualPosition();
        if (this.frontStep != null)
            this.frontStep.position(getStepOffset(this.blockEntity, facing, pos, true)).setChanged();
        if (this.backStep != null)
            this.backStep.position(getStepOffset(this.blockEntity, facing, pos, false)).setChanged();
    }

    @Override
    public void update(float partialTick) {
        super.update(partialTick);
        // fixme
        /*if (this.leftShaft != null)
            this.updateRotation(this.leftShaft);
        if (this.rightShaft != null)
            this.updateRotation(this.rightShaft);
        if (this.bottomShaft != null)
            this.updateRotation(this.bottomShaft, Direction.Axis.Y);*/
    }

    // fixme
    /*@Override
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
    }*/

    @Override
    public void updateLight(float partialTick) {
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

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(leftShaft);
        consumer.accept(rightShaft);
        consumer.accept(bottomShaft);
        consumer.accept(frontStep);
        consumer.accept(backStep);
    }
}
