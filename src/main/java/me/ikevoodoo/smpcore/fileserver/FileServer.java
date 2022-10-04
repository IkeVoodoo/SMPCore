package me.ikevoodoo.smpcore.fileserver;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class FileServer {

    private final int port;
    private final int backlog;
    private final int maxThreads;

    private final HttpServer server;
    private ExecutorService executorService;

    protected FileServer(int port, int backlog, int maxThreads, long maxCacheSize, String rootDirectory, List<Function<String, byte[]>> handlers) throws IOException {
        this.port = port;
        this.backlog = backlog;
        this.maxThreads = maxThreads;

        this.server = HttpServer.create(new InetSocketAddress(port), backlog);
        this.server.createContext("/", new FileServerHandler(maxCacheSize, rootDirectory, handlers));
        this.executorService = Executors.newFixedThreadPool(maxThreads);
        this.server.setExecutor(this.executorService);
    }

    public void start() {
        this.server.start();
    }

    public void stop() {
        this.server.stop(0);
        this.executorService.shutdown();
    }

    public static FileServerBuilder on(int port) {
        return new FileServerBuilder().port(port);
    }

}
