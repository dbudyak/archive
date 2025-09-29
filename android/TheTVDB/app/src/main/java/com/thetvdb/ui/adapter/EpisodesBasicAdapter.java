package com.thetvdb.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thetvdb.R;
import com.thetvdb.model.EpisodesBasic;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dbudyak on 29.06.16.
 */
public class EpisodesBasicAdapter extends RecyclerView.Adapter<EpisodesBasicAdapter.ViewHolder> {

    private List<EpisodesBasic> episodes;

    public EpisodesBasicAdapter(List<EpisodesBasic> episodesBasic) {
        episodes = episodesBasic;
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
        EpisodesBasic episode = episodes.get(position);
        holder.epName.setText(episode.getEpisodeName());
        holder.epNum.setText("Episode №" + episode.getAiredEpisodeNumber());
        holder.epSeasNum.setText("Season №" + episode.getAiredSeason());
    }


    @Override
    public int getItemCount() {
        return episodes.size();
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
