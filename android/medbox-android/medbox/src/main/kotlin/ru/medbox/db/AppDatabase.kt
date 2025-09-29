package ru.medbox.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import ru.medbox.db.model.*

@Database(
        version = 1,
        entities = [
            Category::class,
            Article::class,
            Lecture::class,
            Doctor::class,
            Feedback::class,
            Specialization::class,
            Medcard::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): Dao
}
