package rbasamoyai.escalated.datagen;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class EscalatedBuilderTransformers {

    @ExpectPlatform public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> walkwayTerminal(String material) { throw new AssertionError(); }
    @ExpectPlatform public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> narrowWalkway(String material, NonNullSupplier<ItemLike> drop) { throw new AssertionError(); }
    @ExpectPlatform public static <T extends Item, P> NonNullUnaryOperator<ItemBuilder<T, P>> existingItemModel() { throw new AssertionError(); }

}
