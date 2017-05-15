package com.flibs.service;

import com.flibs.model.BookModel;
import com.flibs.util.DefaultPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dbudyak on 29.06.16.
 */
public class CacheManager {

    public static final String SHARED_EPISODES = "SHARED_EPISODES";
    public static final String SHARED_BookModelS = "SHARED_BookModelS";
    private DefaultPreferences prefs;
    private Gson gson;

    private List<BookModel> BookModels;

    public CacheManager(DefaultPreferences prefs, Gson gson) {
        this.prefs = prefs;
        this.gson = gson;

        this.BookModels = new ArrayList<>();
    }

    public boolean isContainBookModel(BookModel BookModel) {
        Iterator<BookModel> iterator = restoreSharedBookModels().iterator();
        while(iterator.hasNext())
            if (iterator.next().BookId.equals(BookModel.BookId)) return true;
        return false;
    }

    public void addBookModel(BookModel BookModel) {
        if (!isContainBookModel(BookModel)) {
            BookModels.add(BookModel);
            persistSharedBookModels();
        }
    }

    public void removeBookModel(BookModel BookModel) {
        BookModels.remove(BookModel);
        persistSharedBookModels();
    }

    public void setBookModels(List<BookModel> BookModels) {
        this.BookModels = BookModels;
        persistSharedBookModels();
    }

    public List<BookModel> getBookModels() {
        return restoreSharedBookModels();
    }


    private void persistSharedBookModels() {
        String BookModelsJSONString = new Gson().toJson(BookModels);
        prefs.self().edit().putString(SHARED_BookModelS, BookModelsJSONString).commit();
    }

    private List<BookModel> restoreSharedBookModels() {
        Type type = new TypeToken<List<BookModel>>() {}.getType();
        List<BookModel> BookModels = new Gson().fromJson(
                prefs.self().getString(SHARED_BookModelS, "[]"), type);
        return BookModels == null ? Collections.<BookModel>emptyList() : BookModels;
    }

}
