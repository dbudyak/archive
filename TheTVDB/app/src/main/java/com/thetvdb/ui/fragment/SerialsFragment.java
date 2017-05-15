package com.thetvdb.ui.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.thetvdb.R;
import com.thetvdb.model.SearchSeriesData;
import com.thetvdb.model.SerialBasic;
import com.thetvdb.ui.BaseFragment;
import com.thetvdb.ui.SerialInfoActivity;
import com.thetvdb.ui.adapter.SerialsBasicAdapter;
import com.thetvdb.ui.helper.RVItemClickSupport;
import com.thetvdb.util.Config;
import com.thetvdb.util.SerialDateComparator;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dbudyak on 27.06.16.
 */
public class SerialsFragment extends BaseFragment implements Callback<SearchSeriesData>, SearchView.OnQueryTextListener {

    @Inject Picasso picasso;
    @Inject Config config;

    SearchView searchView = null;
    @BindView(R.id.serials_rec_view) RecyclerView cardsView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        injector.inject(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(this);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // Not implemented here
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(this);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_serial_list, container, false);
        ButterKnife.bind(this, v);
        setupCardsView();
        return v;
    }

    public void setupCardsView() {
        cardsView.setHasFixedSize(true);
        cardsView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        cardsView.setItemAnimator(new DefaultItemAnimator());

        Call<SearchSeriesData> recentlyUpdated = tvDbRestApi.seriesSearch(config.getDefaultSearchPattern());
        recentlyUpdated.enqueue(SerialsFragment.this);
    }

    @Override
    public void onResponse(Call<SearchSeriesData> call, Response<SearchSeriesData> response) {
        if (response.isSuccessful()) {
            SearchSeriesData seriesData = response.body();
            final List<SerialBasic> series = seriesData.getData();
            Collections.sort(series, new SerialDateComparator());
            cardsView.setAdapter(new SerialsBasicAdapter(picasso, series));

            RVItemClickSupport.addTo(cardsView).setOnItemClickListener((recyclerView, position, v) -> {
                SerialBasic serialBasic = series.get(position);
                showSeriesInfo(serialBasic);
            });
        }
    }

    private void showSeriesInfo(SerialBasic serialBasic) {
        Bundle seriesShortInfo = new Bundle();
        seriesShortInfo.putString(SerialInfoActivity.KEY_ID, serialBasic.getId());
        seriesShortInfo.putString(SerialInfoActivity.KEY_NAME, serialBasic.getSeriesName());
        seriesShortInfo.putString(SerialInfoActivity.KEY_BANNER, serialBasic.getBanner());
        seriesShortInfo.putString(SerialInfoActivity.KEY_FIRST_AIRED, serialBasic.getFirstAired());
        seriesShortInfo.putString(SerialInfoActivity.KEY_NETWORK, serialBasic.getNetwork());
        seriesShortInfo.putString(SerialInfoActivity.KEY_OVERVIEW, serialBasic.getOverview());
        seriesShortInfo.putString(SerialInfoActivity.KEY_STATUS, serialBasic.getStatus());

        Intent intent = new Intent(getActivity(), SerialInfoActivity.class);
        intent.putExtras(seriesShortInfo);
        startActivity(intent);
    }

    @Override
    public void onFailure(Call<SearchSeriesData> call, Throwable t) {
        Log.d("DEBUG", t.getMessage());
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText != null && !newText.isEmpty()) {
            Call<SearchSeriesData> recentlyUpdated = tvDbRestApi.seriesSearch(newText);
            recentlyUpdated.enqueue(SerialsFragment.this);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }
}
