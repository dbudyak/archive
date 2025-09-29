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
import com.squareup.picasso.Picasso;
import com.thetvdb.R;
import com.thetvdb.model.Serial;
import com.thetvdb.ui.BaseFragment;
import com.thetvdb.ui.FavSerialInfoActivity;
import com.thetvdb.ui.SerialInfoActivity;
import com.thetvdb.ui.adapter.FavSerialAdapter;
import com.thetvdb.ui.helper.RVAdapterHelper;
import com.thetvdb.ui.helper.RVItemClickSupport;
import com.thetvdb.util.FavoritesManager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dbudyak on 29.06.16.
 */
public class FavSerialsFragment extends BaseFragment {

    @BindView(R.id.cached_serials_rec_view) RecyclerView cachedSerialsView;
    @Inject FavoritesManager favoritesManager;
    @Inject Picasso picasso;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cached_serials, container, false);
        ButterKnife.bind(this, v);
        FavSerialAdapter adapter = new FavSerialAdapter(favoritesManager, picasso);

        cachedSerialsView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        cachedSerialsView.setHasFixedSize(true);
        cachedSerialsView.setItemAnimator(new DefaultItemAnimator());
        cachedSerialsView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        cachedSerialsView.setAdapter(adapter);

        if (favoritesManager.getSerials().isEmpty()) {
            Toast.makeText(this.getActivity(), "You haven't favorited any serials yet", Toast.LENGTH_LONG).show();
        } else {
            ItemTouchHelper.Callback callback = new RVAdapterHelper<>(adapter);
            ItemTouchHelper helper = new ItemTouchHelper(callback);
            helper.attachToRecyclerView(cachedSerialsView);

            RVItemClickSupport.addTo(cachedSerialsView).setOnItemClickListener((recyclerView, position, v1) -> {
                Serial episode = favoritesManager.getSerials().get(position);
                showSeriesInfo(episode);
            });
        }
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cachedSerialsView.getAdapter().notifyDataSetChanged();
    }

    private void showSeriesInfo(Serial serial) {
        Bundle seriesShortInfo = new Bundle();
        seriesShortInfo.putString(SerialInfoActivity.KEY_ID, serial.getId());
        seriesShortInfo.putString(SerialInfoActivity.KEY_NAME, serial.getSeriesName());
        seriesShortInfo.putString(SerialInfoActivity.KEY_BANNER, serial.getBanner());
        seriesShortInfo.putString(SerialInfoActivity.KEY_FIRST_AIRED, serial.getFirstAired());
        seriesShortInfo.putString(SerialInfoActivity.KEY_NETWORK, serial.getNetwork());
        seriesShortInfo.putString(SerialInfoActivity.KEY_OVERVIEW, serial.getOverview());
        seriesShortInfo.putString(SerialInfoActivity.KEY_STATUS, serial.getStatus());

        Intent intent = new Intent(getActivity(), FavSerialInfoActivity.class);
        intent.putExtras(seriesShortInfo);
        startActivityForResult(intent, 0);
    }
}
