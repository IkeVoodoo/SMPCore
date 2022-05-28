package me.ikevoodoo.smpcore.callbacks.chat;

public interface ChatTransactionListener {

    boolean onChat(String message);

    default void onComplete(boolean success) {
        // no-op
    }

}
