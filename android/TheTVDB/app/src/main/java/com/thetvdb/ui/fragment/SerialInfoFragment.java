package com.thetvdb.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.thetvdb.R;
import com.thetvdb.model.Serial;
import com.thetvdb.model.SerialData;
import com.thetvdb.ui.BaseFragment;
import com.thetvdb.ui.SerialInfoActivity;
import com.thetvdb.ui.adapter.SerialInfoAdapter;
import com.thetvdb.util.FavoritesManager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dbudyak on 29.06.16.
 */
public class SerialInfoFragment extends BaseFragment {

    @BindView(R.id.serial_info_rec_view) RecyclerView infoRecView;
    @Inject FavoritesManager favoritesManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_serial_info, container, false);
        ButterKnife.bind(this, v);

        infoRecView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        infoRecView.setHasFixedSize(true);
        infoRecView.setItemAnimator(new DefaultItemAnimator());

        infoRecView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        setBasicEnv();
        return v;
    }

    private void setBasicEnv() {
        Bundle extras = getArguments();

        String serFirstAired = extras.getString(SerialInfoActivity.KEY_FIRST_AIRED);
        String serId = extras.getString(SerialInfoActivity.KEY_ID);
        String serNetwork = extras.getString(SerialInfoActivity.KEY_NETWORK);
        String serOverview = extras.getString(SerialInfoActivity.KEY_OVERVIEW);
        String serStatus = extras.getString(SerialInfoActivity.KEY_STATUS);

        Serial serial = new Serial();
        serial.setFirstAired(serFirstAired);
        serial.setId(serId);
        serial.setNetwork(serNetwork);
        serial.setOverview(serOverview);
        serial.setStatus(serStatus);

        infoRecView.setAdapter(new SerialInfoAdapter(favoritesManager, serial));
        tvDbRestApi.seriesInfo(serId).enqueue(seriesDataCallback);
    }

    private Callback<SerialData> seriesDataCallback = new Callback<SerialData>() {
        @Override
        public void onResponse(Call<SerialData> call, Response<SerialData> response) {
            if (response.isSuccessful()) {
                SerialData serialData = response.body();
                Serial serial = serialData.getData();
                if (serialData != null && serial != null) {
                    infoRecView.setAdapter(new SerialInfoAdapter(favoritesManager, serial));
                } else {
                    Log.e("DEBUG", "serial is unavailable");
                }
            } else {
                Log.e("DEBUG", "bad response: " + response.code());
            }
        }

        @Override
        public void onFailure(Call<SerialData> call, Throwable t) {
            Log.e("DEBUG", t.getLocalizedMessage());
        }
    };
}
