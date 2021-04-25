package com.example.skinspe.models;

public class Skin {
    private String skin;
    private int key_id;
    private int favStatus;

    public Skin(String skin, int key_id, int favStatus) {
        this.skin = skin;
        this.key_id = key_id;
        this.favStatus = favStatus;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public int getKey_id() {
        return key_id;
    }

    public void setKey_id(int key_id) {
        this.key_id = key_id;
    }

    public int getFavStatus() {
        return favStatus;
    }

    public void setFavStatus(int favStatus) {
        this.favStatus = favStatus;
    }
}
