package rbasamoyai.escalated.datagen.assets.fabric;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import io.github.fabricators_of_create.porting_lib.models.generators.block.BlockModelProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import rbasamoyai.escalated.CreateEscalated;

public class EscalatedPartialsGen extends BlockModelProvider {

    public EscalatedPartialsGen(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        walkwayAndEscalatorSteps("metal", "walkway_steps/metal");
        walkwayAndEscalatorSteps("wooden", "walkway_steps/wooden");
    }

    protected void walkwayAndEscalatorSteps(String material) { walkwayAndEscalatorSteps(material, material); }

    protected void walkwayAndEscalatorSteps(String material, String textureMaterial) {
        ResourceLocation walkwayStepBase = CreateEscalated.resource("block/walkway_step");
        ResourceLocation walkwayStepLeftBase = CreateEscalated.resource("block/walkway_step_left");
        ResourceLocation walkwayStepRightBase = CreateEscalated.resource("block/walkway_step_right");
        ResourceLocation walkwayStepCenterBase = CreateEscalated.resource("block/walkway_step_center");

        ResourceLocation escalatorStepBase = CreateEscalated.resource("block/escalator_step");
        ResourceLocation escalatorStepLeftBase = CreateEscalated.resource("block/escalator_step_left");
        ResourceLocation escalatorStepRightBase = CreateEscalated.resource("block/escalator_step_right");
        ResourceLocation escalatorStepCenterBase = CreateEscalated.resource("block/escalator_step_center");

        String walkwayStep = material + "_walkway_step";
        String escalatorStep = material + "_escalator_step";
        String texture = textureMaterial + "_walkway_step";

        getBuilder("block/" + walkwayStep)
                .parent(getExistingFile(walkwayStepBase))
                .texture("steps", modLoc("block/" + texture));
        getBuilder("block/" + walkwayStep + "_left")
                .parent(getExistingFile(walkwayStepLeftBase))
                .texture("steps", modLoc("block/" + texture));
        getBuilder("block/" + walkwayStep + "_right")
                .parent(getExistingFile(walkwayStepRightBase))
                .texture("steps", modLoc("block/" + texture));
        getBuilder("block/" + walkwayStep + "_center")
                .parent(getExistingFile(walkwayStepCenterBase))
                .texture("steps", modLoc("block/" + texture));

        getBuilder("block/" + escalatorStep)
                .parent(getExistingFile(escalatorStepBase))
                .texture("steps", modLoc("block/" + texture));
        getBuilder("block/" + escalatorStep + "_left")
                .parent(getExistingFile(escalatorStepLeftBase))
                .texture("steps", modLoc("block/" + texture));
        getBuilder("block/" + escalatorStep + "_right")
                .parent(getExistingFile(escalatorStepRightBase))
                .texture("steps", modLoc("block/" + texture));
        getBuilder("block/" + escalatorStep + "_center")
                .parent(getExistingFile(escalatorStepCenterBase))
                .texture("steps", modLoc("block/" + texture));

        for (DyeColor color : DyeColor.values()) {
            String s = color.getSerializedName();
            getBuilder("block/" + walkwayStep + "_" + s)
                    .parent(getExistingFile(walkwayStepBase))
                    .texture("steps", modLoc("block/" + texture + "_" + s));
            getBuilder("block/" + walkwayStep + "_left_" + s)
                    .parent(getExistingFile(walkwayStepLeftBase))
                    .texture("steps", modLoc("block/" + texture + "_" + s));
            getBuilder("block/" + walkwayStep + "_right_" + s)
                    .parent(getExistingFile(walkwayStepRightBase))
                    .texture("steps", modLoc("block/" + texture + "_" + s));
            getBuilder("block/" + walkwayStep + "_center_" + s)
                    .parent(getExistingFile(walkwayStepCenterBase))
                    .texture("steps", modLoc("block/" + texture + "_" + s));

            getBuilder("block/" + escalatorStep + "_" + s)
                    .parent(getExistingFile(escalatorStepBase))
                    .texture("steps", modLoc("block/" + texture + "_" + s));
            getBuilder("block/" + escalatorStep + "_left_" + s)
                    .parent(getExistingFile(escalatorStepLeftBase))
                    .texture("steps", modLoc("block/" + texture + "_" + s));
            getBuilder("block/" + escalatorStep + "_right_" + s)
                    .parent(getExistingFile(escalatorStepRightBase))
                    .texture("steps", modLoc("block/" + texture + "_" + s));
            getBuilder("block/" + escalatorStep + "_center_" + s)
                    .parent(getExistingFile(escalatorStepCenterBase))
                    .texture("steps", modLoc("block/" + texture + "_" + s));
        }
    }

}
