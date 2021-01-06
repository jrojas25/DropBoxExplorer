package com.jmr.data.util

import android.app.Application
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject

class DropBoxBrowserPrefs @Inject constructor(application: Application) {
    private val sharedPreferences: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            application,
            "encrypted_prefs",
            MasterKey.Builder(application).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun save(vararg pairs: Pair<String, String>) {
        sharedPreferences.edit {
            pairs.forEach {
                this.putString(it.first, it.second)
            }
        }
    }

    fun get(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun delete(vararg keys: String) {
        sharedPreferences.edit {
            keys.forEach { remove(it) }
        }
    }

    fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }

    companion object {
        const val TOKEN = "token"
    }
}