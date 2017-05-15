package com.thetvdb.model;

/**
 * Created by dbudyak on 27.06.16.
 */
public class Update extends Model {
    private String id = "";
    private String lastUpdated = "";

    public String getId() {
        return id;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public String toString() {
        return "Update{" +
                "id=" + getId() +
                ", lastUpdated=" + getLastUpdated() +
                '}';
    }
}
