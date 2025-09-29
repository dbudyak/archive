package ru.medbox.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.medbox.db.model.*

const val URL: String = "http://185.212.148.95:8080/"

interface Api {

    @GET("/api/specializations") fun getSpecializations(): Observable<List<Specialization>>
    @GET("/api/doctors") fun getDoctors(): Observable<List<Doctor>>
    @GET("/api/feedback") fun getFeedback(): Observable<List<Feedback>>
    @GET("/api/emr-records") fun getMedcard(): Observable<List<Medcard>>
    @GET("/api/categories") fun getCategories(): Observable<List<Category>>
    @GET("/api/articles") fun getArticles(): Observable<List<Article>>
    @GET("/api/articles/{article_id}") fun getArticle(
            @Path("article_id") articleId: Int
    ): Observable<Article>

    @GET("/api/lectures") fun getLectures(): Observable<List<Lecture>>
    @GET("/api/lectures/{lecture_id}") fun getLecture(
            @Path("lecture_id") lectureId: Int
    ): Observable<Lecture>

    @PUT("/api/emr-records") fun updateMedcard(
            @Body medcard: ru.medbox.db.model.Medcard
    ): Observable<ResponseBody>

}