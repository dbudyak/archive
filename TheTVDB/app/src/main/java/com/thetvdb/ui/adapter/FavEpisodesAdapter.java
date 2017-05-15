package com.thetvdb.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thetvdb.R;
import com.thetvdb.model.Episode;
import com.thetvdb.ui.AbstractMutableAdapter;
import com.thetvdb.util.FavoritesManager;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dbudyak on 29.06.16.
 */
public class FavEpisodesAdapter extends AbstractMutableAdapter<FavEpisodesAdapter.ViewHolder> {

    private List<Episode> episodes;
    private FavoritesManager favoritesManager;

    public FavEpisodesAdapter(FavoritesManager favoritesManager) {
        this.episodes = favoritesManager.getEpisodes();
        this.favoritesManager = favoritesManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_episode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Episode episode = episodes.get(position);
        holder.epName.setText(episode.getEpisodeName());
        holder.epNum.setText("Episode #" + episode.getDvdEpisodeNumber());
        holder.epSeasNum.setText("Season #" + episode.getDvdSeason());
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public void remove(int position) {
        episodes.remove(position);
        favoritesManager.setEpisodes(episodes);
        notifyItemRemoved(position);
    }

    public void swap(int firstPosition, int secondPosition) {
        Collections.swap(episodes, firstPosition, secondPosition);
        favoritesManager.setEpisodes(episodes);
        notifyItemMoved(firstPosition, secondPosition);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.episode_num) TextView epNum;
        @BindView(R.id.episode_season_num) TextView epSeasNum;
        @BindView(R.id.episode_name) TextView epName;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
