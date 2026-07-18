package com.aryan.jobportalapp.data.remote

import com.aryan.jobportalapp.data.remote.dto.ApiResponse
import com.aryan.jobportalapp.data.remote.dto.CreateJobRequest
import com.aryan.jobportalapp.data.remote.dto.PageResponse
import com.aryan.jobportalapp.data.remote.dto.JobResponse
import com.aryan.jobportalapp.data.remote.dto.JobSummaryResponse
import com.aryan.jobportalapp.data.remote.dto.PatchJobRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface JobApi {

    @GET("jobs/core")
    suspend fun findJobs(
        @Query("employerId") employerId: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50,
        @Query("sort") sort: String = "createdAt,desc"
    ): Response<ApiResponse<PageResponse<JobSummaryResponse>>>

    @POST("jobs/core")
    suspend fun createJob(
        @Body request: CreateJobRequest
    ): Response<ApiResponse<JobResponse>>

    @PATCH("jobs/core/{id}")
    suspend fun patchJob(
        @Path("id") id: String,
        @Body request: PatchJobRequest
    ): Response<Unit>

    @GET("jobs/core/{id}")
    suspend fun getJobById(
        @Path("id") id: String
    ): Response<ApiResponse<JobResponse>>
}
