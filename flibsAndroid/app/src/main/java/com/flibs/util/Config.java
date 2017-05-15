package com.flibs.util;

import android.content.Context;

import com.github.fernandodev.androidproperties.lib.AssetsProperties;
import com.github.fernandodev.androidproperties.lib.Property;

/**
 * Created by Dmitry Budyak on 05.04.16.
 */
public class Config extends AssetsProperties {

    @Property String apikey;

    public Config(Context context) {
        super(context);
    }

    public String getApiKey() {
        return apikey;
    }

}
