package me.ikevoodoo.smpcore.handlers;

public record EliminationData(String message, long banTime) {

    public static final String DEFAULT_MESSAGE = "§cYou have been eliminated.";

    public static EliminationData defaultMessage(long banTime) {
        return new EliminationData(DEFAULT_MESSAGE, banTime);
    }

    public static EliminationData infiniteTime() {
        return EliminationData.defaultMessage(Long.MAX_VALUE);
    }

    public static EliminationData infiniteTime(String message) {
        return new EliminationData(message, Long.MAX_VALUE);
    }

    public EliminationData withMessage(String message) {
        return new EliminationData(message, this.banTime);
    }

    public EliminationData withBanTime(long banTime) {
        return new EliminationData(this.message, banTime);
    }

    /**
     * @return The time the player should be banned for in milliseconds
     *         If the banTime is Long.MAX_VALUE, this method will return Long.MAX_VALUE
     *         Otherwise, this method will return {@link System#currentTimeMillis()} + {@link EliminationData#banTime()}
     * */
    public long getCacheTime() {
        return this.banTime == Long.MAX_VALUE ? this.banTime : System.currentTimeMillis() + this.banTime;
    }


}
