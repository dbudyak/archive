package com.thetvdb.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.squareup.picasso.Picasso;
import com.thetvdb.R;
import com.thetvdb.model.Episode;
import com.thetvdb.model.EpisodeData;
import com.thetvdb.ui.adapter.EpisodeInfoAdapter;
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
public class EpisodeInfoActivity extends BaseActivity implements Callback<EpisodeData> {

    public final static String KEY_EPISODE_ID = "KEY_EPISODE_ID";
    public static final String KEY_OVERVIEW = "KEY_OVERVIEW";
    public static final String KEY_DVD_SEASON = "KEY_DVD_SEASON";
    public static final String KEY_DVD_EPISODE_NUMBER = "KEY_DVD_EPISODE_NUMBER";
    public static final String KEY_EPISODE_NAME = "KEY_EPISODE_NAME";
    public static final String KEY_AIRED_SEASON = "KEY_AIRED_SEASON";
    public static final String KEY_AIRED_EPISODES_NUMBER = "KEY_AIRED_EPISODES_NUMBER";

    @BindView(R.id.episode_info_rec_view) RecyclerView infoRecView;
    @BindView(R.id.episode_info_toolbar) Toolbar toolbar;
    @Inject FavoritesManager favoritesManager;
    @Inject Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_info);
        ButterKnife.bind(this);
        injector.inject(this);

        setToolbar();
        setRecycleView();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle extras = getIntent().getExtras();
        String episodeId = extras.getString(KEY_EPISODE_ID);
        tvDbRestApi.episodeInfo(episodeId).enqueue(this);
    }

    private void setToolbar() {
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setHomeButtonEnabled(true);
    }

    private void setRecycleView() {
        infoRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        infoRecView.setHasFixedSize(true);
        infoRecView.setItemAnimator(new DefaultItemAnimator());
        infoRecView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResponse(Call<EpisodeData> call, Response<EpisodeData> response) {
        if (response.isSuccessful()) {
            EpisodeData body = response.body();
            if (body != null) {
                Episode episode = body.getData();
                if (episode != null) {
                    Log.d("DEBUG", episode.toString());
                    infoRecView.setAdapter(new EpisodeInfoAdapter(picasso, favoritesManager, episode));
                } else {
                    Log.e("DEBUG", "episode is null");
                }
            } else {
                Log.e("DEBUG", "episode data is null");
            }
        } else {
            Log.e("DEBUG", "episode info response is bad");
        }
    }

    @Override
    public void onFailure(Call<EpisodeData> call, Throwable t) {
        Log.e("DEBUG", t.getMessage());
    }
}
