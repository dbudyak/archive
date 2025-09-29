package com.thetvdb.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbudyak on 28.06.16.
 */
public class SerialBasic extends Model {
    private List<String> aliases = new ArrayList<>();
    private String banner = "";
    private String firstAired = "";
    private String id = "";
    private String network = "";
    private String overview = "";
    private String seriesName = "";
    private String status = "";

    public List<String> getAliases() {
        return aliases;
    }

    public String getBanner() {
        return banner;
    }

    public String getFirstAired() {
        return firstAired;
    }

    public String getId() {
        return id;
    }

    public String getNetwork() {
        return network;
    }

    public String getOverview() {
        return overview;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "SeriesSearchData{" +
                "aliases=" + aliases +
                ", banner='" + banner + '\'' +
                ", firstAired='" + firstAired + '\'' +
                ", id='" + id + '\'' +
                ", network='" + network + '\'' +
                ", overview='" + overview + '\'' +
                ", seriesName='" + seriesName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
