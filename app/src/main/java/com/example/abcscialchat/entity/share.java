package com.example.abcscialchat.entity;

public class share {
    public String time;
    public String uid;
    public String content;

    public share() {
    }

    public share(String time, String uid, String content) {
        this.time = time;
        this.uid = uid;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
