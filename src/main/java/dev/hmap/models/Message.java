package dev.hmap.models;

import java.time.LocalDateTime;

public class Message {


    public enum Type {
        SENT,
        RECEIVED,
        INFO,
        ERROR
    }

    private String content;
    private LocalDateTime timeStamp;
    private Type type;

    public Message(String content, Type type){
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


}
