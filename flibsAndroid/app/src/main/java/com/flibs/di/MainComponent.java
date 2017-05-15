package com.flibs.di;

import com.flibs.ui.MainActivity;
import com.flibs.ui.fragment.BooksFragment;
import com.flibs.ui.fragment.CachedBooksFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by dbudyak on 07.04.16.
 */
@Singleton
@Component(modules={AppModule.class, MainModule.class})
public interface MainComponent {
    void inject(MainActivity activity);
    void inject(BooksFragment booksFragment);
    void inject(CachedBooksFragment cachedBooksFragment);
}