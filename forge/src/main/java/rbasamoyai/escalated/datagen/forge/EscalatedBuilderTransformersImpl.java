package rbasamoyai.escalated.datagen.forge;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.walkways.WalkwayBlock;
import rbasamoyai.escalated.walkways.WalkwayCaps;

public class EscalatedBuilderTransformersImpl {

    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> walkwayTerminal(String material) {
        ResourceLocation noneLoc = CreateEscalated.resource("walkway_terminal");
        ResourceLocation leftLoc = CreateEscalated.resource("walkway_terminal_left_cap");
        ResourceLocation rightLoc = CreateEscalated.resource("walkway_terminal_right_cap");
        ResourceLocation bothLoc = CreateEscalated.resource("walkway_terminal_both_cap");
        return b -> b.blockstate((c, p) -> p.horizontalBlock(c.get(), state -> {
            WalkwayCaps cap = state.getValue(WalkwayBlock.CAPS_SHAFT);
            ResourceLocation loc = switch (cap) {
                case NONE -> noneLoc;
                case LEFT -> leftLoc;
                case RIGHT -> rightLoc;
                case BOTH, NO_SHAFT -> bothLoc;
            };
            return p.models().withExistingParent(c.getName() + "_" + cap.getSerializedName(), loc)
                    .texture("top", "block/" + material + "_walkway_terminal_top")
                    .texture("bottom", "block/" + material + "_walkway_terminal_bottom")
                    .texture("side", "block/" + material + "_walkway_side")
                    .texture("hole", "block/" + material + "_walkway_hole");
        }));
    }

    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> narrowWalkway(String material, NonNullSupplier<ItemLike> drop) {
        ResourceLocation noneLoc = CreateEscalated.resource(material + "_narrow_walkway");
        ResourceLocation leftLoc = CreateEscalated.resource(material + "_narrow_walkway_left_cap");
        ResourceLocation rightLoc = CreateEscalated.resource(material + "_narrow_walkway_right_cap");
        ResourceLocation bothLoc = CreateEscalated.resource(material + "_narrow_walkway_both_cap");
        return b -> b.blockstate((c, p) -> p.horizontalBlock(c.get(), state -> {
            WalkwayCaps cap = state.getValue(WalkwayBlock.CAPS);
            ResourceLocation loc = switch (cap) {
                case NONE -> noneLoc;
                case LEFT -> leftLoc;
                case RIGHT -> rightLoc;
                case BOTH, NO_SHAFT -> bothLoc;
            };
            return p.models().withExistingParent(c.getName() + "_" + cap.getSerializedName(), loc)
                    .texture("top", "block/" + material + "_walkway_top")
                    .texture("bottom", "block/" + material + "_walkway_bottom")
                    .texture("side", "block/" + material + "_walkway_side")
                    .texture("hole", "block/" + material + "_walkway_hole");
        })).loot((p, c) -> p.dropOther(c, drop.get()));
    }

    public static <T extends Item, P> NonNullUnaryOperator<ItemBuilder<T, P>> existingItemModel() {
        return b -> b.model((c, p) -> {});
    }

}
