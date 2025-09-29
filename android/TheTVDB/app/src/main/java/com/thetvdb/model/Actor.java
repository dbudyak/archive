package com.thetvdb.model;

/**
 * Created by dbudyak on 27.06.16.
 */
public class Actor extends Model {
    private String id = "";
    private String seriesId = "";
    private String name = "";
    private String role = "";
    private String sortOrder = "";
    private String image = "";
    private String imageAuthor = "";
    private String imageAdded = "";
    private String lastUpdated = "";

    public String getId() {
        return id;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public String getImage() {
        return image;
    }

    public String getImageAuthor() {
        return imageAuthor;
    }

    public String getImageAdded() {
        return imageAdded;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", seriesId=" + seriesId +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", sortOrder=" + sortOrder +
                ", image='" + image + '\'' +
                ", imageAuthor=" + imageAuthor +
                ", imageAdded='" + imageAdded + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                '}';
    }
}
