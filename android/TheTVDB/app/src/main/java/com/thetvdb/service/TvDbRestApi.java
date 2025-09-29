package com.thetvdb.service;

import com.thetvdb.model.ActorsData;
import com.thetvdb.model.AuthRequest;
import com.thetvdb.model.AuthResponse;
import com.thetvdb.model.EpisodeData;
import com.thetvdb.model.EpisodesData;
import com.thetvdb.model.ImagesData;
import com.thetvdb.model.SearchSeriesData;
import com.thetvdb.model.SerialData;
import com.thetvdb.model.UpdateData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by dbudyak on 27.06.16.
 */
public interface TvDbRestApi {
    String BASE_URL = "https://api.thetvdb.com/";
    String IMG_URL = "http://thetvdb.com/banners/";

    @POST("login")
    Call<AuthResponse> login(@Body AuthRequest authRequest);

    @GET("updated/query")
    Call<UpdateData> recentlyUpdated(@Query("fromTime") String fromTime);

    @GET("episodes/{id}")
    Call<EpisodeData> episodeInfo(@Path("id") String episodeId);

    @GET("series/{id}")
    Call<SerialData> seriesInfo(@Path("id") String seriesId);

    @GET("series/{id}/actors")
    Call<ActorsData> actorsInfo(@Path("id") String seriesId);

    @GET("series/{id}/episodes")
    Call<EpisodesData> episodesInfo(@Path("id") String serialId);

    @GET("series/{id}/images/query")
    Call<ImagesData> serialImages(@Path("id") String serialId,
                                  @Query("keyType") String keyType,
                                  @Query("resolution") String resolution,
                                  @Query("graphical") String graphical);

    @GET("search/series")
    Call<SearchSeriesData> seriesSearch(@Query("name") String searchPattern);


}
