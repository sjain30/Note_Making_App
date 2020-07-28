package com.sajal.notemakingapp;

public class Notes {

    public String title;
    public String body;
    public int priority;
    public String image;
    public long timestamp;

    public Notes(){

    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public int getPriority() {
        return priority;
    }

    public String getImage() {
        return image;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Notes(String title, String body, int priority, String image, long timestamp) {
        this.title = title;
        this.body = body;
        this.priority = priority;
        this.image = image;
        this.timestamp = timestamp;
    }
}
