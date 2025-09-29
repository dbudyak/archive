package com.thetvdb.model;

import java.util.Collections;
import java.util.List;

/**
 * Created by dbudyak on 27.06.16.
 */
public class UpdateData extends Model {
    private List<Update> data = Collections.emptyList();
    private JSONErrors errors;

    public List<Update> getData() {
        return data;
    }

    public JSONErrors getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "UpdateData{" +
                "data=" + getData() +
                ", errors=" + getErrors() +
                '}';
    }
}
