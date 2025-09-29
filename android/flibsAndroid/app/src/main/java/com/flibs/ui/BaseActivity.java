package com.flibs.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.flibs.FlibsApplication;
import com.flibs.di.MainComponent;
import com.flibs.service.FlibsApi;
import com.flibs.util.DefaultPreferences;

import javax.inject.Inject;

/**
 * Created by Dmitry Budyak on 24.06.16.
 */
public class BaseActivity extends AppCompatActivity {
    protected MainComponent injector;

    @Inject
    FlibsApi flibsApi;
    @Inject DefaultPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector = ((FlibsApplication) getApplication()).getInjector();
    }
}
