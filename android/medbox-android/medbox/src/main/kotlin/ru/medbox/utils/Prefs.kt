package ru.medbox.utils

import android.content.SharedPreferences

class Prefs(private val preferences: SharedPreferences) {

    var token: String
        get() = preferences.getString(TOKEN, EMPTY)
        set(userIdToken) = preferences.edit().putString(TOKEN, "$userIdToken").apply()

    var userId: String
        get() = preferences.getString(USER_ID, EMPTY)
        set(userId) = preferences.edit().putString(USER_ID, userId).apply()

    var userName: String
        get() = preferences.getString(USER_NAME, EMPTY)
        set(userName) = preferences.edit().putString(USER_NAME, userName).apply()

    var userEmail: String
        get() = preferences.getString(USER_EMAIL, EMPTY)
        set(userEmail) = preferences.edit().putString(USER_EMAIL, userEmail).apply()

    val userPhoto: String
        get() = preferences.getString(USER_PHOTO, EMPTY)

    fun self(): SharedPreferences {
        return preferences
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    fun setPhotoUri(photoUri: String) {
        preferences.edit().putString(USER_PHOTO, photoUri).apply()
    }

    companion object {
        private const val TOKEN = "token"
        private const val USER_ID = "user_id"
        private const val USER_NAME = "user_name"
        private const val USER_EMAIL = "user_email"
        private const val USER_PHOTO = "user_photo"
        const val EMPTY = ""
    }
}