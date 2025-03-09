package rbasamoyai.escalated.mixin.fabric.client;

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.render.MultiPosDestructionHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import rbasamoyai.escalated.walkways.AbstractWalkwayBlock;
import rbasamoyai.escalated.walkways.WalkwayBlock;
import rbasamoyai.escalated.walkways.WalkwayBlockEntity;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

@Mixin(AbstractWalkwayBlock.class)
public abstract class AbstractWalkwayBlockMixin extends HorizontalKineticBlock implements MultiPosDestructionHandler {

    AbstractWalkwayBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public Set<BlockPos> getExtraPositions(ClientLevel level, BlockPos pos, BlockState blockState, int progress) {
        return level.getBlockEntity(pos) instanceof WalkwayBlockEntity be ?
                new HashSet<>(WalkwayBlock.getWalkwayChain(level, be.getController())) : null;
    }

}
