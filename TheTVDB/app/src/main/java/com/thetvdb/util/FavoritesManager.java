package com.thetvdb.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thetvdb.model.Episode;
import com.thetvdb.model.Serial;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbudyak on 29.06.16.
 *
 * Just stupid replacement for persisting in sqlite. Not production-ready :)
 */
public class FavoritesManager {

    public static final String SHARED_EPISODES = "SHARED_EPISODES";
    public static final String SHARED_SERIALS = "SHARED_SERIALS";
    private DefaultPreferences prefs;
    private Gson gson;

    private List<Serial> serials;
    private List<Episode> episodes;
    private static final String DEFAULT_JSON_STRING = "[]";

    public FavoritesManager(DefaultPreferences prefs, Gson gson) {
        this.prefs = prefs;
        this.gson = gson;

        this.serials = new ArrayList<>();
        this.episodes = new ArrayList<>();
    }

    public boolean isContainSerial(Serial serial) {
        return restoreSharedSerials().stream().anyMatch(ser -> ser.getId().equals(serial.getId()));
    }

    public boolean isContainEpisode(Episode episode) {
        return restoreSharedEpisodes().stream().anyMatch(ep -> ep.getId().equals(episode.getId()));
    }

    public void addSerial(Serial serial) {
        if (!isContainSerial(serial)) {
            serials.add(serial);
            persistSharedSerials();
        }
    }

    public void addEpisode(Episode episode) {
        if (!isContainEpisode(episode)) {
            episodes.add(episode);
            persistSharedEpisodes();
        }
    }

    public void removeSerial(Serial serial) {
        serials.remove(serial);
        persistSharedSerials();
    }

    public void removeEpisode(Episode episode) {
        episodes.remove(episode);
        persistSharedEpisodes();
    }

    public void setSerials(List<Serial> serials) {
        this.serials = serials;
        persistSharedSerials();
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
        persistSharedEpisodes();
    }

    public List<Serial> getSerials() {
        return restoreSharedSerials();
    }

    public List<Episode> getEpisodes() {
        return restoreSharedEpisodes();
    }

    private void persistSharedSerials() {
        String serialsJSONString = gson.toJson(serials);
        prefs.self().edit().putString(SHARED_SERIALS, serialsJSONString).apply();
    }

    private void persistSharedEpisodes() {
        String episodesJSONString = gson.toJson(episodes);
        prefs.self().edit().putString(SHARED_EPISODES, episodesJSONString).apply();
    }

    private List<Serial> restoreSharedSerials() {
        Type type = new TypeToken<List<Serial>>() {
        }.getType();
        return gson.fromJson(prefs.self().getString(SHARED_SERIALS, DEFAULT_JSON_STRING), type);
    }

    private List<Episode> restoreSharedEpisodes() {
        Type type = new TypeToken<List<Episode>>() {
        }.getType();
        return gson.fromJson(prefs.self().getString(SHARED_EPISODES, DEFAULT_JSON_STRING), type);
    }

}
