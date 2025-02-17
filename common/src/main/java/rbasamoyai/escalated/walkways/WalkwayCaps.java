package rbasamoyai.escalated.walkways;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum WalkwayCaps implements StringRepresentable {
    NONE,
    LEFT,
    RIGHT,
    BOTH,
    NO_SHAFT;

    private final String name = this.name().toLowerCase(Locale.ROOT);

    public boolean hasLeftCap() { return this == LEFT || this == BOTH || this == NO_SHAFT; }
    public boolean hasRightCap() { return this == RIGHT || this == BOTH || this == NO_SHAFT; }

    public WalkwayCaps toggleLeft() {
        return switch (this) {
            case NONE -> LEFT;
            case LEFT -> NONE;
            case RIGHT -> BOTH;
            case BOTH -> RIGHT;
            case NO_SHAFT -> NO_SHAFT;
        };
    }

    public WalkwayCaps toggleRight() {
        return switch (this) {
            case NONE -> RIGHT;
            case LEFT -> BOTH;
            case RIGHT -> NONE;
            case BOTH -> LEFT;
            case NO_SHAFT -> NO_SHAFT;
        };
    }

    @Override public String getSerializedName() { return this.name; }

}
