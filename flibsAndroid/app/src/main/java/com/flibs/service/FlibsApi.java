package com.flibs.service;

import com.flibs.model.BookModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by dbudyak on 27.06.16.
 */
public interface FlibsApi {
    String BASE_URL = "http://flibs.efnez.ru/php/";

    @GET("GetBooks.php")
    Call<List<BookModel>> bookList(@Query("t") String query);

}
