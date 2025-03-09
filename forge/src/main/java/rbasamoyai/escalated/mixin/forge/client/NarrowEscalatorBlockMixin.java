package rbasamoyai.escalated.mixin.forge.client;

import com.simibubi.create.foundation.block.render.MultiPosDestructionHandler;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import org.spongepowered.asm.mixin.Mixin;
import rbasamoyai.escalated.walkways.AbstractWalkwayBlock;
import rbasamoyai.escalated.walkways.NarrowEscalatorBlock;
import rbasamoyai.escalated.walkways.WalkwaySet;
import rbasamoyai.escalated.walkways.forge.WalkwayRenderProperties;

import java.util.function.Consumer;

@Mixin(NarrowEscalatorBlock.class)
public abstract class NarrowEscalatorBlockMixin extends AbstractWalkwayBlock implements MultiPosDestructionHandler {

    NarrowEscalatorBlockMixin(Properties properties, NonNullSupplier<WalkwaySet> walkwaySetSupplier) {
        super(properties, walkwaySetSupplier);
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
        consumer.accept(new WalkwayRenderProperties());
    }

}
