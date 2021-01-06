package com.jmr.domain.repository

import com.jmr.domain.model.DropboxFileEntry
import com.jmr.domain.model.DropboxFileList
import java.io.File

interface RemoteRepository {

    suspend fun revokeToken()
    suspend fun getFiles(folder: String?): DropboxFileList
    suspend fun downloadFile(file: DropboxFileEntry): File?

}