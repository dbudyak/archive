package ru.medbox.di.module

import android.arch.persistence.room.Room
import android.preference.PreferenceManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.Reusable
import ru.medbox.MedboxApplication
import ru.medbox.db.AppDatabase
import ru.medbox.db.Dao
import ru.medbox.utils.Prefs

const val DB_NAME = "medbox_db"

@Module
@Suppress("unused")
object PersistenceModule {

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideDatabase(): AppDatabase {
        return Room.databaseBuilder(MedboxApplication.applicationContext(), AppDatabase::class.java, DB_NAME).build()
    }

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideDao(database: AppDatabase): Dao {
        return database.dao()
    }

    @Provides
    @Reusable
    @JvmStatic
    internal fun providePrefs(): Prefs {
        return Prefs(PreferenceManager.getDefaultSharedPreferences(MedboxApplication.applicationContext()))
    }

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideMoshi(): Moshi {
        return Moshi.Builder().add(KotlinJsonAdapterFactory()).build();
    }

}