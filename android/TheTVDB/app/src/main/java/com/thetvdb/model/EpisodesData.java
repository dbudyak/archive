package com.thetvdb.model;

import java.util.Collections;
import java.util.List;

/**
 * Created by dbudyak on 27.06.16.
 */
public class EpisodesData extends Model {
    private EpisodesLinks links;
    private List<EpisodesBasic> data = Collections.emptyList();
    private JSONErrors errors;

    public EpisodesLinks getLinks() {
        return links;
    }

    public List<EpisodesBasic> getData() {
        return data;
    }

    public JSONErrors getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "EpisodesData{" +
                "links=" + links +
                ", data=" + data +
                ", errors=" + errors +
                '}';
    }
}
