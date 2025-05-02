package rbasamoyai.escalated.walkways;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static net.minecraft.core.Direction.AxisDirection.NEGATIVE;
import static net.minecraft.core.Direction.AxisDirection.POSITIVE;
import static net.minecraft.world.entity.MoverType.SELF;

/**
 * Adapted from {@link com.simibubi.create.content.kinetics.belt.transport.BeltMovementHandler}
 */
public class WalkwayMovementHandler {

    public static class TransportedEntityInfo {
        int ticksSinceLastCollision;
        BlockPos lastCollidedPos;
        BlockState lastCollidedState;

        public TransportedEntityInfo(BlockPos collision, BlockState walkway) { this.refresh(collision, walkway); }

        public void refresh(BlockPos collision, BlockState walkway) {
            this.ticksSinceLastCollision = 0;
            this.lastCollidedPos = new BlockPos(collision).immutable();
            this.lastCollidedState = walkway;
        }

        public TransportedEntityInfo tick() {
            this.ticksSinceLastCollision++;
            return this;
        }

        public int getTicksSinceLastCollision() { return this.ticksSinceLastCollision; }
    }

    public static boolean canBeTransported(Entity entity) {
        return entity.isAlive() && (!(entity instanceof Player player) || !player.isShiftKeyDown());
    }

    public static void transportEntity(WalkwayBlockEntity walkwayBE, Entity entity, TransportedEntityInfo info) {
        BlockPos pos = info.lastCollidedPos;
        Level level = walkwayBE.getLevel();
        BlockEntity otherBE = level.getBlockEntity(pos);

        BlockPos belowPos = BlockPos.containing(entity.position().subtract(0, 0.05, 0));
        BlockEntity beBelowPassenger = level.getBlockEntity(belowPos);
        BlockState blockState = info.lastCollidedState;
        WalkwayBlock walkwayBlock = (WalkwayBlock) blockState.getBlock();
        Direction movementFacing = Direction.fromAxisAndDirection(walkwayBlock.getFacing(blockState).getAxis(),
                walkwayBE.getSpeed() < 0 ? POSITIVE : NEGATIVE);

        boolean collidedWithWalkways = otherBE instanceof WalkwayBlockEntity;
        boolean betweenWalkways = beBelowPassenger instanceof WalkwayBlockEntity && beBelowPassenger != otherBE;

        // Don't fight other walkways
        if (!collidedWithWalkways || betweenWalkways)
            return;

        // Too slow or doesn't move (e.g. terminals)
        if (Math.abs(walkwayBE.getSpeed()) < 1 || !walkwayBlock.movesEntities(blockState))
            return;

        // Not on top
        if (entity.getY() + 0.25f < pos.getY())
            return;

        // Lock entities in place
        boolean isPlayer = entity instanceof Player;
        if (entity instanceof LivingEntity living && !isPlayer)
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 1, false, false));

        final Direction walkwayFacing = walkwayBlock.getFacing(blockState);
        final WalkwaySlope slope = walkwayBlock.getWalkwaySlope(blockState);
        final Direction.Axis axis = walkwayFacing.getAxis();
        float movementSpeed = walkwayBE.getWalkwayMovementSpeed();
        final Direction movementDirection = Direction.get(axis == Direction.Axis.X ? NEGATIVE : POSITIVE, axis);

        Vec3i centeringDirection = Direction.get(POSITIVE, walkwayFacing.getClockWise().getAxis()).getNormal();
        Vec3 movement = Vec3.atLowerCornerOf(movementDirection.getNormal()).scale(movementSpeed);

        double diffCenter = axis == Direction.Axis.Z ? (pos.getX() + .5f - entity.getX()) : (pos.getZ() + .5f - entity.getZ());
        if (Math.abs(diffCenter) > 48 / 64f)
            return;

        float top = 15.5f / 16f;
        boolean onSlope = slope == WalkwaySlope.MIDDLE || slope == WalkwaySlope.TOP && entity.getY() - pos.getY() < top
                || slope == WalkwaySlope.BOTTOM && entity.getY() - pos.getY() > top;

        boolean movingDown = onSlope && movementFacing != walkwayFacing;
        boolean movingUp = onSlope && movementFacing == walkwayFacing;

        if (walkwayFacing.getAxis() == Direction.Axis.Z) {
            boolean b = movingDown;
            movingDown = movingUp;
            movingUp = b;
        }

        if (movingUp)
            movement = movement.add(0, Math.abs(axis.choose(movement.x, movement.y, movement.z)), 0);
        if (movingDown)
            movement = movement.add(0, -Math.abs(axis.choose(movement.x, movement.y, movement.z)), 0);

        Vec3 centering = Vec3.atLowerCornerOf(centeringDirection).scale(diffCenter * Math.min(Math.abs(movementSpeed), .1f) * 4);

        if (!(entity instanceof LivingEntity living) || living.zza == 0 && living.xxa == 0)
            movement = movement.add(centering);

        float step = entity.maxUpStep();
        if (!isPlayer)
            entity.setMaxUpStep(1);

        // Entity Collisions
        if (Math.abs(movementSpeed) < .5f) {
            Vec3 checkDistance = movement.normalize()
                    .scale(0.5);
            AABB bb = entity.getBoundingBox();
            AABB checkBB = new AABB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
            checkBB = checkBB.move(checkDistance)
                    .inflate(-Math.abs(checkDistance.x), -Math.abs(checkDistance.y), -Math.abs(checkDistance.z));
            List<Entity> list = level.getEntities(entity, checkBB);
            list.removeIf(e -> shouldIgnoreBlocking(entity, e));
            if (!list.isEmpty()) {
                entity.setDeltaMovement(0, 0, 0);
                info.ticksSinceLastCollision--;
                return;
            }
        }

        entity.fallDistance = 0;

        if (movingUp) {
            float minVelocity = .13f;
            float yMovement = (float) (Math.max(Math.abs(movement.y), minVelocity));
            entity.move(SELF, new Vec3(0, yMovement, 0));
            entity.move(SELF, new Vec3(0, -yMovement * 2, 0));
            entity.move(SELF, movement.multiply(1, 0, 1));
        } else if (movingDown) {
            entity.move(SELF, movement.multiply(1, 0, 1));
            entity.move(SELF, movement.multiply(0, 1, 0));
        } else {
            entity.move(SELF, movement);
        }

        // Placement on steps
        entity.setOnGround(true);

        if (!isPlayer)
            entity.setMaxUpStep(step);
    }

    public static boolean shouldIgnoreBlocking(Entity me, Entity other) {
        if (other instanceof HangingEntity)
            return true;
        if (other.getPistonPushReaction() == PushReaction.IGNORE)
            return true;
        return isRidingOrBeingRiddenBy(me, other);
    }

    public static boolean isRidingOrBeingRiddenBy(Entity me, Entity other) {
        for (Entity entity : me.getPassengers()) {
            if (entity.equals(other))
                return true;
            if (isRidingOrBeingRiddenBy(entity, other))
                return true;
        }
        return false;
    }

}
