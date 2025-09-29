package com.flibs.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.flibs.FlibsApplication;
import com.flibs.di.MainComponent;
import com.flibs.service.FlibsApi;

import javax.inject.Inject;

/**
 * Created by dbudyak on 27.06.16.
 */
public class BaseFragment extends Fragment {

    protected MainComponent injector;
    @Inject protected FlibsApi flibsApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector = ((FlibsApplication) getActivity().getApplication()).getInjector();
    }
}
