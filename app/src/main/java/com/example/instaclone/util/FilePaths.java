package com.example.instaclone.util;

import android.os.Environment;

public class FilePaths {
    public String CAMERA;
    public String FIREBASE_IMAGE_STORAGE;
    public String PICTURES;
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();

    public FilePaths() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.ROOT_DIR);
        stringBuilder.append("/Pictures");
        this.PICTURES = stringBuilder.toString();
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.ROOT_DIR);
        stringBuilder.append("/DCIM/Camera");
        this.CAMERA = stringBuilder.toString();
        this.FIREBASE_IMAGE_STORAGE = "photos/users/";
    }
}
