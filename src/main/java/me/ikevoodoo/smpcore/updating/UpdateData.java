package me.ikevoodoo.smpcore.updating;

import org.jetbrains.annotations.NotNull;

public record UpdateData(UpdateVersionData latestVersion, UpdateVersionData currentVersion, long checkedAt, boolean isUpdatePresent) implements Comparable<UpdateData> {
    @Override
    public int compareTo(@NotNull UpdateData o) {
        return Long.compare(this.checkedAt(), o.checkedAt());
    }

    public boolean isAfter(UpdateData o) {
        return this.compareTo(o) > 0;
    }

    public boolean isBefore(UpdateData o) {
        return this.compareTo(o) < 0;
    }
}
