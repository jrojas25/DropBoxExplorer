package com.jmr.domain.usecases

import com.jmr.domain.repository.LocalRepository
import javax.inject.Inject

class HasTokenUseCase @Inject constructor(private val repository: LocalRepository) :
    PrimitiveUseCase<Boolean> {
    override fun invoke(): Boolean {
        return repository.hasToken()
    }
}