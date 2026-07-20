package com.aryan.jobportalapp.data.remote

import com.aryan.jobportalapp.data.remote.dto.ApiResponse
import com.aryan.jobportalapp.data.remote.dto.CreateJobSeekerRequest
import com.aryan.jobportalapp.data.remote.dto.FindJobSeekersFilter
import com.aryan.jobportalapp.data.remote.dto.Gender
import com.aryan.jobportalapp.data.remote.dto.JobSeekerResponse
import com.aryan.jobportalapp.data.remote.dto.JobSeekerSummaryResponse
import com.aryan.jobportalapp.data.remote.dto.PageResponse
import com.aryan.jobportalapp.data.remote.dto.PatchJobSeekerRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface JobSeekerApi {

    @GET("jobseeker/core")
    suspend fun findJobSeekers(
        @Query("search") search: String? = null,
        @Query("gender") gender: Gender? = null,
        @Query("city") city: String? = null,
        @Query("district") district: String? = null,
        @Query("state") state: String? = null,
        @Query("country") country: String? = null,
        @Query("minExperienceYears") minExperienceYears: Int? = null,
        @Query("maxExperienceYears") maxExperienceYears: Int? = null,
        @Query("currentlyWorking") currentlyWorking: Boolean? = null,
        @Query("willingToRelocate") willingToRelocate: Boolean? = null,
        @Query("openToRemote") openToRemote: Boolean? = null,
        @Query("preferredJobLocation") preferredJobLocation: String? = null,
        @Query("highestQualification") highestQualification: String? = null,
        @Query("specialization") specialization: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "createdAt,desc"
    ): Response<ApiResponse<PageResponse<JobSeekerSummaryResponse>>>

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
