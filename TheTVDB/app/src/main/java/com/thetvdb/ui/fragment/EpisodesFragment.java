package com.thetvdb.ui.fragment;

import android.content.Intent;
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
import com.thetvdb.model.EpisodesBasic;
import com.thetvdb.model.EpisodesData;
import com.thetvdb.ui.BaseFragment;
import com.thetvdb.ui.EpisodeInfoActivity;
import com.thetvdb.ui.SerialInfoActivity;
import com.thetvdb.ui.adapter.EpisodesBasicAdapter;
import com.thetvdb.ui.helper.RVItemClickSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dbudyak on 29.06.16.
 */
public class EpisodesFragment extends BaseFragment implements Callback<EpisodesData> {

    @BindView(R.id.episodes_rec_view) RecyclerView episodesView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_serial_episodes, container, false);
        ButterKnife.bind(this, v);

        Bundle args = getArguments();
        String serialId = args.getString(SerialInfoActivity.KEY_ID);
        tvDbRestApi.episodesInfo(serialId).enqueue(this);

        episodesView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        episodesView.setHasFixedSize(true);
        episodesView.setItemAnimator(new DefaultItemAnimator());

        episodesView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        return v;
    }

    @Override
    public void onResponse(Call<EpisodesData> call, Response<EpisodesData> response) {
        if (response.isSuccessful()) {
            EpisodesData episodesData = response.body();
            if (episodesData != null) {
                final List<EpisodesBasic> episodesBasic = episodesData.getData();

                if (episodesBasic != null && !episodesBasic.isEmpty()) {
                    episodesView.setAdapter(new EpisodesBasicAdapter(episodesBasic));
                    MaterialViewPagerHelper.registerRecyclerView(getActivity(), episodesView);
                    RVItemClickSupport.addTo(episodesView).setOnItemClickListener((recyclerView, position, v) -> {
                        EpisodesBasic basicEpisode = episodesBasic.get(position);
                        showEpisodesInfo(basicEpisode);
                    });
                } else {
                    Log.e("DEBUG", "no episodes");
                }
            } else {
                Log.e("DEBUG", "episodes data is null");
            }
        } else {
            Log.e("DEBUG", "episodes response is bad");
        }
    }

    private void showEpisodesInfo(EpisodesBasic basicEpisode) {
        Intent intent = new Intent(getActivity(), EpisodeInfoActivity.class);
        Bundle extras = new Bundle();
        extras.putString(EpisodeInfoActivity.KEY_EPISODE_ID, basicEpisode.getId());
        extras.putString(EpisodeInfoActivity.KEY_OVERVIEW, basicEpisode.getOverview());
        extras.putString(EpisodeInfoActivity.KEY_DVD_SEASON, basicEpisode.getDvdSeason());
        extras.putString(EpisodeInfoActivity.KEY_DVD_EPISODE_NUMBER, basicEpisode.getDvdEpisodeNumber());
        extras.putString(EpisodeInfoActivity.KEY_EPISODE_NAME, basicEpisode.getEpisodeName());
        extras.putString(EpisodeInfoActivity.KEY_AIRED_SEASON, basicEpisode.getAiredSeason());
        extras.putString(EpisodeInfoActivity.KEY_AIRED_EPISODES_NUMBER, basicEpisode.getAiredEpisodeNumber());
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    public void onFailure(Call<EpisodesData> call, Throwable t) {
        Log.e("DEBUG", t.getMessage());
    }
}
