package me.ikevoodoo.smpcore.fileserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.ikevoodoo.smpcore.utils.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class FileServerHandler implements HttpHandler {
    private final long maxCacheSize;
    private final File rootDirectory;
    private final List<Function<String, byte[]>> handlers;

    private final HashMap<String, byte[]> cache;

    public FileServerHandler(long maxCacheSize, String rootDirectory, List<Function<String, byte[]>> handlers) {
        this.maxCacheSize = maxCacheSize;
        this.rootDirectory = new File(rootDirectory);
        this.handlers = handlers;

        this.cache = new HashMap<>();

        if (!this.rootDirectory.exists()) {
            this.rootDirectory.mkdirs();
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        for (Function<String, byte[]> handler : handlers) {
            byte[] data = handler.apply(path);
            if (data != null) {
                exchange.sendResponseHeaders(200, data.length);
                exchange.getResponseBody().write(data);
                exchange.getResponseBody().close();
                return;
            }
        }

        if (cache.containsKey(path)) {
            byte[] data = cache.get(path);
            exchange.sendResponseHeaders(200, data.length);
            exchange.getResponseBody().write(data);
            return;
        }

        File file = new File(rootDirectory, path);
        if (!file.exists()) {
            exchange.sendResponseHeaders(404, 0);
            return;
        }

        if (file.length() > maxCacheSize) {
            exchange.sendResponseHeaders(200, file.length());
            StreamUtils.readAndDump(Files.newInputStream(file.toPath()), exchange.getResponseBody());
            return;
        }

        byte[] data = Files.readAllBytes(file.toPath());
        cache.put(path, data);
        exchange.sendResponseHeaders(200, data.length);
        exchange.getResponseBody().write(data);
    }
}
