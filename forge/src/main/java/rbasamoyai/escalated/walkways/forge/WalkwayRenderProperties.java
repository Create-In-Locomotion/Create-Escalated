package rbasamoyai.escalated.walkways.forge;

import com.simibubi.create.foundation.block.render.MultiPosDestructionHandler;
import com.simibubi.create.foundation.block.render.ReducedDestroyEffects;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import rbasamoyai.escalated.walkways.WalkwayBlock;
import rbasamoyai.escalated.walkways.WalkwayBlockEntity;

import java.util.HashSet;
import java.util.Set;

public class WalkwayRenderProperties extends ReducedDestroyEffects implements MultiPosDestructionHandler {

    @Override
    @Nullable
    public Set<BlockPos> getExtraPositions(ClientLevel level, BlockPos pos, BlockState blockState, int progress) {
        return level.getBlockEntity(pos) instanceof WalkwayBlockEntity be ?
                new HashSet<>(WalkwayBlock.getWalkwayChain(level, be.getController())) : null;
    }

}