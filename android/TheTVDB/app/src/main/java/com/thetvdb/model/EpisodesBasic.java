package com.thetvdb.model;

/**
 * Created by dbudyak on 27.06.16.
 */
public class EpisodesBasic extends Model {
    private String absoluteNumber = "";
    private String airedEpisodeNumber = "";
    private String airedSeason = "";
    private String dvdEpisodeNumber = "";
    private String dvdSeason = "";
    private String episodeName = "";
    private String id = "";
    private String overview = "";


    public String getAbsoluteNumber() {
        return absoluteNumber;
    }

    public String getAiredEpisodeNumber() {
        return airedEpisodeNumber;
    }

    public String getAiredSeason() {
        return airedSeason;
    }

    public String getDvdEpisodeNumber() {
        return dvdEpisodeNumber;
    }

    public String getDvdSeason() {
        return dvdSeason;
    }

    public String getEpisodeName() {
        return episodeName;
    }

    public String getId() {
        return id;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEpisodeName(String episodeName) {
        this.episodeName = episodeName;
    }

    public void setDvdSeason(String dvdSeason) {
        this.dvdSeason = dvdSeason;
    }

    public void setDvdEpisodeNumber(String dvdEpisodeNumber) {
        this.dvdEpisodeNumber = dvdEpisodeNumber;
    }

    public void setAiredSeason(String airedSeason) {
        this.airedSeason = airedSeason;
    }

    public void setAiredEpisodeNumber(String airedEpisodeNumber) {
        this.airedEpisodeNumber = airedEpisodeNumber;
    }

    public void setAbsoluteNumber(String absoluteNumber) {
        this.absoluteNumber = absoluteNumber;
    }

    @Override
    public String toString() {
        return "EpisodesBasic{" +
                "absoluteNumber=" + absoluteNumber +
                ", airedEpisodeNumber=" + airedEpisodeNumber +
                ", airedSeason=" + airedSeason +
                ", dvdEpisodeNumber=" + dvdEpisodeNumber +
                ", dvdSeason=" + dvdSeason +
                ", episodeName='" + episodeName + '\'' +
                ", id=" + id +
                ", overview='" + overview + '\'' +
                '}';
    }
}
