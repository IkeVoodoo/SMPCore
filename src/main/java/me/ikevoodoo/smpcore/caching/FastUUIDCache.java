package me.ikevoodoo.smpcore.caching;

import me.ikevoodoo.smpcore.utils.NumberUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class FastUUIDCache {

    private final HashMap<UUID, Number> uuids = new HashMap<>();

    public Map<UUID, Number> fetch() {
        return uuids;
    }

    public void add(UUID uuid, Number value) {
        uuids.put(uuid, value);
    }

    public void remove(UUID uuid) {
        uuids.remove(uuid);
    }

    public void save(OutputStream stream) throws IOException {
        stream.write(NumberUtils.toByteArray(uuids.size()));
        for (Map.Entry<UUID, Number> uuid : uuids.entrySet()) {
            stream.write(uuid.getKey().toString().getBytes(StandardCharsets.UTF_8));
            byte[] array = NumberUtils.toByteArray(uuid.getValue());
            stream.write(NumberUtils.toByteArray(array.length));
            stream.write(array);
        }
    }

    public void load(InputStream stream) throws IOException {
        uuids.clear();
        if (stream.available() == 0) {
            return;
        }
        int size = NumberUtils.toInt(stream.readNBytes(4));
        for (int i = 0; i < size; i++) {
            String uuid = new String(stream.readNBytes(36), StandardCharsets.UTF_8);
            int length = NumberUtils.toInt(stream.readNBytes(4));
            byte[] array = stream.readNBytes(length);
            uuids.put(UUID.fromString(uuid), NumberUtils.fromArray(array));
        }
    }

    public boolean has(UUID id) {
        return this.uuids.containsKey(id);
    }
}
