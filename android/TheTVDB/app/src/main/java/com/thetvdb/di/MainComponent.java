package com.thetvdb.di;

import com.thetvdb.ui.EpisodeInfoActivity;
import com.thetvdb.ui.FavSerialInfoActivity;
import com.thetvdb.ui.LoginActivity;
import com.thetvdb.ui.MainActivity;
import com.thetvdb.ui.SerialInfoActivity;
import com.thetvdb.ui.fragment.ActorsFragment;
import com.thetvdb.ui.fragment.EpisodesFragment;
import com.thetvdb.ui.fragment.FavEpisodesFragment;
import com.thetvdb.ui.fragment.FavSerialsFragment;
import com.thetvdb.ui.fragment.SerialInfoFragment;
import com.thetvdb.ui.fragment.SerialsFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by dbudyak on 07.04.16.
 */
@Singleton
@Component(modules = {AppModule.class, MainModule.class})
public interface MainComponent {
    void inject(MainActivity activity);

    void inject(LoginActivity activity);

    void inject(SerialsFragment serialsFragment);

    void inject(SerialInfoActivity serialInfoActivity);

    void inject(SerialInfoFragment serialInfoFragment);

    void inject(EpisodesFragment episodesFragment);

    void inject(ActorsFragment actorsFragment);

    void inject(FavEpisodesFragment favEpisodesFragment);

    void inject(FavSerialsFragment favSerialsFragment);

    void inject(EpisodeInfoActivity episodeInfoActivity);

    void inject(FavSerialInfoActivity favSerialInfoActivity);
}