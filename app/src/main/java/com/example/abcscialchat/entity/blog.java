package com.example.abcscialchat.entity;

import java.util.ArrayList;

public class blog {
    String id;
    String uid;
    String content;
    String image;
    ArrayList<comment> comments;
    ArrayList<share> shares;
    int like;
    long timeCreate;
    long timeUpdate;
    public blog() {
    }

    public blog(String uid, String content, String image, ArrayList<comment> comments, ArrayList<share> shares, int like, long timeCreate, long timeUpdate) {
        this.uid = uid;
        this.content = content;
        this.image = image;
        this.comments = comments;
        this.shares = shares;
        this.like = like;
        this.timeCreate = timeCreate;
        this.timeUpdate = timeUpdate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<comment> comments) {
        this.comments = comments;
    }

    public ArrayList<share> getShares() {
        return shares;
    }

    public void setShares(ArrayList<share> shares) {
        this.shares = shares;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public long getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(long timeCreate) {
        this.timeCreate = timeCreate;
    }

    public long getTimeUpdate() {
        return timeUpdate;
    }

    public void setTimeUpdate(long timeUpdate) {
        this.timeUpdate = timeUpdate;
    }
}
