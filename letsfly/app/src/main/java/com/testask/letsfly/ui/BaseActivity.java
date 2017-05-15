package com.testask.letsfly.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.testask.letsfly.FlightApplication;
import com.testask.letsfly.util.Loggable;

/**
 * Created by dbudyak on 24.03.17.
 */

public class BaseActivity extends AppCompatActivity implements Loggable {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected FlightApplication.AppComponent component() {
        return ((FlightApplication) getApplication()).getComponent();
    }
}
