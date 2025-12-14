package dev.hmap.utils;

import dev.hmap.model.Message;

import java.util.function.Consumer;

public interface AlertInterface {

    void notifyStatusChanged(Consumer<String> onChangeStatus);
    void notifyError(Consumer<String> onError);
    void notifyMessage(Consumer<Message> onMessageReceived);
}
