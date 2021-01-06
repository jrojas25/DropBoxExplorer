package com.jmr.data.di.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jmr.data.BuildConfig
import com.jmr.data.api.interceptor.HeaderInterceptor
import com.jmr.domain.usecases.GetTokenUseCase
import com.jmr.domain.usecases.HasTokenUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetworkModule {
    @Provides
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideOkHttpClient(getTokenUseCase: GetTokenUseCase, hasTokenUseCase: HasTokenUseCase) =
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(HeaderInterceptor(getTokenUseCase, hasTokenUseCase))
                .build()
        } else OkHttpClient
            .Builder()
            .addInterceptor(HeaderInterceptor(getTokenUseCase, hasTokenUseCase))
            .build()

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .setLenient()
            .create()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson,
        BASE_URL: String
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
}