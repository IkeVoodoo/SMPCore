package me.ikevoodoo.smpcore.updating;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public record UpdateVersionData(String name, Date releaseDate, long id, boolean isLegacy) implements Comparable<UpdateVersionData> {
    @Override
    public int compareTo(@NotNull UpdateVersionData o) {
        return this.releaseDate().compareTo(o.releaseDate());
    }

    public boolean isAfter(UpdateVersionData o) {
        return this.compareTo(o) > 0;
    }

    public boolean isBefore(UpdateVersionData o) {
        return this.compareTo(o) < 0;
    }
}
