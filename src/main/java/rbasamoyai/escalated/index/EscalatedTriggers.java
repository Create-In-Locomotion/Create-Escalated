package rbasamoyai.escalated.index;

import net.minecraft.advancements.CriteriaTriggers;
import rbasamoyai.escalated.advancements.SimpleEscalatedTrigger;

public class EscalatedTriggers {

    public static final SimpleEscalatedTrigger
        WALKWAY = add("walkway_builtin"),
        HANDRAIL = add("handrail_builtin"),
        ESCALATOR_100 = add("escalator_100_builtin"),
        ESCALATOR_100_NETHER = add("escalator_100_nether_builtin");

    public static void register() {}

    private static SimpleEscalatedTrigger add(String id) {
        SimpleEscalatedTrigger trigger = new SimpleEscalatedTrigger(id);
        CriteriaTriggers.register(trigger);
        return trigger;
    }

}
