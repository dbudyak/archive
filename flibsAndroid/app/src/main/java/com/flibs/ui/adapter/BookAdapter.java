package com.flibs.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.flibs.R;
import com.flibs.model.BookModel;
import com.flibs.service.CacheManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dbudyak on 29.06.16.
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private List<BookModel> books;
    private CacheManager cacheManager;

    public BookAdapter(CacheManager cacheManager, List<BookModel> books) {
        this.cacheManager = cacheManager;
        this.books = books;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        BookModel book = books.get(position);
        holder.author.setText(book.FirstName + book.MiddleName + book.LastName);
        holder.title.setText(book.Title);
        setFavButton(holder, position);
    }

    private void setFavButton(final ViewHolder holder, final int position) {
        boolean isAddedToFav = cacheManager.isContainBookModel(books.get(position));
        holder.favBtn.setImageResource(isAddedToFav ? android.R.drawable.star_big_on : android.R.drawable.star_big_off);
        holder.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cacheManager.isContainBookModel(books.get(position))) {
                    cacheManager.removeBookModel(books.get(position));
                    holder.favBtn.setImageResource(android.R.drawable.star_big_off);
                } else {
                    cacheManager.addBookModel(books.get(position));
                    holder.favBtn.setImageResource(android.R.drawable.star_big_on);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.book_author) TextView author;
        @BindView(R.id.book_title) TextView title;
        @BindView(R.id.book_fav) ImageButton favBtn;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
