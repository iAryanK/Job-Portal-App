package com.aryan.jobportalapp.data.remote

import com.aryan.jobportalapp.data.remote.dto.ApiResponse
import com.aryan.jobportalapp.data.remote.dto.CreateEmployerRequest
import com.aryan.jobportalapp.data.remote.dto.EmployerResponse
import com.aryan.jobportalapp.data.remote.dto.PatchEmployerRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface EmployerApi {

    @GET("employer/core/{id}")
    suspend fun getEmployerById(@Path("id") id: String): Response<ApiResponse<EmployerResponse>>

    @POST("employer/core")
    suspend fun createEmployer(
        @Body request: CreateEmployerRequest
    ): Response<ApiResponse<EmployerResponse>>

    @PATCH("employer/core/{id}")
    suspend fun patchEmployer(
        @Path("id") id: String,
        @Body request: PatchEmployerRequest
    ): Response<ApiResponse<EmployerResponse>>
}
