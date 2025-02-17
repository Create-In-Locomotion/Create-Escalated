package rbasamoyai.escalated.walkways.fabric;

import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import io.github.fabricators_of_create.porting_lib.tags.Tags;
import io.github.fabricators_of_create.porting_lib.util.TagUtil;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;

public class WalkwayHelperImpl {

    public static boolean isDye(ItemStack itemStack) { return itemStack.is(Tags.Items.DYES); }

    public static boolean hasWater(Level level, ItemStack itemStack) {
        return GenericItemEmptying.emptyItem(level, itemStack, true).getFirst().getFluid().isSame(Fluids.WATER);
    }

    public static DyeColor getDyeColorFromItem(ItemStack itemStack) { return TagUtil.getColorFromStack(itemStack); }

}
