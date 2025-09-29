package com.thetvdb.util;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dbudyak on 27.06.16.
 */
public class AuthInterceptor implements Interceptor {

    public static final String AUTHORIZATION = "Authorization";
    private String jwtTokenHeader;

    public AuthInterceptor(String jwtTokenHeader) {
        this.jwtTokenHeader = jwtTokenHeader;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request orig = chain.request();
        Request.Builder requestBuilder = orig.newBuilder();
        if (!jwtTokenHeader.isEmpty()) {
            requestBuilder.header(AUTHORIZATION, jwtTokenHeader);
        }
        Request request = requestBuilder.build();
        Response response = chain.proceed(request);
        return response;

    }
}
