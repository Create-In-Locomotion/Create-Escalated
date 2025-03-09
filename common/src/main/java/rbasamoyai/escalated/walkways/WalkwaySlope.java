package rbasamoyai.escalated.walkways;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum WalkwaySlope implements StringRepresentable {
    HORIZONTAL,
    BOTTOM,
    MIDDLE,
    TOP,
    TERMINAL;

    private final String id = this.name().toLowerCase(Locale.ROOT);

    @Override public String getSerializedName() { return this.id; }

}
