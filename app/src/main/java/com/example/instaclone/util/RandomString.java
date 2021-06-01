package com.example.instaclone.util;

public class RandomString {
    public static String getAlphaNumericString() {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(25);
        for (int i = 0; i < 25; i++) {
            sb.append(AlphaNumericString.charAt((int) (((double) AlphaNumericString.length()) * Math.random())));
        }
        return sb.toString();
    }
}
