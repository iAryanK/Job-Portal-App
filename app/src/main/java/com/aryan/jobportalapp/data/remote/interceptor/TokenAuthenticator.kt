package com.aryan.jobportalapp.data.remote.interceptor

import com.aryan.jobportalapp.data.auth.PersistentCookieJar
import com.aryan.jobportalapp.data.auth.TokenStore
import com.aryan.jobportalapp.data.remote.dto.ApiResponse
import com.aryan.jobportalapp.data.remote.dto.AuthResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Authenticator
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.TimeUnit

class TokenAuthenticator(
    private val baseUrl: String,
    private val cookieJar: CookieJar,
    private val gson: Gson
) : Authenticator {

    private val refreshLock = Any()

    private val refreshClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2 || isRefreshRequest(response.request)) {
            return null
        }

        synchronized(refreshLock) {
            val currentAccessToken = TokenStore.getAccessToken()
            val requestAuthorization = response.request.header(AUTHORIZATION_HEADER)

            if (
                currentAccessToken != null &&
                requestAuthorization != null &&
                requestAuthorization != "$BEARER_PREFIX$currentAccessToken"
            ) {
                return response.request.newBuilder()
                    .header(AUTHORIZATION_HEADER, "$BEARER_PREFIX$currentAccessToken")
                    .build()
            }

            val refreshResponse = refreshClient.newCall(buildRefreshRequest()).execute()
            refreshResponse.use { refreshResult ->
                if (!refreshResult.isSuccessful) {
                    clearSession()
                    return null
                }

                val responseBody = refreshResult.body?.string()
                if (responseBody.isNullOrBlank()) {
                    clearSession()
                    return null
                }

                val type = object : TypeToken<ApiResponse<AuthResponse>>() {}.type
                val apiResponse: ApiResponse<AuthResponse> = gson.fromJson(responseBody, type)
                val newAccessToken = apiResponse.data?.accessToken

                if (newAccessToken.isNullOrBlank()) {
                    clearSession()
                    return null
                }

                TokenStore.saveAccessToken(newAccessToken)
            }

            return response.request.newBuilder()
                .header(AUTHORIZATION_HEADER, "$BEARER_PREFIX${TokenStore.getAccessToken()}")
                .build()
        }
    }

    private fun buildRefreshRequest(): Request {
        return Request.Builder()
            .url("${ensureTrailingSlash(baseUrl)}auth/core/refresh")
            .get()
            .build()
    }

    private fun clearSession() {
        TokenStore.clear()
        if (cookieJar is PersistentCookieJar) {
            cookieJar.clear()
        }
    }

    private fun isRefreshRequest(request: Request): Boolean {
        return request.url.encodedPath.endsWith(PUBLIC_REFRESH_PATH)
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }

    private fun ensureTrailingSlash(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }

    private companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        private const val PUBLIC_REFRESH_PATH = "/auth/core/refresh"
    }
}
