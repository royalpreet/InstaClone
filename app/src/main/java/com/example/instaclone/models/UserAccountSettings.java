package com.example.instaclone.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class UserAccountSettings implements Parcelable {
    public static final Creator<UserAccountSettings> CREATOR = new Creator<UserAccountSettings>() {
        public UserAccountSettings createFromParcel(Parcel in) {
            return new UserAccountSettings(in);
        }

        public UserAccountSettings[] newArray(int size) {
            return new UserAccountSettings[size];
        }
    };
    private String description;
    private String display_name;
    private long followers;
    private long following;
    private long posts;
    private String profile_photo;
    private String user_id;
    private String username;

    public UserAccountSettings(String description, String display_name, long followers, long following, long posts, String profile_photo, String user_id, String username) {
        this.description = description;
        this.display_name = display_name;
        this.followers = followers;
        this.following = following;
        this.posts = posts;
        this.profile_photo = profile_photo;
        this.user_id = user_id;
        this.username = username;
    }

    public UserAccountSettings(){
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplay_name() {
        return this.display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public long getFollowers() {
        return this.followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getFollowing() {
        return this.following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public long getPosts() {
        return this.posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public String getProfile_photo() {
        return this.profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
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
        stringBuilder.append("UserAccountSettings{description='");
        stringBuilder.append(this.description);
        stringBuilder.append('\'');
        stringBuilder.append(", display_name='");
        stringBuilder.append(this.display_name);
        stringBuilder.append('\'');
        stringBuilder.append(", followers=");
        stringBuilder.append(this.followers);
        stringBuilder.append(", following=");
        stringBuilder.append(this.following);
        stringBuilder.append(", posts=");
        stringBuilder.append(this.posts);
        stringBuilder.append(", profile_photo='");
        stringBuilder.append(this.profile_photo);
        stringBuilder.append('\'');
        stringBuilder.append(", user_id='");
        stringBuilder.append(this.user_id);
        stringBuilder.append('\'');
        stringBuilder.append(", username='");
        stringBuilder.append(this.username);
        stringBuilder.append('\'');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public static Creator<UserAccountSettings> getCREATOR() {
        return CREATOR;
    }

    protected UserAccountSettings(Parcel in) {
        this.description = in.readString();
        this.display_name = in.readString();
        this.followers = in.readLong();
        this.following = in.readLong();
        this.posts = in.readLong();
        this.profile_photo = in.readString();
        this.user_id = in.readString();
        this.username = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.description);
        dest.writeString(this.display_name);
        dest.writeLong(this.followers);
        dest.writeLong(this.following);
        dest.writeLong(this.posts);
        dest.writeString(this.profile_photo);
        dest.writeString(this.user_id);
        dest.writeString(this.username);
    }

    public int describeContents() {
        return 0;
    }
}
