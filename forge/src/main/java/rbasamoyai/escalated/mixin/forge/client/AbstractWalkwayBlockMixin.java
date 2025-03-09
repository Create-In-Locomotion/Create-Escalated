package rbasamoyai.escalated.mixin.forge.client;

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.render.MultiPosDestructionHandler;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import org.spongepowered.asm.mixin.Mixin;
import rbasamoyai.escalated.walkways.AbstractWalkwayBlock;
import rbasamoyai.escalated.walkways.forge.WalkwayRenderProperties;

import java.util.function.Consumer;

@Mixin(AbstractWalkwayBlock.class)
public abstract class AbstractWalkwayBlockMixin extends HorizontalKineticBlock implements MultiPosDestructionHandler {

    AbstractWalkwayBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
        consumer.accept(new WalkwayRenderProperties());
    }

}
