package com.jmr.domain.usecases

import com.jmr.domain.repository.LocalRepository
import javax.inject.Inject

class GetTokenUseCase @Inject constructor(private val repository: LocalRepository) :
    PrimitiveUseCase<String> {
    override operator fun invoke(): String {
        return repository.getToken()
    }
}