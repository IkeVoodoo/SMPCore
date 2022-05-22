package me.ikevoodoo.smpcore.fileserver;

public record FileServerResponse(byte[] body, int statusCode) {

}
