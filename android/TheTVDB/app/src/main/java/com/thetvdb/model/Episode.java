package com.thetvdb.model;

import java.util.Collections;
import java.util.List;

/**
 * Created by dbudyak on 27.06.16.
 */
public class Episode extends Model {
    private String id = "";
    private String airedSeason = "";
    private String airedEpisodeNumber = "";
    private String episodeName = "";
    private String firstAired = "";
    private List<String> guestStars = Collections.emptyList();
    private String director = "";
    private List<String> writers = Collections.emptyList();
    private String overview = "";
    private String productionCode = "";
    private String showUrl = "";
    private String lastUpdated = "";
    private String dvdDiscid = "";
    private String dvdSeason = "";
    private String dvdEpisodeNumber = "";
    private String dvdChapter = "";
    private String absoluteNumber = "";
    private String filename = "";
    private String seriesId = "";
    private String lastUpdatedBy = "";
    private String airsAfterSeason = "";
    private String airsBeforeSeason = "";
    private String airsBeforeEpisode = "";
    private String thumbAuthor = "";
    private String thumbAdded = "";
    private String thumbWidth = "";
    private String thumbHeight = "";
    private String imdbId = "";
    private String siteRating = "";
    private String siteRatingCount = "";

    public String getId() {
        return id;
    }

    public String getAiredSeason() {
        return airedSeason;
    }

    public String getAiredEpisodeNumber() {
        return airedEpisodeNumber;
    }

    public String getEpisodeName() {
        return episodeName;
    }

    public String getFirstAired() {
        return firstAired;
    }

    public List<String> getGuestStars() {
        return guestStars;
    }

    public String getDirector() {
        return director;
    }

    public List<String> getWriters() {
        return writers;
    }

    public String getOverview() {
        return overview;
    }

    public String getProductionCode() {
        return productionCode;
    }

    public String getShowUrl() {
        return showUrl;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public String getDvdDiscid() {
        return dvdDiscid;
    }

    public String getDvdSeason() {
        return dvdSeason;
    }

    public String getDvdEpisodeNumber() {
        return dvdEpisodeNumber;
    }

    public String getDvdChapter() {
        return dvdChapter;
    }

    public String getAbsoluteNumber() {
        return absoluteNumber;
    }

    public String getFilename() {
        return filename;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public String getAirsAfterSeason() {
        return airsAfterSeason;
    }

    public String getAirsBeforeSeason() {
        return airsBeforeSeason;
    }

    public String getAirsBeforeEpisode() {
        return airsBeforeEpisode;
    }

    public String getThumbAuthor() {
        return thumbAuthor;
    }

    public String getThumbAdded() {
        return thumbAdded;
    }

    public String getThumbWidth() {
        return thumbWidth;
    }

    public String getThumbHeight() {
        return thumbHeight;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getSiteRating() {
        return siteRating;
    }

    public String getSiteRatingCount() {
        return siteRatingCount;
    }

    @Override
    public String toString() {
        return "Episode{" +
                "id=" + id +
                ", airedSeason=" + airedSeason +
                ", airedEpisodeNumber=" + airedEpisodeNumber +
                ", episodeName='" + episodeName + '\'' +
                ", firstAired='" + firstAired + '\'' +
                ", guestStars=" + guestStars +
                ", director='" + director + '\'' +
                ", writers=" + writers +
                ", overview='" + overview + '\'' +
                ", productionCode='" + productionCode + '\'' +
                ", showUrl='" + showUrl + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", dvdDiscid='" + dvdDiscid + '\'' +
                ", dvdSeason=" + dvdSeason +
                ", dvdEpisodeNumber=" + dvdEpisodeNumber +
                ", dvdChapter=" + dvdChapter +
                ", absoluteNumber=" + absoluteNumber +
                ", filename='" + filename + '\'' +
                ", seriesId='" + seriesId + '\'' +
                ", lastUpdatedBy='" + lastUpdatedBy + '\'' +
                ", airsAfterSeason=" + airsAfterSeason +
                ", airsBeforeSeason=" + airsBeforeSeason +
                ", airsBeforeEpisode=" + airsBeforeEpisode +
                ", thumbAuthor=" + thumbAuthor +
                ", thumbAdded='" + thumbAdded + '\'' +
                ", thumbWidth='" + thumbWidth + '\'' +
                ", thumbHeight='" + thumbHeight + '\'' +
                ", imdbId='" + imdbId + '\'' +
                ", siteRating=" + siteRating +
                ", siteRatingCount=" + siteRatingCount +
                '}';
    }
}
