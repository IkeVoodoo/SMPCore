package me.ikevoodoo.smpcore.texture;

import java.util.Arrays;

public record ResourcePackData(String url, byte[] hash) {

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ResourcePackData data && url.equals(data.url) && Arrays.equals(hash, data.hash);
    }

    @Override
    public int hashCode() {
        return url.hashCode() ^ Arrays.hashCode(hash);
    }

    @Override
    public String toString() {
        return "TexturePackData[url=" + this.url + "]";
    }
}
