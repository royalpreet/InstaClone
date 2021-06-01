package com.example.instaclone.models;

public class Comment {
    private String comment;
    private String date_created;
    private String user_id;

    public Comment(String comment, String user_id, String date_created) {
        this.comment = comment;
        this.user_id = user_id;
        this.date_created = date_created;
    }

    public Comment() {
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate_created() {
        return this.date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Comment{comment='");
        stringBuilder.append(this.comment);
        stringBuilder.append('\'');
        stringBuilder.append(", user_id='");
        stringBuilder.append(this.user_id);
        stringBuilder.append('\'');
        stringBuilder.append(", date_created='");
        stringBuilder.append(this.date_created);
        stringBuilder.append('\'');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
