package ru.medbox.di.module

import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.medbox.api.AUTH_URL
import ru.medbox.api.Api
import ru.medbox.api.AuthApi
import ru.medbox.api.URL
import ru.medbox.utils.Loggable
import ru.medbox.utils.Prefs
import java.util.concurrent.TimeUnit


@Module
@Suppress("unused")
object NetworkModule : Loggable {

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideApi(client: OkHttpClient): Api {
        return Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(client)
                .build()
                .create(Api::class.java)
    }

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideAuthApi(client: OkHttpClient): AuthApi {
        return Retrofit.Builder()
                .baseUrl(AUTH_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(client)
                .build()
                .create(AuthApi::class.java)
    }

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideHttpClient(prefs: Prefs): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient().newBuilder()
                .addInterceptor(logging)
                .addNetworkInterceptor {
                    var newBuilder = it.request().newBuilder()
                    if (!prefs.token.isEmpty()) {
                        log(this, "set accept token ${prefs.token}")
                        newBuilder = newBuilder.addHeader("Authorization", "Bearer ${prefs.token}")
                    } else {
                        log(this, "accept token is not set")
                    }
                    val request = newBuilder.build()
                    val response = it.proceed(request)
                    log(this, response.message())
                    log(this, response.headers()?.toString())

                    response
                }
                .readTimeout(10, TimeUnit.SECONDS).build()
    }
}