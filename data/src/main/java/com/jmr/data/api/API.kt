package com.jmr.data.api

import com.jmr.data.di.module.APIModule
import com.jmr.data.model.DropboxFileList
import com.jmr.data.model.FileRequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface API {

    @POST("auth/token/revoke")
    suspend fun revokeToken()

    @Headers("${APIModule.CONTENT_TYPE_HEADER}: ${APIModule.CONTENT_TYPE_JSON}")
    @POST("files/list_folder")
    suspend fun getFiles(@Body requestBody: FileRequestBody): DropboxFileList

}