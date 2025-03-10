package rbasamoyai.escalated.fabric;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack.Factory;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.datagen.EscalatedLangGen;
import rbasamoyai.escalated.datagen.assets.fabric.EscalatedPartialsGen;
import rbasamoyai.escalated.datagen.data.fabric.EscalatedCraftingRecipeProvider;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class EscalatedDataGeneration implements DataGeneratorEntrypoint {

	public static final String PLATFORM = System.getProperty("escalated.datagen.platform");

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		Path modResources = Paths.get(System.getProperty(ExistingFileHelper.EXISTING_RESOURCES));
		ExistingFileHelper helper = new ExistingFileHelper(
			Set.of(modResources), Set.of("create"), false, null, null
		);
		CreateEscalated.REGISTRATE.setupDatagen(generator.createPack(), helper);

		FabricDataGenerator.Pack modDatapack = generator.createPack();

		modDatapack.addProvider((Factory<EscalatedPartialsGen>) output -> new EscalatedPartialsGen(output, CreateEscalated.MOD_ID, helper));

		EscalatedLangGen.prepare();
		EscalatedCraftingRecipeProvider.register();
	}

	public static boolean isForge() { return "forge".equals(PLATFORM); }
	public static boolean isFabric() { return "fabric".equals(PLATFORM); }

}
