package com.aryan.jobportalapp.data.remote.dto

data class PageResponse<T>(
    val content: List<T>? = null,
    val totalElements: Long? = null,
    val totalPages: Int? = null,
    val number: Int? = null,
    val size: Int? = null,
    val first: Boolean? = null,
    val last: Boolean? = null,
    val empty: Boolean? = null
)
