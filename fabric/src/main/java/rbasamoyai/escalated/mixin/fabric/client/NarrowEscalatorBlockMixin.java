package rbasamoyai.escalated.mixin.fabric.client;

import com.simibubi.create.foundation.block.render.MultiPosDestructionHandler;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import rbasamoyai.escalated.walkways.*;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

@Mixin(NarrowEscalatorBlock.class)
public abstract class NarrowEscalatorBlockMixin extends AbstractWalkwayBlock implements MultiPosDestructionHandler {

    NarrowEscalatorBlockMixin(Properties properties, NonNullSupplier<WalkwaySet> walkwaySetSupplier) {
        super(properties, walkwaySetSupplier);
    }

    @Override
    @Nullable
    public Set<BlockPos> getExtraPositions(ClientLevel level, BlockPos pos, BlockState blockState, int progress) {
        return level.getBlockEntity(pos) instanceof WalkwayBlockEntity be ?
                new HashSet<>(WalkwayBlock.getWalkwayChain(level, be.getController())) : null;
    }

}
