package com.jmr.data.repository

import com.jmr.data.util.DropBoxBrowserPrefs
import com.jmr.domain.repository.LocalRepository
import javax.inject.Inject

class AppSettingsRepository @Inject constructor(private val sharedPrefs: DropBoxBrowserPrefs) :
    LocalRepository {

    override fun saveToken(token: String) {
        sharedPrefs.save(DropBoxBrowserPrefs.TOKEN to token)
    }

    override fun getToken(): String {
        return sharedPrefs.get(DropBoxBrowserPrefs.TOKEN)
            ?: throw IllegalStateException("Token is null")
    }

    override fun hasToken(): Boolean {
        return sharedPrefs.contains(DropBoxBrowserPrefs.TOKEN)
    }

    override fun removeCredentials() {
        sharedPrefs.delete(DropBoxBrowserPrefs.TOKEN)
    }

}