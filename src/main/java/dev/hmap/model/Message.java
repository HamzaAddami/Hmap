package dev.hmap.model;

import java.time.LocalDateTime;
import dev.hmap.enums.MessageType;

public class Message {


    private String content;
    private LocalDateTime timeStamp;
    private MessageType type;

    public Message(String content, MessageType type){
        this.content = content;
        this.type = type;
        this.timeStamp = LocalDateTime.now();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }


}
