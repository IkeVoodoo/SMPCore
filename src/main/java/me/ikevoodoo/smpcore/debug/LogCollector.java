package me.ikevoodoo.smpcore.debug;

import org.bukkit.Bukkit;

import java.time.ZoneId;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public final class LogCollector  {

    private static final StringBuilder SERVER_LOGS = new StringBuilder();

    public static void init() {
        Bukkit.getLogger().addHandler(new LogHandler(SERVER_LOGS));
    }

    public static String getLogs() {
        return SERVER_LOGS.toString();
    }

    private static class LogHandler extends Handler {

        private final StringBuilder logs;

        private LogHandler(StringBuilder logs) {
            this.logs = logs;
        }

        @Override
        public void publish(LogRecord record) {
            var current = Thread.currentThread().getThreadGroup();
            ThreadGroup parent;
            while ((parent = current.getParent()) != null) {
                current = parent;
            }
            var count = current.activeCount();
            var threads = new Thread[count];
            current.enumerate(threads);

            Thread currentThread = null;
            for (int i = 0; i < threads.length; i++) {
                var thread = threads[i];
                var id = thread.getId();

                if (id == record.getLongThreadID()) {
                    currentThread = thread;
                    break;
                }
            }

            if (currentThread == null) {
                return;
            }


            var time = record.getInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            var timeString = time.getHour() + ":" + time.getMinute() + ":" + time.getSecond();

            var message = "[%s] [%s/%s]: %s".formatted(
                    timeString,
                    currentThread.getName(),
                    record.getLevel().getName(),
                    record.getMessage()
            );

            this.logs.append(message).append('\n');
        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }
    }

}
