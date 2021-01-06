package com.jmr.domain.usecases

import com.jmr.domain.repository.RemoteRepository
import javax.inject.Inject

class RevokeTokenUseCase @Inject constructor(private val repository: RemoteRepository) {
    suspend operator fun invoke() {
        repository.revokeToken()
    }
}