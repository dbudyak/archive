package com.flibs.ui;

import android.support.v7.widget.RecyclerView;

/**
 * Created by dbudyak on 30.06.16.
 */
public abstract class BaseMutableAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    public abstract void swap(int fisrtPos, int secondPos);
    public abstract void remove(int position);

}
