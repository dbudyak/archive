package com.thetvdb.model;

import java.util.Collections;
import java.util.List;

/**
 * Created by dbudyak on 27.06.16.
 */
public class Serial extends Model {
    private String id = "";
    private String seriesName = "";
    //    private List<String> aliases = Collections.emptyList();
    private String banner = "";
    private String seriesId = "";
    private String status = "";
    private String firstAired = "";
    private String network = "";
    private String networkId = "";
    private String runtime = "";
    private List<String> genre = Collections.emptyList();
    private String overview = "";
    private String lastUpdated = "";
    private String airsDayOfWeek = "";
    private String airsTime = "";
    private String rating = "";
    private String imdbId = "";
    private String zap2itId = "";
    private String added = "";
    private String siteRating = "";
    private String siteRatingCount = "";


    public String getId() {
        return id;
    }

    public String getSeriesName() {
        return seriesName;
    }

//    public List<String> getAliases() {
//        return aliases;
//    }

    public String getBanner() {
        return banner;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public String getStatus() {
        return status;
    }

    public String getFirstAired() {
        return firstAired;
    }

    public String getNetwork() {
        return network;
    }

    public String getNetworkId() {
        return networkId;
    }

    public String getRuntime() {
        return runtime;
    }

    public List<String> getGenre() {
        return genre;
    }

    public String getOverview() {
        return overview;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public String getAirsDayOfWeek() {
        return airsDayOfWeek;
    }

    public String getAirsTime() {
        return airsTime;
    }

    public String getRating() {
        return rating;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getZap2itId() {
        return zap2itId;
    }

    public String getAdded() {
        return added;
    }

    public String getSiteRating() {
        return siteRating;
    }

    public String getSiteRatingCount() {
        return siteRatingCount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFirstAired(String firstAired) {
        this.firstAired = firstAired;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setAirsDayOfWeek(String airsDayOfWeek) {
        this.airsDayOfWeek = airsDayOfWeek;
    }

    public void setAirsTime(String airsTime) {
        this.airsTime = airsTime;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public void setZap2itId(String zap2itId) {
        this.zap2itId = zap2itId;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public void setSiteRating(String siteRating) {
        this.siteRating = siteRating;
    }

    public void setSiteRatingCount(String siteRatingCount) {
        this.siteRatingCount = siteRatingCount;
    }

    @Override
    public String toString() {
        return "Serial{" +
                "id=" + id +
                ", seriesName='" + seriesName + '\'' +
//                ", aliases=" + aliases +
                ", banner='" + banner + '\'' +
                ", seriesId=" + seriesId +
                ", status='" + status + '\'' +
                ", firstAired='" + firstAired + '\'' +
                ", network='" + network + '\'' +
                ", networkId='" + networkId + '\'' +
                ", runtime='" + runtime + '\'' +
                ", genre=" + genre +
                ", overview='" + overview + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", airsDayOfWeek='" + airsDayOfWeek + '\'' +
                ", airsTime='" + airsTime + '\'' +
                ", rating='" + rating + '\'' +
                ", imdbId='" + imdbId + '\'' +
                ", zap2itId='" + zap2itId + '\'' +
                ", added='" + added + '\'' +
                ", siteRating=" + siteRating +
                ", siteRatingCount=" + siteRatingCount +
                '}';
    }
}
