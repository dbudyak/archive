package com.thetvdb.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.thetvdb.TheTVApplication;
import com.thetvdb.di.MainComponent;
import com.thetvdb.service.TvDbRestApi;

import javax.inject.Inject;

/**
 * Created by dbudyak on 27.06.16.
 */
public class BaseFragment extends Fragment {

    protected MainComponent injector;
    @Inject protected TvDbRestApi tvDbRestApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector = ((TheTVApplication) getActivity().getApplication()).getInjector();
    }
}
