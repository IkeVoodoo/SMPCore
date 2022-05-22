package me.ikevoodoo.smpcore.fileserver;

import java.util.function.Consumer;

public class FileServerRequest {
    
    private Consumer<FileServerResponse> onResponse;

    private final String url;
    
    public FileServerRequest(String url) {
        this.url = url;
    }

    public FileServerRequest onResponse(Consumer<FileServerResponse> onResponse) {
        this.onResponse = onResponse;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void emitResponse(FileServerResponse response) {
        if (onResponse != null) {
            onResponse.accept(response);
        }
    }


    
}
