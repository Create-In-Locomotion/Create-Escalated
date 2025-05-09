package rbasamoyai.escalated.ponder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import rbasamoyai.escalated.handrails.AbstractHandrailBlock;
import rbasamoyai.escalated.handrails.HandrailBlockEntity;
import rbasamoyai.escalated.handrails.WalkwayHandrailBlock;
import rbasamoyai.escalated.index.EscalatedBlocks;
import rbasamoyai.escalated.index.EscalatedItems;
import rbasamoyai.escalated.walkways.NarrowWalkwayBlock;
import rbasamoyai.escalated.walkways.WalkwayBlockEntity;
import rbasamoyai.escalated.walkways.WalkwayCaps;

public class WalkwayPonders {

    public static void creatingWalkways(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("walkways/creating_walkways", "Creating Walkways and Escalators");
        scene.configureBasePlate(0, 0, 9);
        scene.showBasePlate();

        scene.idle(30);
        ElementLink<WorldSectionElement> walkwayShafts = scene.world().showIndependentSection(util.select().fromTo(5, 1, 1, 5, 1, 7), Direction.DOWN);
        scene.world().moveSection(walkwayShafts, util.vector().of(1, 0, 0), 0);
        scene.idle(30);

        scene.overlay().showText(100)
                .independent(20)
                .text("Right-Click two shafts with a walkway steps item to create a walkway.");
        scene.idle(30);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(6, 1, 1), Direction.WEST), Pointing.LEFT, 30)
                .withItem(EscalatedItems.METAL_WALKWAY_STEPS.asStack()).rightClick();
        scene.idle(30);
        Object selectionSlot = new Object();
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(6, 1, 7), Direction.WEST), Pointing.LEFT, 30)
                .withItem(EscalatedItems.METAL_WALKWAY_STEPS.asStack()).rightClick();
        scene.idle(15);
        scene.overlay().showOutline(PonderPalette.GREEN, selectionSlot, util.select().fromTo(6, 1, 1, 6, 1, 7), 30);
        scene.idle(15);
        scene.world().hideIndependentSection(walkwayShafts, null);
        scene.world().showIndependentSectionImmediately(util.select().fromTo(6, 1, 1, 6, 1, 7));
        scene.idle(40);

        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("Accidental selections can be cancelled with Right-Click while Sneaking.")
                .colored(PonderPalette.BLUE);
        scene.idle(30);
        scene.overlay().showOutline(PonderPalette.RED, selectionSlot, util.select().fromTo(6, 1, 1, 6, 1, 7), 50);
        scene.overlay().showControls(util.vector().topOf(6, 1, 4), Pointing.DOWN, 50)
                .withItem(EscalatedItems.METAL_WALKWAY_STEPS.asStack()).rightClick().whileSneaking();
        scene.idle(70);

        scene.overlay().showText(120)
                .attachKeyFrame()
                .independent(160)
                .text("Creating an escalator works similarly.");
        scene.idle(30);
        ElementLink<WorldSectionElement> escalatorShafts = scene.world().showIndependentSection(util.select().fromTo(1, 1, 1, 1, 4, 7), Direction.DOWN);
        scene.world().moveSection(escalatorShafts, util.vector().of(1, 0, 0), 0);
        scene.idle(30);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 1, 1), Direction.EAST), Pointing.RIGHT, 30)
                .withItem(EscalatedItems.METAL_WALKWAY_STEPS.asStack()).rightClick();
        scene.idle(30);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 4, 7), Direction.EAST), Pointing.RIGHT, 30)
                .withItem(EscalatedItems.METAL_WALKWAY_STEPS.asStack()).rightClick();
        scene.idle(15);
        scene.overlay().showOutline(PonderPalette.GREEN, selectionSlot, util.select().fromTo(2, 1, 1, 2, 4, 7), 50);
        scene.idle(15);
        scene.world().hideIndependentSection(escalatorShafts, null);
        scene.world().showIndependentSectionImmediately(util.select().fromTo(2, 1, 1, 2, 4, 7));
        scene.idle(60);

        scene.overlay().showText(50)
                .attachKeyFrame()
                .independent(160)
                .text("Due to the escalator structure, the shafts are not aligned on a perfect diagonal.")
                .colored(PonderPalette.RED);
        scene.idle(70);
        scene.overlay().showText(80)
                .independent(160)
                .text("The horizontal distance between the shafts is 3 blocks greater than the vertical distance.")
                .colored(PonderPalette.BLUE);
        scene.idle(30);
        scene.overlay().showOutlineWithText(util.select().fromTo(2, 1, 1, 2, 1, 7), 80)
                .text("7 blocks long")
                .placeNearTarget();
        scene.idle(15);
        scene.overlay().showOutlineWithText(util.select().fromTo(2, 1, 7, 2, 4, 7), 65)
                .text("4 blocks tall")
                .placeNearTarget();

        scene.idle(80);
        scene.markAsFinished();
    }

    public static void addingShaftsToWalkways(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("walkways/adding_shafts_to_walkways", "Adding more Shafts to Walkways and Escalators");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        scene.world().showSection(util.select().fromTo(0, 1, 2, 4, 1, 2), Direction.UP);
        scene.idle(30);

        scene.overlay().showText(50)
                .text("Additional shafts can be added to walkways for more power supply.");
        scene.idle(65);
        scene.overlay().showText(80)
                .text("To add a shaft, Right-Click a middle walkway block with a Shaft item.");
        scene.idle(30);
        scene.overlay().showOutline(PonderPalette.GREEN, new Object(), util.select().position(2, 1, 2), 50);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 1, 2), Direction.NORTH), Pointing.RIGHT, 50)
                .withItem(AllBlocks.SHAFT.asStack()).rightClick();
        scene.idle(40);
        scene.world().modifyBlock(util.grid().at(2, 1, 2), s -> s.setValue(NarrowWalkwayBlock.CAPS, WalkwayCaps.NONE), false);
        scene.idle(40);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Once added, the walkway block side can be wrenched to block off rotational input from that side.");
        scene.idle(30);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 1, 2), Direction.NORTH), Pointing.RIGHT, 50)
                .rightClick().withItem(AllItems.WRENCH.asStack());
        scene.idle(40);
        scene.world().modifyBlock(util.grid().at(2, 1, 2), s -> s.setValue(NarrowWalkwayBlock.CAPS, WalkwayCaps.RIGHT), false);
        scene.idle(40);
        scene.overlay().showText(80)
                .text("This also applies to walkway terminals.")
                .colored(PonderPalette.BLUE);
        scene.idle(30);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(4, 1, 2), Direction.NORTH), Pointing.RIGHT, 50)
                .rightClick().withItem(AllItems.WRENCH.asStack());
        scene.idle(40);
        scene.world().modifyBlock(util.grid().at(4, 1, 2), s -> s.setValue(NarrowWalkwayBlock.CAPS_SHAFT, WalkwayCaps.RIGHT), false);
        scene.idle(40);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("To remove the shaft, wrench the block while Sneaking.");
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 1, 2), Direction.NORTH), Pointing.RIGHT, 50)
                .whileSneaking().rightClick().withItem(AllItems.WRENCH.asStack());
        scene.idle(40);
        scene.world().modifyBlock(util.grid().at(2, 1, 2), s -> s.setValue(NarrowWalkwayBlock.CAPS, WalkwayCaps.NO_SHAFT), false);
        scene.idle(55);

        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("Shafts cannot be applied to escalators.")
                .colored(PonderPalette.RED);

        scene.idle(70);
        scene.markAsFinished();
    }

    public static void customizingWalkways(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("walkways/customizing_walkways", "Customizing Walkways and Escalators with Dyes and Handrails");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        scene.world().showSection(util.select().fromTo(0, 1, 2, 4, 1, 2), Direction.UP);
        scene.idle(40);
        scene.overlay().showText(80)
                .text("Walkway and escalator steps can be dyed by Right-Clicking them with a dye item.");
        scene.idle(30);
        scene.overlay().showControls(util.vector().topOf(2, 1, 2), Pointing.DOWN, 50)
                .withItem(new ItemStack(Items.YELLOW_DYE)).rightClick();
        scene.idle(40);
        scene.world().modifyBlockEntityNBT(util.select().fromTo(0, 1, 2, 4, 1, 2), WalkwayBlockEntity.class, tag -> {
            tag.putString("Dye", "YELLOW");
        });
        scene.idle(40);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("To remove the dye, Right-Click the walkway with a water item.");
        scene.idle(30);
        scene.overlay().showControls(util.vector().topOf(2, 1, 2), Pointing.DOWN, 50)
                .withItem(new ItemStack(Items.WATER_BUCKET)).rightClick();
        scene.idle(40);
        scene.world().modifyBlockEntityNBT(util.select().fromTo(0, 1, 2, 4, 1, 2), WalkwayBlockEntity.class, tag -> {
            tag.remove("Dye");
        });
        scene.idle(40);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Handrails can be added to the walkway by Right-Clicking the walkway with a belt item.");
        scene.idle(30);
        scene.overlay().showControls(util.vector().topOf(2, 1, 2), Pointing.DOWN, 50)
                .withItem(AllItems.BELT_CONNECTOR.asStack()).rightClick();
        scene.idle(50);
        scene.world().showIndependentSectionImmediately(util.select().fromTo(0, 2, 2, 4, 2, 2));
        scene.idle(30);
        scene.overlay().showText(50)
                .text("This will only work if there is space above the walkway.")
                .colored(PonderPalette.RED);
        scene.idle(80);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("The handrails can also be dyed and cleaned just like the walkway.");
        scene.idle(30);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 2, 2), Direction.NORTH), Pointing.RIGHT, 50)
                .withItem(new ItemStack(Items.RED_DYE)).rightClick();
        scene.idle(40);
        scene.world().modifyBlockEntityNBT(util.select().fromTo(0, 2, 2, 4, 2, 2), HandrailBlockEntity.class, tag -> {
            tag.putString("Dye", "RED");
        });
        scene.idle(40);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Finally, the handrails can also be made into glass by Right-Clicking them with a glass block.");
        scene.idle(30);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 2, 2), Direction.NORTH), Pointing.RIGHT, 50)
                .withItem(new ItemStack(Items.GLASS)).rightClick();
        scene.idle(40);
        scene.world().modifyBlocks(util.select().fromTo(0, 2, 2, 4, 2, 2), s -> BlockHelper.copyProperties(s,
                EscalatedBlocks.GLASS_WALKWAY_HANDRAIL.getDefaultState()), false);
        scene.idle(40);

        scene.markAsFinished();
    }

    public static void wideningWalkways(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("walkways/widening_walkways", "Widening Walkways and Escalators");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        scene.world().setBlocks(util.select().fromTo(1, 1, 3, 3, 1, 3), EscalatedBlocks.METAL_NARROW_WALKWAY
                .getDefaultState().setValue(NarrowWalkwayBlock.HORIZONTAL_FACING, Direction.WEST), false);
        scene.world().modifyBlocks(util.select().fromTo(0, 2, 3, 4, 2, 3), s -> s.setValue(WalkwayHandrailBlock.SIDE, AbstractHandrailBlock.Side.BOTH), false);
        scene.world().setBlocks(util.select().fromTo(1, 1, 2, 3, 1, 2), Blocks.AIR.defaultBlockState(), false);
        scene.world().setBlocks(util.select().position(0, 1, 2).add(util.select().position(4, 1, 2)),
                AllBlocks.SHAFT.getDefaultState().setValue(ShaftBlock.AXIS, Direction.Axis.Z), false);
        scene.world().showSection(util.select().fromTo(0, 1, 3, 4, 2, 3), Direction.UP);
        scene.idle(30);
        scene.world().showSection(util.select().fromTo(0, 1, 2, 4, 1, 2), Direction.DOWN);
        scene.idle(30);

        scene.overlay().showText(80)
                .text("Walkways can be widened by connecting a walkway with an adjacent shaft.");
        scene.idle(30);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(0, 1, 3), Direction.WEST), Pointing.LEFT, 30)
                .withItem(EscalatedItems.METAL_WALKWAY_STEPS.asStack()).rightClick();
        scene.idle(45);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(0, 1, 2), Direction.WEST), Pointing.LEFT, 30)
                .withItem(EscalatedItems.METAL_WALKWAY_STEPS.asStack()).rightClick();
        scene.idle(15);
        scene.overlay().showOutline(PonderPalette.GREEN, new Object(), util.select().fromTo(0, 1, 2, 0, 1, 3), 50);
        scene.idle(15);
        scene.world().restoreBlocks(util.select().fromTo(0, 1, 2, 4, 2, 3));
        scene.world().showIndependentSectionImmediately(util.select().fromTo(0, 2, 2, 4, 2, 2));
        scene.idle(60);

        scene.overlay().showOutlineWithText(util.select().fromTo(0, 2, 2, 4, 2, 3), 60)
                .attachKeyFrame()
                .colored(PonderPalette.BLUE)
                .text("The widening also widens handrails on top.");
        scene.idle(75);
        scene.overlay().showText(60)
                .text("However, if the handrail is obstructed by something, the handrail will break.")
                .colored(PonderPalette.RED);
        scene.idle(80);

        scene.markAsFinished();
    }

}
