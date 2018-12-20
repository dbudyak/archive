package ru.medbox.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import ru.medbox.db.model.*

@Dao
interface Dao {

    @get:Query("SELECT * FROM Doctor") val doctors: List<Doctor>
    @get:Query("SELECT * FROM Specialization") val specializations: List<Specialization>
    @get:Query("SELECT * FROM Feedback") val feedbacks: List<Feedback>
    @get:Query("SELECT * FROM Medcard") val medcard: List<Medcard>
    @get:Query("SELECT * FROM Category") val categories: List<Category>
    @get:Query("SELECT * FROM Article") val articles: List<Article>
    @get:Query("SELECT * FROM Lecture") val lectures: List<Lecture>

    @Query("SELECT * FROM Article WHERE categoryId == :categoryId") fun getArticlesById(
            vararg categoryId: Int
    ): List<Article>

    @Query("SELECT * FROM Article WHERE id == :articleId") fun getArticleById(
            vararg articleId: Int
    ): Article

    @Query("SELECT * FROM Lecture WHERE id == :lectureId") fun getLectureById(
            vararg lectureId: Int
    ): Lecture

    @Query("SELECT * FROM Doctor WHERE id == :doctorId") fun getDoctorById(
            vararg doctorId: Int
    ): Doctor

    @Query("SELECT * FROM Specialization WHERE id == :specializationId") fun getSpecializationById(
            vararg specializationId: Int
    ): Specialization

    @Insert fun insertCategory(vararg category: Category)
    @Insert fun insertArticle(vararg articles: Article)
    @Insert fun insertLecture(vararg lecture: Lecture)
    @Insert fun insertDoctor(vararg doctor: Doctor)
    @Insert fun insertSpecialization(vararg specialization: Specialization)
    @Insert fun insertFeedback(vararg feedback: Feedback)
    @Insert fun insertMedcard(vararg medcard: Medcard)

}