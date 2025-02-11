package rbasamoyai.escalated.walkways;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class WalkwayTerminalBlockEntity extends KineticBlockEntity {

    public WalkwayTerminalBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        // TODO transport entities on walkway
        // TODO transport entities on escalator
    }

}
