package com.example.instaclone.models;

import android.util.Patterns;

public class User {
    private String email;
    private long phone_number;
    private String user_id;
    private String username;

    public User(String email, long phone_number, String user_id, String username) {
        this.email = email;
        this.phone_number = phone_number;
        this.user_id = user_id;
        this.username = username;
    }

    public User(){
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhone_number() {
        return this.phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("User{email='");
        stringBuilder.append(this.email);
        stringBuilder.append('\'');
        stringBuilder.append(", phone_number='");
        stringBuilder.append(this.phone_number);
        stringBuilder.append('\'');
        stringBuilder.append(", user_id=");
        stringBuilder.append(this.user_id);
        stringBuilder.append(", username='");
        stringBuilder.append(this.username);
        stringBuilder.append('\'');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
