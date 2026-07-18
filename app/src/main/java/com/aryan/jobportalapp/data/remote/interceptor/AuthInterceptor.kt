package com.aryan.jobportalapp.data.remote.interceptor

import com.aryan.jobportalapp.data.auth.TokenStore
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!requiresAccessToken(request.url.encodedPath)) {
            return chain.proceed(request)
        }

        val accessToken = TokenStore.getAccessToken()
            ?: return chain.proceed(request)

        val authenticatedRequest = request.newBuilder()
            .header(AUTHORIZATION_HEADER, "Bearer $accessToken")
            .build()

        return chain.proceed(authenticatedRequest)
    }

    private fun requiresAccessToken(path: String): Boolean {
        return !path.endsWith(PUBLIC_SEND_OTP_PATH) &&
            !path.endsWith(PUBLIC_EMAIL_AUTH_PATH) &&
            !path.endsWith(PUBLIC_REFRESH_PATH) &&
            !path.endsWith(PUBLIC_GOOGLE_AUTH_PATH)
    }

    private companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val PUBLIC_SEND_OTP_PATH = "/auth/core/send-email-otp"
        private const val PUBLIC_EMAIL_AUTH_PATH = "/auth/core/email"
        private const val PUBLIC_REFRESH_PATH = "/auth/core/refresh"
        private const val PUBLIC_GOOGLE_AUTH_PATH = "/auth/core/google"
    }
}
