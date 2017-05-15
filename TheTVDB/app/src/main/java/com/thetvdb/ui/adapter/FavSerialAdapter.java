package com.thetvdb.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thetvdb.R;
import com.thetvdb.model.Serial;
import com.thetvdb.service.TvDbRestApi;
import com.thetvdb.ui.AbstractMutableAdapter;
import com.thetvdb.util.FavoritesManager;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dbudyak on 27.06.16.
 */
public class FavSerialAdapter extends AbstractMutableAdapter<FavSerialAdapter.ViewHolder> {

    private Picasso picasso;
    private List<Serial> serials;
    private FavoritesManager favoritesManager;

    public FavSerialAdapter(FavoritesManager favoritesManager, Picasso picasso) {
        this.picasso = picasso;
        this.serials = favoritesManager.getSerials();
        this.favoritesManager = favoritesManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_serial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Serial seriesItem = serials.get(position);
        holder.seriesNameTv.setText(seriesItem.getSeriesName());
        if (!seriesItem.getBanner().isEmpty()) {
            String bannerUrl = TvDbRestApi.IMG_URL + seriesItem.getBanner();
            if (ViewHolder.bannerH == 0 || ViewHolder.bannerW == 0) {
                if (holder.seriesBanner.getDrawable() != null) {
                    ViewHolder.bannerW = holder.seriesBanner.getDrawable().getIntrinsicWidth();
                    ViewHolder.bannerH = holder.seriesBanner.getDrawable().getIntrinsicHeight();
                }
            }
            picasso.load(bannerUrl)
                    .resize(ViewHolder.bannerW, ViewHolder.bannerH)
                    .centerInside()
                    .into(holder.seriesBanner);
        }
    }

    @Override
    public int getItemCount() {
        return serials.size();
    }

    public void remove(int position) {
        serials.remove(position);
        favoritesManager.setSerials(serials);
        notifyItemRemoved(position);
    }

    public void swap(int firstPosition, int secondPosition) {
        Collections.swap(serials, firstPosition, secondPosition);
        favoritesManager.setSerials(serials);
        notifyItemMoved(firstPosition, secondPosition);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        static int bannerW, bannerH;

        @BindView(R.id.series_name) TextView seriesNameTv;
        @BindView(R.id.series_banner) ImageView seriesBanner;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}
