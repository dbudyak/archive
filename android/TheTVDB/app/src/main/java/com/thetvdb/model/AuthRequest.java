package com.thetvdb.model;

/**
 * Created by dbudyak on 27.06.16.
 */
public class AuthRequest extends Model {
    private final String apikey;

    public AuthRequest(String apikey) {
        this.apikey = apikey;
    }
}
