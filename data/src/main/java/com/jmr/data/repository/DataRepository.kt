package com.jmr.data.repository

import android.os.Environment
import android.util.Log
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.jmr.data.api.API
import com.jmr.data.mapper.DropboxFileMapper
import com.jmr.data.model.FileRequestBody
import com.jmr.domain.model.DropboxFileEntry
import com.jmr.domain.model.DropboxFileList
import com.jmr.domain.repository.RemoteRepository
import com.jmr.domain.usecases.GetTokenUseCase
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val api: API,
    private val dbxRequestConfig: DbxRequestConfig,
    private val getTokenUseCase: GetTokenUseCase
) : RemoteRepository {

    override suspend fun revokeToken() = api.revokeToken()

    override suspend fun getFiles(folder: String?): DropboxFileList =
        DropboxFileMapper().mapToDomainModel(api.getFiles(FileRequestBody(folder ?: "")))

    override suspend fun downloadFile(file: DropboxFileEntry): File? {
        val dbxClientV2 = DbxClientV2(dbxRequestConfig, getTokenUseCase())
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val mFile = File(path, file.name)
        return try {
            FileOutputStream(mFile).use { output ->
                dbxClientV2.files().download(file.path_display).download(output)
            }
            mFile
        } catch (e: Exception) {
            Log.e(DataRepository::class.simpleName, e.message)
            null
        }
    }

}