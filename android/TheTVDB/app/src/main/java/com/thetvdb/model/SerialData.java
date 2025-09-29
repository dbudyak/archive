package com.thetvdb.model;

/**
 * Created by dbudyak on 27.06.16.
 */
public class SerialData extends Model {
    private Serial data;
    private JSONErrors errors;


    public Serial getData() {
        return data;
    }

    public JSONErrors getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "SerialData{" +
                "data=" + data +
                ", errors=" + errors +
                '}';
    }
}
