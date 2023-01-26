package me.ikevoodoo.smpcore.utils.health;

public record HealthSetResult(int state, double oldHealth, double newHealth) {

    public static final int ABOVE_MAX = 1;
    public static final int BELOW_MIN = 2;
    public static final int OK = 3;

    public boolean isInRange() {
        return !this.isAboveMax() && !this.isBelowMin();
    }

    public boolean isAboveMax() {
        return (this.state & ABOVE_MAX) == ABOVE_MAX;
    }

    public boolean isBelowMin() {
        return (this.state & BELOW_MIN) == BELOW_MIN;
    }

}
