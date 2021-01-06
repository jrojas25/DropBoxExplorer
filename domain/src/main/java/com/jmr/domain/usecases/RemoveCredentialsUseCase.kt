package com.jmr.domain.usecases

import com.jmr.domain.repository.LocalRepository
import javax.inject.Inject

class RemoveCredentialsUseCase @Inject constructor(private val repository: LocalRepository) {
    operator fun invoke() {
        repository.removeCredentials()
    }
}