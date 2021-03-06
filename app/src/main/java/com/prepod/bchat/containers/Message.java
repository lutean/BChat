package com.prepod.bchat.containers;

public class Message {

    private String id;
    private String name;
    private String userAvavatar;
    private String text;
    private String imgUrl;
    private long timeStamp;

    public Message(){

    }

    public Message(String name, String userAvavatar, String text) {
        this.name = name;
        this.userAvavatar = userAvavatar;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserAvavatar() {
        return userAvavatar;
    }

    public void setUserAvavatar(String userAvavatar) {
        this.userAvavatar = userAvavatar;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
