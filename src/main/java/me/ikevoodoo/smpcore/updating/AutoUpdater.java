package me.ikevoodoo.smpcore.updating;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class AutoUpdater extends PluginProvider {

    private static final JsonParser JSON_PARSER = new JsonParser();

    private static final String GET_VERSIONS_URL = "https://api.spiget.org/v2/resources/%s/versions?sort=-releaseDate";
    private static final String GET_LATEST_VERSION_URL = "https://api.spiget.org/v2/resources/%s/versions/latest";
    private static final String DOWNLOAD_VERSION_URL = "https://api.spiget.org/v2/resources/%s/versions/%s/download";
    private static final long LEGACY_THRESHOLD = TimeUnit.DAYS.toSeconds(7);

    private final int resourceId;
    private UpdateData latestData;

    public AutoUpdater(SMPPlugin plugin, int resourceId) {
        super(plugin);
        this.resourceId = resourceId;
    }

    public UpdateData getLatestData() {
        return latestData;
    }

    public boolean downloadUpdate() {
        if (this.latestData == null) {
            return false; // Update check required
        }

        try {
            var url = new URL(DOWNLOAD_VERSION_URL.formatted(this.resourceId, this.latestData.latestVersion().id()));
            var connection = url.openConnection();

        } catch (IOException ignored) {
            // Don't handle IOExceptions
        }

        return false;
    }

    public CompletableFuture<UpdateData> checkForUpdates() {
        var future = new CompletableFuture<UpdateData>();

        CompletableFuture.runAsync(() -> {
            final var currentVersion = getPlugin().getDescription().getVersion();
            final var currentVersionData = findVersionData(currentVersion);
            final var latestVersionData = this.getLatestVersionData();
            final var updatePresent = currentVersionData.isBefore(latestVersionData);
            final var now = System.nanoTime();

            var updateData = new UpdateData(latestVersionData, currentVersionData, now, updatePresent);
            if (this.latestData != null && this.latestData.isAfter(updateData)) {
                future.complete(this.latestData);
                return; // A newer update data exists, cancel
            }

            this.latestData = updateData;
            future.complete(this.latestData);
        });

        return future;
    }

    private UpdateVersionData getLatestVersionData() {
        try {
            var url = new URL(GET_LATEST_VERSION_URL.formatted(this.resourceId));
            try(var reader = new InputStreamReader(url.openStream())) {
                final var object = JSON_PARSER.parse(reader).getAsJsonObject();

                return this.getUpdateData(object, null);
            }
        } catch (IOException ignored) {
            // Don't handle IOExceptions
        }

        return new UpdateVersionData(null, null, -1, true);
    }

    private UpdateVersionData findVersionData(String version) {
        try {
            var url = new URL(GET_VERSIONS_URL.formatted(this.resourceId));
            try(var reader = new InputStreamReader(url.openStream())) {
                final var array = JSON_PARSER.parse(reader).getAsJsonArray();

                if (array.size() == 0) {
                    return new UpdateVersionData(version, null, -1, false);
                }

                for (final var element : array) {
                    final var object = element.getAsJsonObject();

                    final var data = getUpdateData(object, version);
                    if (data == null) continue;

                    return data;
                }


            }
        } catch (IOException ignored) {
            // Don't handle IOExceptions
        }

        return new UpdateVersionData(version, null, -1, true);
    }

    /**
     * Extract an update version data from json
     *
     * @param object The JsonObject to get the values from.
     * @param requiredVersion The required version to match, if null no checking will be done.
     *                        If the version does not match then null is returned.
     */
    private UpdateVersionData getUpdateData(JsonObject object, String requiredVersion) {
        final var name = object.get("name");
        final var releaseDate = object.get("releaseDate");
        final var versionId = object.get("id");

        final var versionName = name == null ? null : name.getAsString();
        if (requiredVersion != null && !requiredVersion.equalsIgnoreCase(versionName)) {
           return null;
        }

        final var date = releaseDate == null ? null : new Date(releaseDate.getAsLong() * 1000);
        final var id = versionId == null ? -1 : versionId.getAsLong();

        return new UpdateVersionData(versionName, date, id, isLegacyDate(date));
    }

    private boolean isLegacyDate(Date date) {
        return date == null || date.toInstant().isBefore(Instant.from(LocalDateTime.now().minusSeconds(LEGACY_THRESHOLD)));
    }
}
