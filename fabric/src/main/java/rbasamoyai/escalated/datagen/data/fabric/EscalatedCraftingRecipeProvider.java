package rbasamoyai.escalated.datagen.data.fabric;

import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.index.EscalatedItems;

import java.util.function.Consumer;

import static com.tterrag.registrate.providers.RegistrateRecipeProvider.getHasName;
import static com.tterrag.registrate.providers.RegistrateRecipeProvider.has;
import static rbasamoyai.escalated.fabric.EscalatedDataGeneration.isForge;

public class EscalatedCraftingRecipeProvider {

    public static void register() {
        CreateEscalated.REGISTRATE.addDataGenerator(ProviderType.RECIPE, EscalatedCraftingRecipeProvider::buildCraftingRecipes);
    }

    public static void buildCraftingRecipes(Consumer<FinishedRecipe> cons) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EscalatedItems.METAL_WALKWAY_STEPS.get())
                .define('C', Items.CHAIN).define('P', getMetalPlateTag())
                .pattern("CPC")
                .pattern("CPC")
                .pattern("CPC")
                .unlockedBy(getHasName(Items.CHAIN), has(Items.CHAIN))
                .save(cons);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, EscalatedItems.WOODEN_WALKWAY_STEPS.get())
                .define('C', Items.CHAIN).define('P', ItemTags.WOODEN_SLABS)
                .pattern("CPC")
                .pattern("CPC")
                .pattern("CPC")
                .unlockedBy(getHasName(Items.CHAIN), has(Items.CHAIN))
                .save(cons);
    }

    private static TagKey<Item> getMetalPlateTag() {
        ResourceLocation loc = isForge() ? new ResourceLocation("forge", "plates/iron")
                : new ResourceLocation("c", "iron_plates"); // TODO change for 1.21 to c:iron_plates
        return TagKey.create(Registries.ITEM, loc);
    }

    private EscalatedCraftingRecipeProvider() {}

}
