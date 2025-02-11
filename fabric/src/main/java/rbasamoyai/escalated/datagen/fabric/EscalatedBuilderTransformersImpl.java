package rbasamoyai.escalated.datagen.fabric;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.walkways.WalkwayTerminalBlock;

public class EscalatedBuilderTransformersImpl {

    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> walkwayTerminal() {
        ResourceLocation noneLoc = CreateEscalated.resource("walkway_terminal");
        ResourceLocation leftLoc = CreateEscalated.resource("walkway_terminal_left_cap");
        ResourceLocation rightLoc = CreateEscalated.resource("walkway_terminal_right_cap");
        ResourceLocation bothLoc = CreateEscalated.resource("walkway_terminal_both_cap");
        return b -> b.blockstate((c, p) -> p.horizontalBlock(c.get(), state -> {
            ResourceLocation loc = switch (state.getValue(WalkwayTerminalBlock.CAPS)) {
                case NONE -> noneLoc;
                case LEFT -> leftLoc;
                case RIGHT -> rightLoc;
                case BOTH -> bothLoc;
            };
            return p.models().getExistingFile(loc);
        }));
    }

    public static <T extends Item, P> NonNullUnaryOperator<ItemBuilder<T, P>> metalWalkwaySteps() {
        return b -> b.model((c, p) -> {});
    }

}
