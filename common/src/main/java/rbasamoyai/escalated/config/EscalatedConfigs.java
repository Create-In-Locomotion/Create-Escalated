package rbasamoyai.escalated.config;

import com.simibubi.create.foundation.config.ConfigBase;
import com.simibubi.create.foundation.config.ui.BaseConfigScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import rbasamoyai.escalated.CreateEscalated;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Adapted from {@link com.simibubi.create.infrastructure.config.AllConfigs}
 */
public class EscalatedConfigs {

    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

    public static EscalatedServerCfg SERVER;

    public static ConfigBase byType(ModConfig.Type type) {
        return CONFIGS.get(type);
    }

    private static <T extends EscalatedConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
        Pair<T, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(builder -> {
            T config = factory.get();
            config.registerAll(builder);
            return config;
        });

        T config = specPair.getLeft();
        config.specification = specPair.getRight();
        CONFIGS.put(side, config);
        return config;
    }

    public static void registerConfigs(BiConsumer<ModConfig.Type, ForgeConfigSpec> cons) {
        SERVER = register(EscalatedServerCfg::new, ModConfig.Type.SERVER);

        for (Map.Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet())
            cons.accept(pair.getKey(), pair.getValue().specification);
    }

    public static void onLoad(ModConfig modConfig) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == modConfig
                    .getSpec())
                config.onLoad();
    }

    public static void onReload(ModConfig modConfig) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == modConfig
                    .getSpec())
                config.onReload();
    }

    public static BaseConfigScreen createConfigScreen(Screen parent) {
        BaseConfigScreen.setDefaultActionFor(CreateEscalated.MOD_ID, (base) ->
                base.withSpecs(null, null, SERVER.specification) // not including common since there's nothing there
                        .withTitles("", "", "Server Settings")
        );
        return new BaseConfigScreen(parent, CreateEscalated.MOD_ID);
    }

}
