package rbasamoyai.escalated.index;

import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import rbasamoyai.escalated.ModGroup;
import rbasamoyai.escalated.datagen.EscalatedBuilderTransformers;
import rbasamoyai.escalated.walkways.WalkwayTerminalBlock;

import static rbasamoyai.escalated.CreateEscalated.REGISTRATE;

public class EscalatedBlocks {

	static { ModGroup.useModTab(ModGroup.MAIN_TAB_KEY); }

	public static final BlockEntry<WalkwayTerminalBlock> WALKWAY_TERMINAL = REGISTRATE
			.block("walkway_terminal", WalkwayTerminalBlock::new)
			.addLayer(() -> RenderType::cutoutMipped)
			.initialProperties(SharedProperties::netheriteMetal)
			.properties(p -> p.noOcclusion()
					.strength(3.0f, 6.0f)
					.mapColor(MapColor.METAL)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.walkwayTerminal())
			.register();

	public static void register() {}

	private static boolean neverConducts(BlockState blockState, BlockGetter level, BlockPos pos) { return false; }

}
