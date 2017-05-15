package com.thetvdb;

import android.app.Application;

import com.thetvdb.di.AppModule;
import com.thetvdb.di.DaggerMainComponent;
import com.thetvdb.di.MainComponent;
import com.thetvdb.di.MainModule;


/**
 * Created by Dmitry Budyak on 04.04.16.
 */
public class TheTVApplication extends Application {

    private MainComponent injector;

    @Override
    public void onCreate() {
        super.onCreate();
        injector = DaggerMainComponent.builder()
                .appModule(new AppModule(this))
                .mainModule(new MainModule()).build();
    }

    public MainComponent getInjector() {
        return injector;
    }

}
