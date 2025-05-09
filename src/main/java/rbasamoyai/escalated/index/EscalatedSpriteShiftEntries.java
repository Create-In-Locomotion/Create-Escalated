package rbasamoyai.escalated.index;

import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SpriteShifter;
import net.minecraft.world.item.DyeColor;
import rbasamoyai.escalated.CreateEscalated;

import java.util.EnumMap;
import java.util.Map;

public class EscalatedSpriteShiftEntries {

    public static final Map<DyeColor, SpriteShiftEntry> DYED_HANDRAILS = new EnumMap<>(DyeColor.class);
    public static final SpriteShiftEntry HANDRAIL = get("block/moving_handrail", "block/moving_handrail_scroll");

    static {
        populateMaps();
    }

    public static void init() {}

    private static void populateMaps() {
        for (DyeColor color : DyeColor.values()) {
            DYED_HANDRAILS.put(color, get("block/moving_handrail", "block/moving_handrail/" + color.getSerializedName() + "_scroll"));
        }
    }

    private static SpriteShiftEntry get(String originalLocation, String targetLocation) {
        return SpriteShifter.get(CreateEscalated.resource(originalLocation), CreateEscalated.resource(targetLocation));
    }

}
