package rbasamoyai.escalated.walkways.forge;

import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;

public class WalkwayHelperImpl {

    public static boolean isDye(ItemStack itemStack) { return itemStack.is(Tags.Items.DYES); }

    public static boolean hasWater(Level level, ItemStack itemStack) {
        return GenericItemEmptying.emptyItem(level, itemStack, true).getFirst().getFluid().isSame(Fluids.WATER);
    }

    public static DyeColor getDyeColorFromItem(ItemStack itemStack) { return DyeColor.getColor(itemStack); }

}
