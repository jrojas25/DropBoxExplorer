package com.jmr.domain.usecases

import com.jmr.domain.model.DropboxFileEntry
import com.jmr.domain.repository.RemoteRepository
import javax.inject.Inject

class DownloadFileUseCase @Inject constructor(private val repository: RemoteRepository) {
    suspend operator fun invoke(file: DropboxFileEntry) = repository.downloadFile(file)
}