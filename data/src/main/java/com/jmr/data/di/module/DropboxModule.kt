package com.jmr.data.di.module

import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.http.OkHttp3Requestor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class DropboxModule {

    @Provides
    @Singleton
    fun provideRequestConfig(okHttpClient: OkHttpClient): DbxRequestConfig =
        DbxRequestConfig.newBuilder("HDropBrowser")
            .withHttpRequestor(OkHttp3Requestor(okHttpClient))
            .build()

}