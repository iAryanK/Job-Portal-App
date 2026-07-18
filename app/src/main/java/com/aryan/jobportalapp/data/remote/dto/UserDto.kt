package com.aryan.jobportalapp.data.remote.dto

data class AppUserResponse(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val role: AppUserRole? = null,
    val profilePhoto: String? = null
)

data class UpdateAppUserRequest(
    val role: AppUserRole
)
