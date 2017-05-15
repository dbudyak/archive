package com.thetvdb.model;

import java.util.List;

/**
 * Created by dbudyak on 28.06.16.
 */
public class SearchSeriesData extends Model {
    private List<SerialBasic> data;

    public List<SerialBasic> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "SearchSeriesData{" +
                "data=" + data +
                '}';
    }
}
