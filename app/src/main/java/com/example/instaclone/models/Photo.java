package com.example.instaclone.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;

public class Photo implements Parcelable {
    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
    private String caption;
    private ArrayList<Comment> comments;
    private String date_created;
    private String image_path;
    private ArrayList<String> likes;
    private String photo_id;
    private String user_id;

    public Photo(String caption, String date_created, String image_path, String photo_id, String user_id, ArrayList<String> likes, ArrayList<Comment> comments) {
        this.caption = caption;
        this.date_created = date_created;
        this.image_path = image_path;
        this.photo_id = photo_id;
        this.user_id = user_id;
        this.likes = likes;
        this.comments = comments;
    }

    protected Photo(Parcel in) {
        this.caption = in.readString();
        this.date_created = in.readString();
        this.image_path = in.readString();
        this.photo_id = in.readString();
        this.user_id = in.readString();
        this.likes = in.createStringArrayList();
    }

    public Photo() {

    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDate_created() {
        return this.date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getImage_path() {
        return this.image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getPhoto_id() {
        return this.photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public ArrayList<String> getLikes() {
        return this.likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public ArrayList<Comment> getComments() {
        return this.comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Photo{caption='");
        stringBuilder.append(this.caption);
        stringBuilder.append('\'');
        stringBuilder.append(", date_created='");
        stringBuilder.append(this.date_created);
        stringBuilder.append('\'');
        stringBuilder.append(", image_path='");
        stringBuilder.append(this.image_path);
        stringBuilder.append('\'');
        stringBuilder.append(", photo_id='");
        stringBuilder.append(this.photo_id);
        stringBuilder.append('\'');
        stringBuilder.append(", user_id='");
        stringBuilder.append(this.user_id);
        stringBuilder.append('\'');
        stringBuilder.append(", likes=");
        stringBuilder.append(this.likes);
        stringBuilder.append(", comments=");
        stringBuilder.append(this.comments);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.caption);
        dest.writeString(this.date_created);
        dest.writeString(this.image_path);
        dest.writeString(this.photo_id);
        dest.writeString(this.user_id);
        dest.writeStringList(this.likes);
    }
}
