package com.jmr.data.api.interceptor

import com.jmr.data.di.module.APIModule
import com.jmr.domain.usecases.GetTokenUseCase
import com.jmr.domain.usecases.HasTokenUseCase
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(
    private val getTokenUseCase: GetTokenUseCase,
    private val hasTokenUseCase: HasTokenUseCase
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()

        if (request.header(APIModule.AUTHORIZATION_HEADER).isNullOrEmpty() && hasTokenUseCase()) {
            requestBuilder.addHeader(APIModule.AUTHORIZATION_HEADER, "Bearer ${getTokenUseCase()}")
        }
        return chain.proceed(requestBuilder.build())
    }
}