package com.example.instaclone.util;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class FileSearch {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    public static ArrayList<String> getDirectoryPaths(String directory) {
        ArrayList<String> pathArray = new ArrayList();
        File[] listFiles = new File(directory).listFiles();
        //Log.i("list size: ", listFiles.toString());
        for (int i = 0; i < Objects.requireNonNull(listFiles).length; i++) {
            if (listFiles[i].isDirectory()) {
                pathArray.add(listFiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }

    public static ArrayList<String> getFilePaths(String directory) {
        ArrayList<String> pathArray = new ArrayList();
        File[] listFiles = new File(directory).listFiles();
        Log.i("Directory path: ", directory);
        for (int i = 0; i < Objects.requireNonNull(listFiles).length; i++) {
            if (listFiles[i].isFile()) {
                pathArray.add(listFiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }
}
