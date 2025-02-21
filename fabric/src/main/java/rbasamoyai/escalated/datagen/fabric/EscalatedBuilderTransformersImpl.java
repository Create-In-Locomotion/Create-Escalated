package rbasamoyai.escalated.datagen.fabric;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.walkways.WalkwayBlock;
import rbasamoyai.escalated.walkways.WalkwayCaps;
import rbasamoyai.escalated.walkways.WideWalkwaySideBlock;

public class EscalatedBuilderTransformersImpl {

    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> walkwayTerminal(String material) {
        ResourceLocation noneLoc = CreateEscalated.resource("walkway_terminal");
        ResourceLocation leftLoc = CreateEscalated.resource("walkway_terminal_left");
        ResourceLocation rightLoc = CreateEscalated.resource("walkway_terminal_right");
        ResourceLocation bothLoc = CreateEscalated.resource("walkway_terminal_both");
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

    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> narrowWalkway(String material) {
        ResourceLocation noneLoc = CreateEscalated.resource("narrow_walkway");
        ResourceLocation leftLoc = CreateEscalated.resource("narrow_walkway_left");
        ResourceLocation rightLoc = CreateEscalated.resource("narrow_walkway_right");
        ResourceLocation bothLoc = CreateEscalated.resource("narrow_walkway_both");
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
        }));
    }

    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> wideWalkwaySide(String material) {
        ResourceLocation leftLoc = CreateEscalated.resource("wide_walkway_side_left");
        ResourceLocation leftCapLoc = CreateEscalated.resource("wide_walkway_side_left_cap");
        ResourceLocation rightLoc = CreateEscalated.resource("wide_walkway_side_right");
        ResourceLocation rightCapLoc = CreateEscalated.resource("wide_walkway_side_right_cap");
        return b -> b.blockstate((c, p) -> p.horizontalBlock(c.get(), state -> {
            WalkwayCaps cap = state.getValue(WideWalkwaySideBlock.CAPS_SIDED);
            boolean left = state.getValue(WideWalkwaySideBlock.LEFT);
            ResourceLocation loc = switch (cap) {
                case NONE -> left ? leftLoc : rightLoc;
                case BOTH, NO_SHAFT -> left ? leftCapLoc : rightCapLoc;
                case LEFT, RIGHT -> null; // Ignore
            };
            String suffix = "_" + (left ? "left" : "right") + "_" + cap.getSerializedName();
            return p.models().withExistingParent(c.getName() + suffix, loc)
                    .texture("top", "block/" + material + "_walkway_top")
                    .texture("bottom", "block/" + material + "_walkway_bottom")
                    .texture("side", "block/" + material + "_walkway_side")
                    .texture("hole", "block/" + material + "_walkway_hole");
        }));
    }

    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> wideWalkwayCenter(String material) {
        ResourceLocation centerLoc = CreateEscalated.resource("wide_walkway_center");
        return b -> b.blockstate((c, p) -> p.horizontalBlock(c.get(), p.models().withExistingParent(c.getName(), centerLoc)
                .texture("bottom", "block/" + material + "_walkway_bottom")
                .texture("side", "block/" + material + "_walkway_side")));
    }

    public static <T extends Item, P> NonNullUnaryOperator<ItemBuilder<T, P>> existingItemModel() {
        return b -> b.model((c, p) -> {});
    }

}
