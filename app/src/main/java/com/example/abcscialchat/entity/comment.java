package com.example.abcscialchat.entity;

public class comment {
    public String comment;
    public String time;
    public String uidSender;
    public String commentId;

    public comment(String comment, String time, String uidSender, String commentId) {
        this.comment = comment;
        this.time = time;
        this.uidSender = uidSender;
        this.commentId = commentId;
    }

    public comment() {
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

    public String getUidSender() {
        return uidSender;
    }

    public void setUidSender(String uidSender) {
        this.uidSender = uidSender;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
