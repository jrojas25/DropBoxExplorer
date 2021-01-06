package com.jmr.domain.usecases

import com.jmr.domain.repository.LocalRepository
import javax.inject.Inject

class SaveTokenUseCase @Inject constructor(private val repository: LocalRepository){

    operator fun invoke(token: String){
        repository.saveToken(token)
    }

}