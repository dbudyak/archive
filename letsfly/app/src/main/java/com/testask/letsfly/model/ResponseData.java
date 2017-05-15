package com.testask.letsfly.model;

import java.util.Collections;
import java.util.List;

/**
 * Created by dbudyak on 21.03.17.
 */

public class ResponseData {
    private List<City> cities = Collections.emptyList();

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public List<City> getCities() {
        return cities;
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "cities=" + cities +
                '}';
    }
}
