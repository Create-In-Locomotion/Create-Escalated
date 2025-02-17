package rbasamoyai.escalated.index;

import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import rbasamoyai.escalated.ModGroup;
import rbasamoyai.escalated.datagen.EscalatedBuilderTransformers;
import rbasamoyai.escalated.walkways.NarrowWalkwayBlock;
import rbasamoyai.escalated.walkways.WalkwayTerminalBlock;

import static rbasamoyai.escalated.CreateEscalated.REGISTRATE;

public class EscalatedBlocks {

	static { ModGroup.useModTab(ModGroup.MAIN_TAB_KEY); }

	public static final BlockEntry<WalkwayTerminalBlock> METAL_WALKWAY_TERMINAL = REGISTRATE
			.block("metal_walkway_terminal", WalkwayTerminalBlock::new)
			.addLayer(() -> RenderType::cutoutMipped)
			.initialProperties(SharedProperties::netheriteMetal)
			.properties(p -> p.noOcclusion()
					.strength(3.0f, 6.0f)
					.mapColor(MapColor.METAL)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.walkwayTerminal("metal"))
			.transform(TagGen.pickaxeOnly())
			.register();

	public static final BlockEntry<NarrowWalkwayBlock> METAL_NARROW_WALKWAY = REGISTRATE
			.block("metal_narrow_walkway", NarrowWalkwayBlock::new)
			.lang("Metal Walkway")
			.addLayer(() -> RenderType::cutoutMipped)
			.initialProperties(SharedProperties::netheriteMetal)
			.properties(p -> p.noOcclusion()
					.strength(3.0f, 6.0f)
					.mapColor(MapColor.METAL)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.narrowWalkway("metal", EscalatedItems.METAL_WALKWAY_STEPS::asItem))
			.transform(TagGen.pickaxeOnly())
			.register();

	// TODO wide walkway blocks, side and center

	// TODO narrow escalator block

	// TODO wide escalator block, side and center

	public static void register() {}

	private static boolean neverConducts(BlockState blockState, BlockGetter level, BlockPos pos) { return false; }

}
