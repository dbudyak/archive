package com.testask.letsfly.util;

import android.util.Log;

import java.util.Objects;

/**
 * Created by dbudyak on 24.03.17.
 */

public interface Loggable {
    String DEBUG = "DEBUG";

    default void log(Object o) {
        Log.d(DEBUG, Objects.toString(o));
    }

    default void log(String msg) {
        Log.d(DEBUG, msg);
    }

    default void log(Throwable t) {
        Log.d(DEBUG, t.getLocalizedMessage());
    }
}
