package com.thetvdb.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thetvdb.R;
import com.thetvdb.model.Actor;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dbudyak on 29.06.16.
 */
public class ActorsAdapter extends RecyclerView.Adapter<ActorsAdapter.ViewHolder> {

    private List<Actor> actors;

    public ActorsAdapter(List<Actor> actors) {
        this.actors = actors;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_actor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Actor actor = actors.get(position);
        holder.actorName.setText(actor.getName());
        Log.d("DEBUG", actor.getImage());
    }

    @Override
    public int getItemCount() {
        return actors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.actor_img) ImageView actorImg;
        @BindView(R.id.actor_name) TextView actorName;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
