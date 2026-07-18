package com.aryan.jobportalapp.data.remote

import com.aryan.jobportalapp.data.remote.dto.ApiResponse
import com.aryan.jobportalapp.data.remote.dto.CreateJobSeekerRequest
import com.aryan.jobportalapp.data.remote.dto.JobSeekerResponse
import com.aryan.jobportalapp.data.remote.dto.PatchJobSeekerRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface JobSeekerApi {

    @GET("jobseeker/core/{id}")
    suspend fun getJobSeekerById(@Path("id") id: String): Response<ApiResponse<JobSeekerResponse>>

    @POST("jobseeker/core")
    suspend fun createJobSeeker(
        @Body request: CreateJobSeekerRequest
    ): Response<ApiResponse<JobSeekerResponse>>

    @PATCH("jobseeker/core/{id}")
    suspend fun patchJobSeeker(
        @Path("id") id: String,
        @Body request: PatchJobSeekerRequest
    ): Response<ApiResponse<JobSeekerResponse>>
}
