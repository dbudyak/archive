package com.flibs.ui.fragment;

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

import com.flibs.R;
import com.flibs.model.BookModel;
import com.flibs.service.CacheManager;
import com.flibs.ui.BaseFragment;
import com.flibs.ui.adapter.CachedBookAdapter;
import com.flibs.ui.helper.RVAdapterHelper;
import com.flibs.ui.helper.RVItemClickSupport;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dbudyak on 29.06.16.
 */
public class CachedBooksFragment extends BaseFragment {
    @BindView(R.id.cached_serials_rec_view) RecyclerView cachedBooksView;
    @Inject CacheManager cacheManager;
    @Inject Picasso picasso;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cached_books, container, false);
        ButterKnife.bind(this, v);
        CachedBookAdapter adapter = new CachedBookAdapter(cacheManager, picasso);

        cachedBooksView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        cachedBooksView.setHasFixedSize(true);
        cachedBooksView.setItemAnimator(new DefaultItemAnimator());
        cachedBooksView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        cachedBooksView.setAdapter(adapter);

        if (cacheManager.getBookModels().isEmpty()) {
            Toast.makeText(this.getActivity(), "На вашей полке нет книг и хуев", Toast.LENGTH_LONG).show();
        } else {
            ItemTouchHelper.Callback callback = new RVAdapterHelper<>(adapter);
            ItemTouchHelper helper = new ItemTouchHelper(callback);
            helper.attachToRecyclerView(cachedBooksView);

            RVItemClickSupport.addTo(cachedBooksView).setOnItemClickListener(new RVItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    BookModel book = cacheManager.getBookModels().get(position);

                    //TODO open cached for WebView
//                    showSeriesInfo(book);
                }
            });
        }
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cachedBooksView.getAdapter().notifyDataSetChanged();
    }

}
