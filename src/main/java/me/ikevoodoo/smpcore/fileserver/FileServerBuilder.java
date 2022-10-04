package me.ikevoodoo.smpcore.fileserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FileServerBuilder {

    private int port;
    private int backlog;
    private int maxThreads;
    private long maxCacheSize;
    private String rootDirectory;
    private final List<Function<String, byte[]>> handlers;

    public FileServerBuilder() {
        this.port = 80;
        this.backlog = 50;
        this.maxThreads = 50;
        this.maxCacheSize = 1000000;
        this.rootDirectory = "./";
        this.handlers = new ArrayList<>();
    }

    public FileServerBuilder port(int port) {
        this.port = port;
        return this;
    }

    public FileServerBuilder backlog(int backlog) {
        this.backlog = backlog;
        return this;
    }

    public FileServerBuilder maxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        return this;
    }

    public FileServerBuilder maxCacheSize(long maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        return this;
    }

    public FileServerBuilder rootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
        return this;
    }

    public FileServerBuilder handle(Function<String, byte[]> handler) {
        this.handlers.add(handler);
        return this;
    }

    public FileServer build() throws IOException {
        return new FileServer(this.port, this.backlog, this.maxThreads, this.maxCacheSize, this.rootDirectory, this.handlers);
    }

}
