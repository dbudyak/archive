package com.thetvdb.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.thetvdb.R;
import com.thetvdb.model.Actor;
import com.thetvdb.model.ActorsData;
import com.thetvdb.ui.BaseFragment;
import com.thetvdb.ui.SerialInfoActivity;
import com.thetvdb.ui.adapter.ActorsAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dbudyak on 29.06.16.
 */
public class ActorsFragment extends BaseFragment {

    @BindView(R.id.actors_rec_view) RecyclerView actorsView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_serial_actors, container, false);
        ButterKnife.bind(this, v);

        Bundle args = getArguments();
        String serialId = args.getString(SerialInfoActivity.KEY_ID);
        tvDbRestApi.actorsInfo(serialId).enqueue(actorsDataCallback);

        actorsView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        actorsView.setHasFixedSize(true);
        actorsView.setItemAnimator(new DefaultItemAnimator());

        actorsView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        return v;
    }

    private Callback<ActorsData> actorsDataCallback = new Callback<ActorsData>() {
        @Override
        public void onResponse(Call<ActorsData> call, Response<ActorsData> response) {
            if (response.isSuccessful()) {
                ActorsData actorsData = response.body();
                if (actorsData != null) {
                    List<Actor> actors = actorsData.getData();

                    if (actors != null && !actors.isEmpty()) {
                        actorsView.setAdapter(new ActorsAdapter(actors));
                        MaterialViewPagerHelper.registerRecyclerView(getActivity(), actorsView);
                    } else {
                        Log.e("DEBUG", "no actors :(");
                    }
                } else {
                    Log.e("DEBUG", "actors data is null");
                }
            }
        }

        @Override
        public void onFailure(Call<ActorsData> call, Throwable t) {
            Log.e("DEBUG", t.getLocalizedMessage());
        }
    };
}
