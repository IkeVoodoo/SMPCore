package me.ikevoodoo.smpcore.fileserver;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class FileServer implements Runnable {

    public static void start() {
        if(thread != null) return;
        FileServer server = FileServer.instance == null ? new FileServer() : FileServer.instance;
        thread = new Thread(server);
        thread.start();
    }

    public static void get(FileServerRequest request) {
        if(instance == null) instance = new FileServer();
        requests.add(request);
    }

    public static void stop() {
        if(thread == null) return;
        thread.interrupt();
        thread = null;
        requests.clear();
    }

    private static Thread thread;
    private static FileServer instance;
    private static final BlockingQueue<FileServerRequest> requests = new LinkedBlockingDeque<>();

    private FileServer() {

    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            FileServerRequest request;
            try {
                request = requests.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            File parentFolder = new File(Bukkit.getWorldContainer(), "resourcepacks");
            if(!parentFolder.exists()) {
                parentFolder.mkdirs();
                request.emitResponse(new FileServerResponse("File not found".getBytes(StandardCharsets.UTF_8), 404));
            } else {
                try {
                    request.emitResponse(new FileServerResponse(
                            Files.readAllBytes(new File(parentFolder, request.getUrl()).toPath()),
                            200));
                } catch (IOException e) {
                    request.emitResponse(new FileServerResponse(
                            ("Unable to GET " + request.getUrl()).getBytes(StandardCharsets.UTF_8),
                            404));
                }
            }
        }
    }

    public static void main(String[] args) {
        FileServer.start();
        new Thread(() -> {
            FileServer.get(new FileServerRequest("/test.txt").onResponse(res -> {
                System.out.println(res.statusCode() + " " + Arrays.toString(res.body()));
                FileServer.stop();
            }));

        }).start();
    }
}
