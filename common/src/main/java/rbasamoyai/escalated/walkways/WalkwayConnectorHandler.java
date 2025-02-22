package rbasamoyai.escalated.walkways;

import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Adapted from {@link com.simibubi.create.content.kinetics.belt.item.BeltConnectorHandler}.
 */
public class WalkwayConnectorHandler {

	private static final Random RANDOM = new Random();

	public static void tick() {
		Player player = Minecraft.getInstance().player;
		Level level = Minecraft.getInstance().level;

		if (player == null || level == null)
			return;
		if (Minecraft.getInstance().screen != null)
			return;

		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack heldItem = player.getItemInHand(hand);

			if (!(heldItem.getItem() instanceof WalkwayConnectorItem walkwayItem) || !heldItem.hasTag())
				continue;
			CompoundTag tag = heldItem.getTag();
			if (!tag.contains("FirstTerminal"))
				continue;

			BlockPos first = NbtUtils.readBlockPos(tag.getCompound("FirstTerminal"));
			BlockState firstState = level.getBlockState(first);

			Direction.Axis axis = Direction.Axis.Y;
			if (ShaftBlock.isShaft(firstState)) {
				axis = firstState.getValue(BlockStateProperties.AXIS);
			} else if (firstState.getBlock() instanceof KineticBlock kinetic && kinetic instanceof WalkwayBlock) {
				axis = kinetic.getRotationAxis(firstState);
			}
			if (axis == Axis.Y)
				return;

			HitResult rayTrace = Minecraft.getInstance().hitResult;
			if (rayTrace == null || !(rayTrace instanceof BlockHitResult)) {
				if (RANDOM.nextInt(50) == 0) {
					level.addParticle(new DustParticleOptions(new Vector3f(.3f, .9f, .5f), 1),
						first.getX() + .5f + randomOffset(.25f), first.getY() + .5f + randomOffset(.25f),
						first.getZ() + .5f + randomOffset(.25f), 0, 0, 0);
				}
				return;
			}

			BlockPos selected = ((BlockHitResult) rayTrace).getBlockPos();
			BlockState secondState = level.getBlockState(selected);

			if (secondState.canBeReplaced())
				return;
			if (!ShaftBlock.isShaft(secondState) && !(secondState.getBlock() instanceof WalkwayBlock))
				selected = selected.relative(((BlockHitResult) rayTrace).getDirection());
			if (!selected.closerThan(first, walkwayItem.maxWalkwayLength()))
				return;

			boolean canConnect = WalkwayConnectorItem.validateAxis(level, selected) && walkwayItem.canConnect(level, first, selected);
			BlockPos diffPos = selected.subtract(first);
			boolean extendingWalkway = Math.abs(axis.choose(diffPos.getX(), diffPos.getY(), diffPos.getZ())) == 1;

			Vec3 start = Vec3.atLowerCornerOf(first);
			Vec3 end = Vec3.atLowerCornerOf(selected);
			Vec3 diff = end.subtract(start);
			if (extendingWalkway) {
				List<BlockPos> list = new ArrayList<>();
				Vec3 extensionOffset = new Vec3(axis.choose(diffPos.getX(), 0, 0), 0, axis.choose(0, 0, diffPos.getZ()));
				if (level.getBlockEntity(first) instanceof WalkwayBlockEntity walkwayBE) {
					list = walkwayBE.getAllBlocks();
				} else if (level.getBlockEntity(selected) instanceof WalkwayBlockEntity walkwayBE) {
					list = walkwayBE.getAllBlocks();
					extensionOffset = extensionOffset.reverse();
				}
				if (list.isEmpty())
					return;

				start = Vec3.atLowerCornerOf(list.get(0));
				end = Vec3.atLowerCornerOf(list.get(list.size() - 1));
				diff = end.subtract(start);
				start = start.add(extensionOffset);
			}

			double x = Math.abs(diff.x);
			double y = Math.abs(diff.y);
			double z = Math.abs(diff.z);
			float length = (float) diff.length();
			Vec3 step = diff.normalize();

			// Crude simplification compared to BeltConnectorHandler
			for (float f = 0; f < length; f += .0625f) {
				Vec3 position = start.add(step.scale(f));
				if (RANDOM.nextInt(10) == 0) {
					level.addParticle(new DustParticleOptions(new Vector3f(canConnect ? .3f : .9f, canConnect ? .9f : .3f, .5f), 1),
							position.x + .5f, position.y + .5f, position.z + .5f, 0, 0, 0);
				}
			}
			return;
		}
	}

	private static float randomOffset(float range) {
		return (RANDOM.nextFloat() - .5f) * 2 * range;
	}

}
