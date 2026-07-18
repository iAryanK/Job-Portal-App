package com.aryan.jobportalapp.data.remote.dto

data class ApiResponse<T>(
    val timestamp: String? = null,
    val data: T? = null,
    val error: ApiError? = null
)

data class ApiError(
    val status: String? = null,
    val message: String? = null,
    val subErrors: List<String>? = null
)
