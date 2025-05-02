package rbasamoyai.escalated.index;

import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import rbasamoyai.escalated.ModGroup;
import rbasamoyai.escalated.datagen.EscalatedBuilderTransformers;
import rbasamoyai.escalated.handrails.EscalatorHandrailBlock;
import rbasamoyai.escalated.handrails.WalkwayHandrailBlock;
import rbasamoyai.escalated.walkways.*;

import static rbasamoyai.escalated.CreateEscalated.REGISTRATE;

public class EscalatedBlocks {

	static { ModGroup.useModTab(ModGroup.MAIN_TAB_KEY); }

	//////// Metal walkway blocks ////////
	public static final BlockEntry<WalkwayTerminalBlock> METAL_WALKWAY_TERMINAL = REGISTRATE
			.block("metal_walkway_terminal", p -> new WalkwayTerminalBlock(p, EscalatedWalkwaySets::metalWalkwaySet))
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
			.block("metal_narrow_walkway", p -> new NarrowWalkwayBlock(p, EscalatedWalkwaySets::metalWalkwaySet))
			.lang("Metal Walkway")
			.addLayer(() -> RenderType::cutoutMipped)
			.initialProperties(SharedProperties::netheriteMetal)
			.properties(p -> p.noOcclusion()
					.strength(3.0f, 6.0f)
					.mapColor(MapColor.METAL)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.narrowWalkway("metal"))
			.transform(TagGen.pickaxeOnly())
			.register();

	public static final BlockEntry<WideWalkwaySideBlock> METAL_WIDE_WALKWAY_SIDE = REGISTRATE
			.block("metal_wide_walkway_side", p -> new WideWalkwaySideBlock(p, EscalatedWalkwaySets::metalWalkwaySet))
			.lang("Metal Walkway")
			.addLayer(() -> RenderType::cutoutMipped)
			.initialProperties(SharedProperties::netheriteMetal)
			.properties(p -> p.noOcclusion()
					.strength(3.0f, 6.0f)
					.mapColor(MapColor.METAL)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.wideWalkwaySide("metal"))
			.transform(TagGen.pickaxeOnly())
			.register();

	public static final BlockEntry<WideWalkwayCenterBlock> METAL_WIDE_WALKWAY_CENTER = REGISTRATE
			.block("metal_wide_walkway_center", p -> new WideWalkwayCenterBlock(p, EscalatedWalkwaySets::metalWalkwaySet))
			.lang("Metal Walkway")
			.addLayer(() -> RenderType::cutoutMipped)
			.initialProperties(SharedProperties::netheriteMetal)
			.properties(p -> p.noOcclusion()
					.strength(3.0f, 6.0f)
					.mapColor(MapColor.METAL)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.wideWalkwayCenter("metal"))
			.transform(TagGen.pickaxeOnly())
			.register();

	//////// Metal escalator blocks ////////
	public static final BlockEntry<NarrowEscalatorBlock> METAL_NARROW_ESCALATOR = REGISTRATE
			.block("metal_narrow_escalator", p -> new NarrowEscalatorBlock(p, EscalatedWalkwaySets::metalEscalatorSet))
			.lang("Metal Escalator")
			.addLayer(() -> RenderType::cutoutMipped)
			.initialProperties(SharedProperties::netheriteMetal)
			.properties(p -> p.noOcclusion()
					.strength(3.0f, 6.0f)
					.mapColor(MapColor.METAL)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.narrowEscalator("metal"))
			.transform(TagGen.pickaxeOnly())
			.register();

	public static final BlockEntry<WideEscalatorSideBlock> METAL_WIDE_ESCALATOR_SIDE = REGISTRATE
			.block("metal_wide_escalator_side", p -> new WideEscalatorSideBlock(p, EscalatedWalkwaySets::metalEscalatorSet))
			.lang("Metal Escalator")
			.addLayer(() -> RenderType::cutoutMipped)
			.initialProperties(SharedProperties::netheriteMetal)
			.properties(p -> p.noOcclusion()
					.strength(3.0f, 6.0f)
					.mapColor(MapColor.METAL)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.wideEscalatorSide("metal"))
			.transform(TagGen.pickaxeOnly())
			.register();

	public static final BlockEntry<WideEscalatorCenterBlock> METAL_WIDE_ESCALATOR_CENTER = REGISTRATE
			.block("metal_wide_escalator_center", p -> new WideEscalatorCenterBlock(p, EscalatedWalkwaySets::metalEscalatorSet))
			.lang("Metal Escalator")
			.addLayer(() -> RenderType::cutoutMipped)
			.initialProperties(SharedProperties::netheriteMetal)
			.properties(p -> p.noOcclusion()
					.strength(3.0f, 6.0f)
					.mapColor(MapColor.METAL)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.wideEscalatorCenter("metal"))
			.transform(TagGen.pickaxeOnly())
			.register();

	//////// Wooden walkway blocks ////////
	public static final BlockEntry<WalkwayTerminalBlock> WOODEN_WALKWAY_TERMINAL = REGISTRATE
			.block("wooden_walkway_terminal", p -> new WalkwayTerminalBlock(p, EscalatedWalkwaySets::woodenWalkwaySet))
			.addLayer(() -> RenderType::cutoutMipped)
			.properties(p -> p.noOcclusion()
					.strength(2.0f)
					.mapColor(MapColor.PODZOL)
					.sound(SoundType.WOOD)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.walkwayTerminal("wooden"))
			.transform(TagGen.axeOrPickaxe())
			.register();

	public static final BlockEntry<NarrowWalkwayBlock> WOODEN_NARROW_WALKWAY = REGISTRATE
			.block("wooden_narrow_walkway", p -> new NarrowWalkwayBlock(p, EscalatedWalkwaySets::woodenWalkwaySet))
			.lang("Wooden Walkway")
			.addLayer(() -> RenderType::cutoutMipped)
			.properties(p -> p.noOcclusion()
					.strength(2.0f)
					.mapColor(MapColor.PODZOL)
					.sound(SoundType.WOOD)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.narrowWalkway("wooden"))
			.transform(TagGen.axeOrPickaxe())
			.register();

	public static final BlockEntry<WideWalkwaySideBlock> WOODEN_WIDE_WALKWAY_SIDE = REGISTRATE
			.block("wooden_wide_walkway_side", p -> new WideWalkwaySideBlock(p, EscalatedWalkwaySets::woodenWalkwaySet))
			.lang("Wooden Walkway")
			.addLayer(() -> RenderType::cutoutMipped)
			.properties(p -> p.noOcclusion()
					.strength(2.0f)
					.mapColor(MapColor.PODZOL)
					.sound(SoundType.WOOD)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.wideWalkwaySide("wooden"))
			.transform(TagGen.axeOrPickaxe())
			.register();

	public static final BlockEntry<WideWalkwayCenterBlock> WOODEN_WIDE_WALKWAY_CENTER = REGISTRATE
			.block("wooden_wide_walkway_center", p -> new WideWalkwayCenterBlock(p, EscalatedWalkwaySets::woodenWalkwaySet))
			.lang("Wooden Walkway")
			.addLayer(() -> RenderType::cutoutMipped)
			.properties(p -> p.noOcclusion()
					.strength(2.0f)
					.mapColor(MapColor.PODZOL)
					.sound(SoundType.WOOD)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.wideWalkwayCenter("wooden"))
			.transform(TagGen.axeOrPickaxe())
			.register();

	//////// Wooden escalator blocks ////////
	public static final BlockEntry<NarrowEscalatorBlock> WOODEN_NARROW_ESCALATOR = REGISTRATE
			.block("wooden_narrow_escalator", p -> new NarrowEscalatorBlock(p, EscalatedWalkwaySets::woodenEscalatorSet))
			.lang("Wooden Escalator")
			.addLayer(() -> RenderType::cutoutMipped)
			.properties(p -> p.noOcclusion()
					.strength(2.0f)
					.mapColor(MapColor.PODZOL)
					.sound(SoundType.WOOD)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.narrowEscalator("wooden"))
			.transform(TagGen.axeOrPickaxe())
			.register();

	public static final BlockEntry<WideEscalatorSideBlock> WOODEN_WIDE_ESCALATOR_SIDE = REGISTRATE
			.block("wooden_wide_escalator_side", p -> new WideEscalatorSideBlock(p, EscalatedWalkwaySets::woodenEscalatorSet))
			.lang("Wooden Escalator")
			.addLayer(() -> RenderType::cutoutMipped)
			.properties(p -> p.noOcclusion()
					.strength(2.0f)
					.mapColor(MapColor.PODZOL)
					.sound(SoundType.WOOD)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.wideEscalatorSide("wooden"))
			.transform(TagGen.axeOrPickaxe())
			.register();

	public static final BlockEntry<WideEscalatorCenterBlock> WOODEN_WIDE_ESCALATOR_CENTER = REGISTRATE
			.block("wooden_wide_escalator_center", p -> new WideEscalatorCenterBlock(p, EscalatedWalkwaySets::woodenEscalatorSet))
			.lang("Wooden Escalator")
			.addLayer(() -> RenderType::cutoutMipped)
			.properties(p -> p.noOcclusion()
					.strength(2.0f)
					.mapColor(MapColor.PODZOL)
					.sound(SoundType.WOOD)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.wideEscalatorCenter("wooden"))
			.transform(TagGen.axeOrPickaxe())
			.register();

	//////// Walkway/escalator handrail blocks ////////
	public static final BlockEntry<WalkwayHandrailBlock> METAL_WALKWAY_HANDRAIL = REGISTRATE
			.block("metal_walkway_handrail", WalkwayHandrailBlock::new)
			.lang("Metal Handrail")
			.addLayer(() -> RenderType::cutoutMipped)
			.initialProperties(SharedProperties::netheriteMetal)
			.properties(p -> p.noOcclusion()
					.strength(3.0f, 6.0f)
					.mapColor(MapColor.NONE)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.walkwayHandrail("metal"))
			.transform(TagGen.pickaxeOnly())
			.register();

	public static final BlockEntry<EscalatorHandrailBlock> METAL_ESCALATOR_HANDRAIL = REGISTRATE
			.block("metal_escalator_handrail", EscalatorHandrailBlock::new)
			.lang("Metal Handrail")
			.addLayer(() -> RenderType::cutoutMipped)
			.initialProperties(SharedProperties::netheriteMetal)
			.properties(p -> p.noOcclusion()
					.strength(3.0f, 6.0f)
					.mapColor(MapColor.NONE)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.escalatorHandrail("metal"))
			.transform(TagGen.pickaxeOnly())
			.register();

	public static final BlockEntry<WalkwayHandrailBlock> WOODEN_WALKWAY_HANDRAIL = REGISTRATE
			.block("wooden_walkway_handrail", WalkwayHandrailBlock::new)
			.lang("Wooden Handrail")
			.addLayer(() -> RenderType::cutoutMipped)
			.properties(p -> p.noOcclusion()
					.strength(2.0f)
					.mapColor(MapColor.NONE)
					.sound(SoundType.WOOD)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.walkwayHandrail("wooden"))
			.transform(TagGen.axeOrPickaxe())
			.register();

	public static final BlockEntry<EscalatorHandrailBlock> WOODEN_ESCALATOR_HANDRAIL = REGISTRATE
			.block("wooden_escalator_handrail", EscalatorHandrailBlock::new)
			.lang("Wooden Handrail")
			.addLayer(() -> RenderType::cutoutMipped)
			.properties(p -> p.noOcclusion()
					.strength(2.0f)
					.mapColor(MapColor.NONE)
					.sound(SoundType.WOOD)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.escalatorHandrail("wooden"))
			.transform(TagGen.axeOrPickaxe())
			.register();

	public static final BlockEntry<WalkwayHandrailBlock> GLASS_WALKWAY_HANDRAIL = REGISTRATE
			.block("glass_walkway_handrail", WalkwayHandrailBlock::new)
			.lang("Glass Handrail")
			.addLayer(() -> RenderType::cutoutMipped)
			.initialProperties(() -> Blocks.GLASS)
			.properties(p -> p.noOcclusion()
					.strength(0.3f)
					.mapColor(MapColor.NONE)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.walkwayHandrail("glass"))
			.register();

	public static final BlockEntry<EscalatorHandrailBlock> GLASS_ESCALATOR_HANDRAIL = REGISTRATE
			.block("glass_escalator_handrail", EscalatorHandrailBlock::new)
			.lang("Glass Handrail")
			.addLayer(() -> RenderType::cutoutMipped)
			.initialProperties(() -> Blocks.GLASS)
			.properties(p -> p.noOcclusion()
					.strength(0.3f)
					.mapColor(MapColor.NONE)
					.isRedstoneConductor(EscalatedBlocks::neverConducts))
			.transform(EscalatedBuilderTransformers.escalatorHandrail("glass"))
			.register();

	public static void register() {}

	private static boolean neverConducts(BlockState blockState, BlockGetter level, BlockPos pos) { return false; }

}
