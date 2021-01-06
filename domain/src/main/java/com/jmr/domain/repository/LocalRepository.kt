package com.jmr.domain.repository

interface LocalRepository {

    fun saveToken(token: String)
    fun getToken(): String
    fun hasToken(): Boolean

    fun removeCredentials()
}