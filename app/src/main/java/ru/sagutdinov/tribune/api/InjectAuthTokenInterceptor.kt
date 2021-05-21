package ru.sagutdinov.tribune.api

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import ru.sagutdinov.tribune.BuildConfig


const val AUTH_TOKEN_HEADER = "Authorization"

class InjectAuthTokenInterceptor(private val authToken: String): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()


        val requestWithToken = originalRequest.newBuilder()
            .header(AUTH_TOKEN_HEADER, "Bearer $authToken")
            .build()



        return chain.proceed(requestWithToken)
    }
}