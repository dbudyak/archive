package com.thetvdb.model;

import java.util.Collections;
import java.util.List;

/**
 * Created by dbudyak on 27.06.16.
 */
public class ActorsData extends Model {
    private List<Actor> data = Collections.emptyList();
    private JSONErrors errors = new JSONErrors();

    public List<Actor> getData() {
        return data;
    }

    public JSONErrors getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "ActorsData{" +
                "data=" + data +
                ", errors=" + errors +
                '}';
    }
}
