package com.jmr.domain.usecases

interface PrimitiveUseCase <T> {
    operator fun invoke(): T
}