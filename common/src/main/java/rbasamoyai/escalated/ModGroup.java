package rbasamoyai.escalated;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import rbasamoyai.escalated.index.EscalatedItems;

import java.util.Arrays;
import java.util.function.Supplier;

public class ModGroup {

    public static final ResourceKey<CreativeModeTab> MAIN_TAB_KEY = makeKey("base");

    public static final Supplier<CreativeModeTab> GROUP = wrapGroup("base", () -> createBuilder()
            .title(Component.translatable("itemGroup." + CreateEscalated.MOD_ID))
            .icon(EscalatedItems.METAL_WALKWAY_STEPS::asStack)
            .displayItems((param, output) -> {
                output.acceptAll(Arrays.asList(
                        EscalatedItems.METAL_WALKWAY_STEPS.asStack(),
                        EscalatedItems.WOODEN_WALKWAY_STEPS.asStack()
                ));
            })
            .build());

    @ExpectPlatform public static Supplier<CreativeModeTab> wrapGroup(String id, Supplier<CreativeModeTab> sup) { throw new AssertionError(); }
    @ExpectPlatform public static CreativeModeTab.Builder createBuilder() { throw new AssertionError(); }

    @ExpectPlatform public static void useModTab(ResourceKey<CreativeModeTab> key) { throw new AssertionError(); }

    public static ResourceKey<CreativeModeTab> makeKey(String id) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, CreateEscalated.resource(id));
    }

    public static void register() {
        CreateEscalated.REGISTRATE.addRawLang("itemGroup." + CreateEscalated.MOD_ID, "Create: Escalated");
    }

}
