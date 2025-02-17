package rbasamoyai.escalated.index;

import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;

import static net.minecraft.world.level.block.Block.box;

public class EscalatedShapes {

    public static final VoxelShaper NARROW_WALKWAY = VoxelShaper.forHorizontal(Shapes.or(box(0, 0, 0, 16, 15.5, 16),
            box(0, 15.5, 0, 2, 16, 16), box(14, 15.5, 0, 16, 16, 16)), Direction.NORTH);

    private EscalatedShapes() {}

}
