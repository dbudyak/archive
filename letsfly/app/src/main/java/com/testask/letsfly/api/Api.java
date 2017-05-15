package com.testask.letsfly.api;

import com.testask.letsfly.model.ResponseData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by dbudyak on 20.03.17.
 */

public interface Api {
    String BASE_URL = "https://yasen.hotellook.com/";

    @GET("/autocomplete")
    public Call<ResponseData> getData(@Query("term") String term, @Query("lang") String lang);

    @GET("/autocomplete")
    public Call<ResponseData> getData(@Query("term") String term);

}
