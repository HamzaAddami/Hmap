package dev.hmap.utils;

import dev.hmap.models.Message;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.function.Consumer;

public class AlertUtils extends Alert implements AlertInterface{


    public String alertMessage;
    public Message message;

    public AlertUtils(AlertType alertType, String alertMessage){
        super(alertType);
        this.alertMessage = alertMessage;
    }

    public AlertUtils(AlertType alertType, Message message){
        super(alertType);
        this.message = message;
    }

    @Override
    public void notifyStatusChanged(Consumer<String> onChangeStatus) {

    }

    @Override
    public void notifyError(Consumer<String> onError) {

    }

    @Override
    public void notifyMessage(Consumer<Message> onMessageReceived) {

    }
}
