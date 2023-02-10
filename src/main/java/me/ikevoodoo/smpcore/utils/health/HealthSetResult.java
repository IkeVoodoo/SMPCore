package me.ikevoodoo.smpcore.utils.health;

public record HealthSetResult(int state, double oldHealth, double newHealth) {

    public static final int OK = 0;
    public static final int ABOVE_MAX = 1;
    public static final int BELOW_MIN = 2;

    public boolean isInRange() {
        return !this.isAboveMax() && !this.isBelowMin();
    }

    public boolean isAboveMax() {
        return (this.state & ABOVE_MAX) == ABOVE_MAX;
    }

    public boolean isBelowMin() {
        return (this.state & BELOW_MIN) == BELOW_MIN;
    }

    public String getStateString() {
        var builder = new StringBuilder();
        if (this.isAboveMax()) builder.append("ABOVE_MAX");
        if (this.isBelowMin()) builder.append(" && BELOW_MIN");
        if (builder.length() == 0) builder.append("OK");
        return builder.toString();
    }

    @Override
    public String toString() {
        return "HealthSetResult[oldHealth=%s, newHealth=%s, state=%s]".formatted(this.oldHealth, this.newHealth, this.getStateString());
    }
}
