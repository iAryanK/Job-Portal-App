package com.aryan.jobportalapp.data.auth

import android.content.Context
import android.content.SharedPreferences

object TokenStore {

    private const val PREFS_NAME = "auth_tokens"
    private const val KEY_ACCESS_TOKEN = "access_token"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        if (::prefs.isInitialized) return
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)?.takeIf { it.isNotBlank() }
    }

    fun hasAccessToken(): Boolean = !getAccessToken().isNullOrBlank()

    fun saveAccessToken(token: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
