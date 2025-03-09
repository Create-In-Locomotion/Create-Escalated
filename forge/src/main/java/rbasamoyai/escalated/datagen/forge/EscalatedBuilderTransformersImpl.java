package rbasamoyai.escalated.datagen.forge;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.walkways.*;

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

    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> narrowWalkway(String material) {
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
        ResourceLocation centerLoc = CreateEscalated.resource("wide_walkway_side_center");
        return b -> b.blockstate((c, p) -> p.horizontalBlock(c.get(), p.models().withExistingParent(c.getName(), centerLoc)
                .texture("bottom", "block/" + material + "_walkway_bottom")
                .texture("side", "block/" + material + "_walkway_side")));
    }

    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> narrowEscalator(String material) {
        ResourceLocation horizontalLoc = CreateEscalated.resource("narrow_escalator_horizontal");
        ResourceLocation bottomLoc = CreateEscalated.resource("narrow_escalator_bottom");
        ResourceLocation middleLoc = CreateEscalated.resource("narrow_escalator_middle");
        ResourceLocation topLoc = CreateEscalated.resource("narrow_escalator_top");
        return b -> b.blockstate((c, p) -> p.horizontalBlock(c.get(), state -> {
            WalkwaySlope slope = state.getValue(WalkwayBlock.SLOPE);
            ResourceLocation loc = switch (slope) {
                case HORIZONTAL -> horizontalLoc;
                case BOTTOM -> bottomLoc;
                case MIDDLE -> middleLoc;
                case TOP -> topLoc;
                case TERMINAL -> null; // Ignore
            };
            return p.models().withExistingParent(c.getName() + "_" + slope.getSerializedName(), loc)
                    .texture("top", "block/" + material + "_walkway_top")
                    .texture("bottom", "block/" + material + "_walkway_bottom")
                    .texture("walkway_side", "block/" + material + "_walkway_side")
                    .texture("escalator_side", "block/" + material + "_escalator_side")
                    .texture("escalator_bottom_side", "block/" + material + "_escalator_bottom_side")
                    .texture("escalator_top_side", "block/" + material + "_escalator_top_side")
                    .texture("escalator_top_side_1", "block/" + material + "_escalator_top_side_1");
        }, 0));
    }

    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> wideEscalatorSide(String material) {
        return b -> b.blockstate((c, p) -> p.horizontalBlock(c.get(), state -> {
            WalkwaySlope slope = state.getValue(WideEscalatorSideBlock.SLOPE);
            boolean left = state.getValue(WideEscalatorSideBlock.LEFT);
            String suffix = "_" + slope.getSerializedName() + "_" + (left ? "left" : "right");
            ResourceLocation modelLoc = CreateEscalated.resource("wide_escalator_side" + suffix);
            return p.models().withExistingParent(c.getName() + suffix, modelLoc)
                    .texture("top", "block/" + material + "_walkway_top")
                    .texture("bottom", "block/" + material + "_walkway_bottom")
                    .texture("walkway_side", "block/" + material + "_walkway_side")
                    .texture("escalator_side", "block/" + material + "_escalator_side")
                    .texture("escalator_bottom_side", "block/" + material + "_escalator_bottom_side")
                    .texture("escalator_top_side", "block/" + material + "_escalator_top_side")
                    .texture("escalator_top_side_1", "block/" + material + "_escalator_top_side_1");
        }, 0));
    }

    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> wideEscalatorCenter(String material) {
        ResourceLocation horizontalLoc = CreateEscalated.resource("wide_escalator_center_horizontal");
        ResourceLocation bottomLoc = CreateEscalated.resource("wide_escalator_center_bottom");
        ResourceLocation middleLoc = CreateEscalated.resource("wide_escalator_center_middle");
        ResourceLocation topLoc = CreateEscalated.resource("wide_escalator_center_top");
        return b -> b.blockstate((c, p) -> p.horizontalBlock(c.get(), state -> {
            WalkwaySlope slope = state.getValue(WalkwayBlock.SLOPE);
            ResourceLocation loc = switch (slope) {
                case HORIZONTAL -> horizontalLoc;
                case BOTTOM -> bottomLoc;
                case MIDDLE -> middleLoc;
                case TOP -> topLoc;
                case TERMINAL -> null; // Ignore
            };
            return p.models().withExistingParent(c.getName() + "_" + slope.getSerializedName(), loc)
                    .texture("bottom", "block/" + material + "_walkway_bottom");
        }, 0));
    }

    public static <T extends Item, P> NonNullUnaryOperator<ItemBuilder<T, P>> existingItemModel() {
        return b -> b.model((c, p) -> {});
    }

}
