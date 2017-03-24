package com.prepod.bchat.containers;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private String email;
    private String userAvatar;
    private boolean admin;
    private String room;

    public User(){}

    public User(String name, String email, String userAvatar) {
        this.name = name;
        this.email = email;
        this.userAvatar = userAvatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

}
