package com.flibs;

import android.app.Application;

import com.flibs.di.AppModule;
import com.flibs.di.DaggerMainComponent;
import com.flibs.di.MainComponent;
import com.flibs.di.MainModule;


/**
 * Created by Dmitry Budyak on 04.04.16.
 */
public class FlibsApplication extends Application {

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
