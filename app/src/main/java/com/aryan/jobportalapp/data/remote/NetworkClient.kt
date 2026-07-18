package com.aryan.jobportalapp.data.remote

import android.content.Context
import com.aryan.jobportalapp.BuildConfig
import com.aryan.jobportalapp.data.auth.PersistentCookieJar
import com.aryan.jobportalapp.data.auth.TokenStore
import com.aryan.jobportalapp.data.remote.interceptor.AuthInterceptor
import com.aryan.jobportalapp.data.remote.interceptor.TokenAuthenticator
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {

    private var appContext: Context? = null

    val gson: Gson = GsonBuilder().create()

    private val cookieJar: PersistentCookieJar by lazy {
        PersistentCookieJar(requireContext())
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .authenticator(
                TokenAuthenticator(
                    baseUrl = BuildConfig.BACKEND_URL,
                    cookieJar = cookieJar,
                    gson = gson
                )
            )
            .addInterceptor(AuthInterceptor())
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                }
            }
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ensureTrailingSlash(BuildConfig.BACKEND_URL))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    fun init(context: Context) {
        appContext = context.applicationContext
        TokenStore.init(context)
        cookieJar
        okHttpClient
        retrofit
        authApi
    }

    private fun requireContext(): Context {
        return requireNotNull(appContext) {
            "NetworkClient.init(context) must be called before making API requests."
        }
    }

    private fun ensureTrailingSlash(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }
}
