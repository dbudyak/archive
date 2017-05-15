package com.thetvdb.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.thetvdb.R;
import com.thetvdb.model.Serial;
import com.thetvdb.util.FavoritesManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dbudyak on 29.06.16.
 */
public class SerialInfoAdapter extends RecyclerView.Adapter<SerialInfoAdapter.ViewHolder> {

    private Serial serial;
    private FavoritesManager favoritesManager;

    public SerialInfoAdapter(FavoritesManager favoritesManager, Serial serial) {
        this.favoritesManager = favoritesManager;
        this.serial = serial;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.airs.setText(
                !serial.getAirsDayOfWeek().isEmpty() && !serial.getAirsTime().isEmpty() ?
                        serial.getAirsDayOfWeek() + " at " + serial.getAirsTime() : ""
        );
        holder.firstAired.setText(serial.getFirstAired());
        holder.network.setText(serial.getNetwork());
        holder.runtime.setText(serial.getRuntime());
        holder.genre.setText(serial.getGenre().toString());
        holder.overview.setText(serial.getOverview());
        setFavButton(holder);
    }

    private void setFavButton(final ViewHolder holder) {
        boolean isAddedToFav = favoritesManager.isContainSerial(serial);
        holder.favBtn.setText(isAddedToFav ? R.string.remove_fav : R.string.add_fav);
        holder.favBtn.setOnClickListener(v -> {
            if (favoritesManager.isContainSerial(serial)) {
                favoritesManager.removeSerial(serial);
                holder.favBtn.setText(R.string.add_fav);
            } else {
                favoritesManager.addSerial(serial);
                holder.favBtn.setText(R.string.remove_fav);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ser_info_status) TextView status;
        @BindView(R.id.ser_info_airs) TextView airs;
        @BindView(R.id.ser_info_first_aired) TextView firstAired;
        @BindView(R.id.ser_info_overview) TextView overview;
        @BindView(R.id.ser_info_runtime) TextView runtime;
        @BindView(R.id.ser_info_network) TextView network;
        @BindView(R.id.ser_info_genre) TextView genre;
        @BindView(R.id.ser_info_datatable) TableLayout table;
        @BindView(R.id.ser_info_addfav) Button favBtn;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
