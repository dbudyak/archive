package com.thetvdb.model;

import java.util.List;

/**
 * Created by dbudyak on 27.06.16.
 */
public class JSONErrors extends Model {
    private List<String> invalidFilters;
    private String invalidLanguage = "";
    private List<String> invalidQueryParams;

    public List<String> getInvalidFilters() {
        return invalidFilters;
    }

    public String getInvalidLanguage() {
        return invalidLanguage;
    }

    public List<String> getInvalidQueryParams() {
        return invalidQueryParams;
    }

    @Override
    public String toString() {
        return "JSONErrors{" +
                "invalidFilters=" + getInvalidFilters() +
                ", invalidLanguage='" + getInvalidLanguage() + '\'' +
                ", invalidQueryParams=" + getInvalidQueryParams() +
                '}';
    }
}
