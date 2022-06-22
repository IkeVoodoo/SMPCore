package me.ikevoodoo.smpcore.utils;

import java.util.HashMap;

public class ThreadUtils {

    private static final HashMap<Integer, Thread> threads = new HashMap<>();

    private ThreadUtils() {

    }

    public static void start(int id, Runnable runnable) {
        stop(id);
        Thread thread = new Thread(runnable);
        thread.start();
        threads.put(id, thread);
    }

    public static boolean isAlive(int id) {
        return threads.containsKey(id);
    }

    public static void stop(int id) {
        Thread thread = threads.remove(id);
        if (thread != null) thread.interrupt();
    }

}
