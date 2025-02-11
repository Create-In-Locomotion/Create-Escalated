package rbasamoyai.escalated.fabric;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.ModGroup;

import java.util.function.Supplier;

public class ModGroupImpl {

	public static Supplier<CreativeModeTab> wrapGroup(String id, Supplier<CreativeModeTab> sup) {
		CreativeModeTab tab = sup.get();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ModGroup.makeKey(id), tab);
		return sup;
	}

	public static CreativeModeTab.Builder createBuilder() {
		return FabricItemGroup.builder();
	}

	public static void useModTab(ResourceKey<CreativeModeTab> key) { CreateEscalated.REGISTRATE.setCreativeTab(key); }

}
