package com.jmr.domain.usecases

import com.jmr.domain.repository.RemoteRepository
import javax.inject.Inject

class GetFilesUseCase @Inject constructor(private val repository: RemoteRepository){
    suspend operator fun invoke(folder: String?) = repository.getFiles(folder)
}