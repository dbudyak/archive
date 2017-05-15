package com.thetvdb.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.thetvdb.TheTVApplication;
import com.thetvdb.di.MainComponent;
import com.thetvdb.service.TvDbRestApi;
import com.thetvdb.util.DefaultPreferences;

import javax.inject.Inject;

/**
 * Created by Dmitry Budyak on 24.06.16.
 */
public class BaseActivity extends AppCompatActivity {
    protected MainComponent injector;

    @Inject TvDbRestApi tvDbRestApi;
    @Inject DefaultPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector = ((TheTVApplication) getApplication()).getInjector();
    }
}
