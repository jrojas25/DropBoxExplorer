package com.jmr.data.di.module

import com.dropbox.core.DbxRequestConfig
import com.jmr.data.api.API
import com.jmr.data.repository.AppSettingsRepository
import com.jmr.data.repository.DataRepository
import com.jmr.data.util.DropBoxBrowserPrefs
import com.jmr.domain.repository.LocalRepository
import com.jmr.domain.repository.RemoteRepository
import com.jmr.domain.usecases.GetTokenUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideRepository(
        api: API,
        dbxRequestConfig: DbxRequestConfig,
        getTokenUseCase: GetTokenUseCase
    ): RemoteRepository = DataRepository(api, dbxRequestConfig, getTokenUseCase)

    @Provides
    @Singleton
    fun provideAppSettingsRepository(sharedPreferences: DropBoxBrowserPrefs): LocalRepository =
        AppSettingsRepository(sharedPreferences)
}