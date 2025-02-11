package rbasamoyai.escalated.index;

import com.jozufozu.flywheel.core.PartialModel;
import rbasamoyai.escalated.CreateEscalated;

import java.util.ArrayList;
import java.util.Collection;

public class EscalatedBlockPartials {

    private static final Collection<Runnable> DEFERRED_MODEL_CALLBACKS = new ArrayList<>();

    public static final PartialModel
        METAL_ESCALATOR_STEP = escalatorSteps("metal"),
        METAL_WALKWAY_STEP = walkwaySteps("metal");

    private static PartialModel escalatorSteps(String material) {
        return new PartialModel(CreateEscalated.resource("block/" + material + "_escalator_step"));
    }

    private static PartialModel walkwaySteps(String material) {
        return new PartialModel(CreateEscalated.resource("block/" + material + "_walkway_step"));
    }

    public static void init() {}

    public static void resolveDeferredModels() {
        for (Runnable run : DEFERRED_MODEL_CALLBACKS)
            run.run();
        DEFERRED_MODEL_CALLBACKS.clear();
    }

}
