package com.thetvdb.model;

/**
 * Created by dbudyak on 27.06.16.
 */
public class EpisodeData extends Model {
    private Episode data;
    private JSONErrors errors;


    public Episode getData() {
        return data;
    }

    public JSONErrors getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "EpisodeData{" +
                "data=" + data +
                ", errors=" + errors +
                '}';
    }
}
