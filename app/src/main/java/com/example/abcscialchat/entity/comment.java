package com.example.abcscialchat.entity;

public class comment {
    public String comment;
    public String time;
    public String uid;

    public comment() {
    }

    public comment(String comment, String time, String uid) {
        this.comment = comment;
        this.time = time;
        this.uid = uid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
