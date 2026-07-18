package com.aryan.jobportalapp.data.remote.dto

data class OtpRequest(
    val email: String
)

data class EmailAuthRequest(
    val email: String,
    val otp: String
)

data class AuthResponse(
    val id: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null
)
