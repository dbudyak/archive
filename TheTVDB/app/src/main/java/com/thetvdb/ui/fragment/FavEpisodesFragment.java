package com.thetvdb.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.thetvdb.R;
import com.thetvdb.model.Episode;
import com.thetvdb.ui.BaseFragment;
import com.thetvdb.ui.EpisodeInfoActivity;
import com.thetvdb.ui.adapter.FavEpisodesAdapter;
import com.thetvdb.ui.helper.RVAdapterHelper;
import com.thetvdb.ui.helper.RVItemClickSupport;
import com.thetvdb.util.FavoritesManager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dbudyak on 29.06.16.
 */
public class FavEpisodesFragment extends BaseFragment {
    @BindView(R.id.cached_episodes_rec_view) RecyclerView cachedEpisodesView;
    @Inject FavoritesManager favoritesManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cached_episodes, container, false);
        ButterKnife.bind(this, v);

        FavEpisodesAdapter adapter = new FavEpisodesAdapter(favoritesManager);
        cachedEpisodesView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        cachedEpisodesView.setHasFixedSize(true);
        cachedEpisodesView.setItemAnimator(new DefaultItemAnimator());
        cachedEpisodesView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        cachedEpisodesView.setAdapter(adapter);

        if (favoritesManager.getEpisodes().isEmpty()) {
            Toast.makeText(this.getActivity(), "You haven't favorited any episodes yet", Toast.LENGTH_LONG).show();
        } else {
            ItemTouchHelper.Callback callback = new RVAdapterHelper<>(adapter);
            ItemTouchHelper helper = new ItemTouchHelper(callback);
            helper.attachToRecyclerView(cachedEpisodesView);

            RVItemClickSupport.addTo(cachedEpisodesView).setOnItemClickListener((recyclerView, position, v1) -> {
                Episode episode = favoritesManager.getEpisodes().get(position);
                showEpisodesInfo(episode);
            });
        }
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cachedEpisodesView.getAdapter().notifyDataSetChanged();
    }

    private void showEpisodesInfo(Episode episode) {
        Intent intent = new Intent(getActivity(), EpisodeInfoActivity.class);
        Bundle extras = new Bundle();
        extras.putString(EpisodeInfoActivity.KEY_EPISODE_ID, episode.getId());
        extras.putString(EpisodeInfoActivity.KEY_OVERVIEW, episode.getOverview());
        extras.putString(EpisodeInfoActivity.KEY_DVD_SEASON, episode.getDvdSeason());
        extras.putString(EpisodeInfoActivity.KEY_DVD_EPISODE_NUMBER, episode.getDvdEpisodeNumber());
        extras.putString(EpisodeInfoActivity.KEY_EPISODE_NAME, episode.getEpisodeName());
        extras.putString(EpisodeInfoActivity.KEY_AIRED_SEASON, episode.getAiredSeason());
        extras.putString(EpisodeInfoActivity.KEY_AIRED_EPISODES_NUMBER, episode.getAiredEpisodeNumber());
        intent.putExtras(extras);
        startActivityForResult(intent, 0);
    }
}
