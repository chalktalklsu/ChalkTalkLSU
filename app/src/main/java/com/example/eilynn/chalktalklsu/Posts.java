package com.example.eilynn.chalktalklsu;

public class Posts {
    private String username, uid, imageURL, desc;

    public Posts(){

    }

    public Posts(String username, String uid, String imageURL, String desc) {
        this.username = username;
        this.uid = uid;
        this.imageURL = imageURL;
        this.desc = desc;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
