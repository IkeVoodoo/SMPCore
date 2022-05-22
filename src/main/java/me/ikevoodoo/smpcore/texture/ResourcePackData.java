package me.ikevoodoo.smpcore.texture;

import java.util.Arrays;

public record ResourcePackData(String location, byte[] hash) {

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ResourcePackData data && location.equals(data.location) && Arrays.equals(hash, data.hash);
    }

    @Override
    public int hashCode() {
        return location.hashCode() ^ Arrays.hashCode(hash);
    }

    @Override
    public String toString() {
        return "TexturePackData[location=" + this.location + "]";
    }
}
