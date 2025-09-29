package ru.medbox.utils

import android.util.Log
import ru.medbox.BuildConfig

interface Loggable {

    fun log(klass: Any, message: String?) {
        if (BuildConfig.DEBUG) {
            message.let { Log.d("DEBUG={$klass}", message) }
        }
    }

}