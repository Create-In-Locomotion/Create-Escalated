package rbasamoyai.escalated.walkways;

import com.simibubi.create.AllItems;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

/**
 * Adapted from {@link com.simibubi.create.content.kinetics.belt.BeltHelper}
 */
public class WalkwayHelper {

    public static WalkwayBlockEntity getSegmentBE(LevelAccessor level, BlockPos pos) {
        if (level instanceof Level l && !l.isLoaded(pos))
            return null;
        return level.getBlockEntity(pos) instanceof WalkwayBlockEntity walkway ? walkway : null;
    }

    public static WalkwayBlockEntity getControllerBE(LevelAccessor level, BlockPos pos) {
        WalkwayBlockEntity segment = getSegmentBE(level, pos);
        if (segment == null)
            return null;
        BlockPos controllerPos = segment.controller;
        return controllerPos == null ? null : getSegmentBE(level, controllerPos);
    }

    @ExpectPlatform public static boolean isDye(ItemStack itemStack) { throw new AssertionError(); }
    @ExpectPlatform public static boolean hasWater(Level level, ItemStack itemStack) { throw new AssertionError(); }
    @ExpectPlatform public static DyeColor getDyeColorFromItem(ItemStack itemStack) { throw new AssertionError(); }

    public static boolean isHandrail(ItemStack itemStack) {
        return AllItems.BELT_CONNECTOR.isIn(itemStack);
    }

    private WalkwayHelper() {}

}
