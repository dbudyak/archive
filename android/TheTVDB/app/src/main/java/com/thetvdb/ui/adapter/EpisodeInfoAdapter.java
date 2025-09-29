package com.thetvdb.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thetvdb.R;
import com.thetvdb.model.Episode;
import com.thetvdb.service.TvDbRestApi;
import com.thetvdb.util.FavoritesManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dbudyak on 29.06.16.
 */
public class EpisodeInfoAdapter extends RecyclerView.Adapter<EpisodeInfoAdapter.ViewHolder> {

    private Episode episode;
    private FavoritesManager favoritesManager;
    private Picasso picasso;

    public EpisodeInfoAdapter(Picasso picasso, FavoritesManager favoritesManager, Episode episode) {
        this.favoritesManager = favoritesManager;
        this.episode = episode;
        this.picasso = picasso;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_episode_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        setHeaderImage(holder);
        holder.name.setText(episode.getEpisodeName());
        holder.firstAired.setText(episode.getFirstAired());
        holder.overview.setText(episode.getOverview());
        setFavButton(holder);
    }

    private void setFavButton(final ViewHolder holder) {
        boolean isAddedToFav = favoritesManager.isContainEpisode(episode);
        holder.favBtn.setText(isAddedToFav ? R.string.remove_fav : R.string.add_fav);
        holder.favBtn.setOnClickListener(v -> {
            if (favoritesManager.isContainEpisode(episode)) {
                favoritesManager.removeEpisode(episode);
                holder.favBtn.setText(R.string.add_fav);
            } else {
                favoritesManager.addEpisode(episode);
                holder.favBtn.setText(R.string.remove_fav);
            }
        });
    }

    private void setHeaderImage(ViewHolder holder) {
        if (ViewHolder.bannerH == 0 || ViewHolder.bannerW == 0) {
            if (holder.img.getDrawable() != null) {
                ViewHolder.bannerW = holder.img.getDrawable().getIntrinsicWidth();
                ViewHolder.bannerH = holder.img.getDrawable().getIntrinsicHeight() * 2;
            }
        }
        picasso.load(TvDbRestApi.IMG_URL + episode.getFilename())
                .resize(ViewHolder.bannerW, ViewHolder.bannerH)
                .centerInside()
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        static int bannerW, bannerH;

        @BindView(R.id.ep_info_name) TextView name;
        @BindView(R.id.ep_info_first_aired) TextView firstAired;
        @BindView(R.id.ep_info_overview) TextView overview;
        @BindView(R.id.ep_info_datatable) TableLayout table;
        @BindView(R.id.ep_info_addfav) Button favBtn;
        @BindView(R.id.ep_info_img) ImageView img;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
