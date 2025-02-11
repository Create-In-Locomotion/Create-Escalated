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
        walkwaySteps("metal");
    }

    protected void walkwaySteps(String material) {
        ResourceLocation walkwayStepBase = CreateEscalated.resource("block/walkway_step");
        ResourceLocation escalatorStepBase = CreateEscalated.resource("block/escalator_step");

        String walkwayStep = material + "_walkway_step";
        String escalatorStep = material + "_escalator_step";

        getBuilder("block/" + walkwayStep)
                .parent(getExistingFile(walkwayStepBase))
                .texture("steps", modLoc("block/" + walkwayStep));
        getBuilder("block/" + escalatorStep)
                .parent(getExistingFile(escalatorStepBase))
                .texture("steps", modLoc("block/" + walkwayStep));

//        for (DyeColor color : DyeColor.values()) {
//            String s = color.getSerializedName();
//            getBuilder("block/" + s + "_" + walkwayStep)
//                    .parent(getExistingFile(walkwayStepBase))
//                    .texture("steps", modLoc("block/" + walkwayStep));
//            getBuilder("block/" + s + "_" + escalatorStep)
//                    .parent(getExistingFile(escalatorStepBase))
//                    .texture("steps", modLoc("block/" + walkwayStep));
//        }
    }

}
