package com.flibs.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flibs.R;
import com.flibs.model.BookModel;
import com.flibs.service.CacheManager;
import com.flibs.ui.BaseMutableAdapter;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dbudyak on 27.06.16.
 */
public class CachedBookAdapter extends BaseMutableAdapter<CachedBookAdapter.ViewHolder> {

    private Picasso picasso;
    private List<BookModel> books;
    private CacheManager cacheManager;

    public CachedBookAdapter(CacheManager cacheManager, Picasso picasso) {
        this.picasso = picasso;
        this.books= cacheManager.getBookModels();
        this.cacheManager = cacheManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        BookModel book = books.get(position);
        holder.title.setText(book.Title);
        holder.author.setText(book.FirstName + book.MiddleName + book.LastName);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void remove(int position) {
        books.remove(position);
        cacheManager.setBookModels(books);
        notifyItemRemoved(position);
    }

    public void swap(int firstPosition, int secondPosition) {
        Collections.swap(books, firstPosition, secondPosition);
        cacheManager.setBookModels(books);
        notifyItemMoved(firstPosition, secondPosition);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.book_title) TextView title;
        @BindView(R.id.book_author) TextView author;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}
