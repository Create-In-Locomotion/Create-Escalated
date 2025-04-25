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
        METAL_ESCALATOR_STEP_LEFT = escalatorSteps("metal", "_left"),
        METAL_ESCALATOR_STEP_RIGHT = escalatorSteps("metal", "_right"),
        METAL_ESCALATOR_STEP_CENTER = escalatorSteps("metal", "_center"),
        METAL_WALKWAY_STEP = walkwaySteps("metal"),
        METAL_WALKWAY_STEP_LEFT = walkwaySteps("metal", "_left"),
        METAL_WALKWAY_STEP_RIGHT = walkwaySteps("metal", "_right"),
        METAL_WALKWAY_STEP_CENTER = walkwaySteps("metal", "_center");
    public static final Map<DyeColor, PartialModel>
        DYED_METAL_ESCALATOR_STEPS = dyedEscalatorSteps("metal"),
        DYED_METAL_ESCALATOR_STEPS_LEFT = dyedEscalatorSteps("metal", "_left"),
        DYED_METAL_ESCALATOR_STEPS_RIGHT = dyedEscalatorSteps("metal", "_right"),
        DYED_METAL_ESCALATOR_STEPS_CENTER = dyedEscalatorSteps("metal", "_center"),
        DYED_METAL_WALKWAY_STEPS = dyedWalkwaySteps("metal"),
        DYED_METAL_WALKWAY_STEPS_LEFT = dyedWalkwaySteps("metal", "_left"),
        DYED_METAL_WALKWAY_STEPS_RIGHT = dyedWalkwaySteps("metal", "_right"),
        DYED_METAL_WALKWAY_STEPS_CENTER = dyedWalkwaySteps("metal", "_center");
    // TODO wooden steps

    //////// Handrails ////////
    public static final PartialModel
        HANDRAIL_START = handrail("start"),
        HANDRAIL_END = handrail("end"),
        HANDRAIL_HORIZONTAL = handrail("horizontal"),
        HANDRAIL_BOTTOM = handrail("bottom"),
        HANDRAIL_MIDDLE = handrail("middle"),
        HANDRAIL_TOP = handrail("top");

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

    private static Map<DyeColor, PartialModel> dyedEscalatorSteps(String material, String suffix) {
        Map<DyeColor, PartialModel> map = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DyeColor.values())
            map.put(color, escalatorSteps(material, suffix + "_" + color.getName()));
        return map;
    }

    private static Map<DyeColor, PartialModel> dyedWalkwaySteps(String material) {
        Map<DyeColor, PartialModel> map = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DyeColor.values())
            map.put(color, walkwaySteps(material, "_" + color.getName()));
        return map;
    }

    private static Map<DyeColor, PartialModel> dyedWalkwaySteps(String material, String suffix) {
        Map<DyeColor, PartialModel> map = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DyeColor.values())
            map.put(color, walkwaySteps(material, suffix + "_" + color.getName()));
        return map;
    }

    private static PartialModel handrail(String type) {
        return new PartialModel(CreateEscalated.resource("block/moving_handrail/" + type));
    }

    public static void init() {}

    public static void resolveDeferredModels() {
        for (Runnable run : DEFERRED_MODEL_CALLBACKS)
            run.run();
        DEFERRED_MODEL_CALLBACKS.clear();
    }

}
