package com.flibs.ui.fragment;

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

import com.flibs.R;
import com.flibs.model.BookModel;
import com.flibs.service.CacheManager;
import com.flibs.ui.BaseFragment;
import com.flibs.ui.BookContentAcitivity;
import com.flibs.ui.adapter.BookAdapter;
import com.flibs.ui.helper.RVItemClickSupport;
import com.squareup.picasso.Picasso;

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
public class BooksFragment extends BaseFragment implements Callback<List<BookModel>>, SearchView.OnQueryTextListener {

    @Inject Picasso picasso;
    @Inject CacheManager cacheManager;

    SearchView searchView = null;
    @BindView(R.id.books_rv) RecyclerView cardsView;

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
        View v = inflater.inflate(R.layout.fragment_books, container, false);
        ButterKnife.bind(this, v);
        setupCardsView();
        return v;
    }

    public void setupCardsView() {
        cardsView.setHasFixedSize(true);
        cardsView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        cardsView.setItemAnimator(new DefaultItemAnimator());

        Call<List<BookModel>> recentlyUpdated = flibsApi.bookList(getActivity().getString(R.string.search_default_query));
        recentlyUpdated.enqueue(BooksFragment.this);
    }

    @Override
    public void onResponse(Call<List<BookModel>> call, Response<List<BookModel>> response) {
        if (response.isSuccessful()) {
            final List<BookModel> books = response.body();
            Log.d("DEBUG", books.toString());
            cardsView.setAdapter(new BookAdapter(cacheManager, books));
            RVItemClickSupport.addTo(cardsView).setOnItemClickListener(new RVItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    BookModel book = books.get(position);
                    showWebView(book);
                }
            });
        }
    }

    private void showWebView(BookModel book) {

        Bundle bookExtras = new Bundle();
        bookExtras.putString(BookContentAcitivity.KEY_BOOK_ID, book.BookId);

       Intent intent =  new Intent(this.getActivity(), BookContentAcitivity.class);
        intent.putExtras(bookExtras);
        startActivity(intent);
    }

    @Override
    public void onFailure(Call<List<BookModel>> call, Throwable t) {
        if (t != null) {
            Log.d("DEBUG", t.getMessage());
            t.printStackTrace();
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText != null && !newText.isEmpty()) {
            Call<List<BookModel>> recentlyUpdated = flibsApi.bookList(newText);
            recentlyUpdated.enqueue(BooksFragment.this);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }
}
