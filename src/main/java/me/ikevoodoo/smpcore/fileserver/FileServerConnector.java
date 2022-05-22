package me.ikevoodoo.smpcore.fileserver;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class FileServerConnector {

    private static HttpServer server;

    public static void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(0), 0);
            server.createContext("/", exchange -> {

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return server.getAddress().getPort();
    }

}
