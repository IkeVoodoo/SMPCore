package me.ikevoodoo.smpcore.utils;

import me.ikevoodoo.smpcore.functions.CatchFunction;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

public class URLUtils {

    private URLUtils() {

    }

    public static <V> V ifValid(String url, CatchFunction<HttpURLConnection, V> runnable, Consumer<Exception> error) {
        try {
            return runnable.accept(openConnection(url));
        } catch (Exception e) {
            error.accept(e);
        }
        return null;
    }

    public static <V> V ifValid(String url, CatchFunction<HttpURLConnection, V> runnable) {
        return ifValid(url, runnable, e -> {});
    }

    public static HttpURLConnection openConnection(String url) throws IOException {
        return openConnection(new URL(url));
    }

    public static HttpURLConnection openConnection(URL url) throws IOException {
        HttpURLConnection conn = getConnection(url);
        conn.connect();
        return conn;
    }

    public static HttpURLConnection getConnection(String url) throws IOException {
        return getConnection(new URL(url));
    }

    public static HttpURLConnection getConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.54 Safari/537.36");
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("accept-encoding", "gzip, deflate, br");
        conn.setRequestProperty("keep-alive", "true");
        conn.setRequestProperty("host", url.getHost());
        return conn;
    }

}
