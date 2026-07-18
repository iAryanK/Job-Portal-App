package com.aryan.jobportalapp.data.auth

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.CookieJar
import java.util.concurrent.ConcurrentHashMap

class PersistentCookieJar(context: Context) : CookieJar {

    private val gson = Gson()
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val cookieStore = ConcurrentHashMap<String, MutableList<Cookie>>()

    init {
        loadCookies()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isEmpty()) return

        val hostCookies = cookieStore.getOrPut(url.host) { mutableListOf() }
        cookies.forEach { newCookie ->
            hostCookies.removeAll { it.name == newCookie.name && it.path == newCookie.path }
            hostCookies.add(newCookie)
        }
        persistCookies()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url.host]
            ?.filter { cookie ->
                cookie.matches(url) && (cookie.expiresAt < 0 || cookie.expiresAt > System.currentTimeMillis())
            }
            ?: emptyList()
    }

    fun clear() {
        cookieStore.clear()
        prefs.edit().clear().apply()
    }

    fun hasCookie(name: String): Boolean {
        return cookieStore.values.any { cookies ->
            cookies.any { cookie ->
                cookie.name == name &&
                    (cookie.expiresAt < 0 || cookie.expiresAt > System.currentTimeMillis())
            }
        }
    }

    private fun persistCookies() {
        val storedCookies = cookieStore.flatMap { (host, cookies) ->
            cookies.map { HostCookie(host, it.toStored()) }
        }
        prefs.edit()
            .putString(KEY_COOKIES, gson.toJson(storedCookies))
            .apply()
    }

    private fun loadCookies() {
        val json = prefs.getString(KEY_COOKIES, null) ?: return
        val type = object : TypeToken<List<HostCookie>>() {}.type
        val storedCookies: List<HostCookie> = gson.fromJson(json, type)

        storedCookies.forEach { hostCookie ->
            cookieStore.getOrPut(hostCookie.host) { mutableListOf() }.add(hostCookie.cookie.toCookie())
        }
    }

    private data class HostCookie(
        val host: String,
        val cookie: StoredCookie
    )

    private data class StoredCookie(
        val name: String,
        val value: String,
        val domain: String,
        val path: String,
        val expiresAt: Long,
        val secure: Boolean,
        val httpOnly: Boolean
    )

    private fun Cookie.toStored(): StoredCookie {
        return StoredCookie(
            name = name,
            value = value,
            domain = domain,
            path = path,
            expiresAt = expiresAt,
            secure = secure,
            httpOnly = httpOnly
        )
    }

    private fun StoredCookie.toCookie(): Cookie {
        val builder = Cookie.Builder()
            .name(name)
            .value(value)
            .domain(domain)
            .path(path)

        if (expiresAt >= 0) {
            builder.expiresAt(expiresAt)
        }
        if (secure) {
            builder.secure()
        }
        if (httpOnly) {
            builder.httpOnly()
        }

        return builder.build()
    }

    private companion object {
        private const val PREFS_NAME = "auth_cookies"
        private const val KEY_COOKIES = "cookies"
    }
}
