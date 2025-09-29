package com.flibs.util;

import android.content.SharedPreferences;

/**
 * Created by dbudyak on 06.04.16.
 */
public class DefaultPreferences {

    private final static String JWT_TOKEN = "jwt_token";
    private final static String USER_ID = "user_id";
    private final static String USER_NAME = "user_name";
    private final static String USER_EMAIL = "user_email";
    private final static String USER_PHOTO = "user_photo";
    public static final String EMPTY = "";
    private SharedPreferences preferences;

    public DefaultPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public String getTokenHeader() {
        return preferences.getString(JWT_TOKEN, EMPTY);
    }

    public void setTokenHeader(String userIdToken) {
        preferences.edit().putString(JWT_TOKEN, "Bearer " + userIdToken).apply();
    }

    public SharedPreferences self() {
        return preferences;
    }

    public void clear() {
        preferences.edit().clear().apply();
    }

    public void setUserId(String userId) {
        preferences.edit().putString(USER_ID, userId).apply();
    }

    public String getUserId() {
        return preferences.getString(USER_ID, EMPTY);
    }

    public void setUserName(String userName) {
        preferences.edit().putString(USER_NAME, userName).apply();
    }

    public String getUserName() {
        return preferences.getString(USER_NAME, EMPTY);
    }

    public void setUserEmail(String userEmail) {
        preferences.edit().putString(USER_EMAIL, userEmail).apply();
    }

    public String getUserEmail() {
        return preferences.getString(USER_EMAIL, EMPTY);
    }

    public void setPhotoUri(String photoUri) {
        preferences.edit().putString(USER_PHOTO, photoUri).apply();
    }

    public String getUserPhoto() {
        return preferences.getString(USER_PHOTO, EMPTY);
    }
}
