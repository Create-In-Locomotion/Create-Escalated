package rbasamoyai.escalated.index;

import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.world.item.DyeColor;
import rbasamoyai.escalated.CreateEscalated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class EscalatedBlockPartials {

    private static final Collection<Runnable> DEFERRED_MODEL_CALLBACKS = new ArrayList<>();

    //////// Metal steps ////////
    public static final PartialModel
        METAL_ESCALATOR_STEP = escalatorSteps("metal"),
        METAL_WALKWAY_STEP = walkwaySteps("metal");
    public static final Map<DyeColor, PartialModel>
        DYED_METAL_ESCALATOR_STEPS = dyedEscalatorSteps("metal"),
        DYED_METAL_WALKWAY_STEPS = dyedWalkwaySteps("metal");

    private static PartialModel escalatorSteps(String material) { return escalatorSteps(material, ""); }

    private static PartialModel escalatorSteps(String material, String suffix) {
        return new PartialModel(CreateEscalated.resource("block/" + material + "_escalator_step" + suffix));
    }

    private static PartialModel walkwaySteps(String material) { return walkwaySteps(material, ""); }

    private static PartialModel walkwaySteps(String material, String suffix) {
        return new PartialModel(CreateEscalated.resource("block/" + material + "_walkway_step" + suffix));
    }

    private static Map<DyeColor, PartialModel> dyedEscalatorSteps(String material) {
        Map<DyeColor, PartialModel> map = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DyeColor.values())
            map.put(color, escalatorSteps(material, "_" + color.getName()));
        return map;
    }

    private static Map<DyeColor, PartialModel> dyedWalkwaySteps(String material) {
        Map<DyeColor, PartialModel> map = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DyeColor.values())
            map.put(color, walkwaySteps(material, "_" + color.getName()));
        return map;
    }

    public static void init() {}

    public static void resolveDeferredModels() {
        for (Runnable run : DEFERRED_MODEL_CALLBACKS)
            run.run();
        DEFERRED_MODEL_CALLBACKS.clear();
    }

}
