package com.jmr.data.di.module

import com.jmr.data.api.API
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class APIModule {
    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val CONTENT_TYPE_HEADER = "Content-Type"
        const val CONTENT_TYPE_JSON = "application/json"
    }

    @Provides
    @Singleton
    fun provideAPI(retrofit: Retrofit): API = retrofit.create(API::class.java)
}