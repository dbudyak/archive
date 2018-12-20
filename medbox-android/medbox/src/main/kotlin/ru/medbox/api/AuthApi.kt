package ru.medbox.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

const val AUTH_URL: String = "https://bx.zdorovkin.org:9443/"

interface AuthApi {

    @FormUrlEncoded
    @POST("/auth/realms/jhipster/protocol/openid-connect/token") fun exchangeToken(
            @Field("client_id") id: String,
            @Field("client_secret") secret: String,
            @Field("grant_type") grantType: String,
            @Field("subject_token") subjectToken: String,
            @Field("subject_issuer") subjectIssuer: String,
            @Field("subject_token_type") subjectTokenType: String,
            @Field("requested_token_type") requestedTokenType: String
    ): Observable<ResponseBody>
}