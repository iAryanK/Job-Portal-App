package com.aryan.jobportalapp.data.remote

import com.aryan.jobportalapp.data.remote.dto.ApiResponse
import com.aryan.jobportalapp.data.remote.dto.CreateJobRequest
import com.aryan.jobportalapp.data.remote.dto.CurrencyType
import com.aryan.jobportalapp.data.remote.dto.EmploymentType
import com.aryan.jobportalapp.data.remote.dto.ExperienceLevel
import com.aryan.jobportalapp.data.remote.dto.PageResponse
import com.aryan.jobportalapp.data.remote.dto.JobResponse
import com.aryan.jobportalapp.data.remote.dto.JobSummaryResponse
import com.aryan.jobportalapp.data.remote.dto.PatchJobRequest
import com.aryan.jobportalapp.data.remote.dto.WorkplaceType
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
        @Query("search") search: String? = null,
        @Query("employmentType") employmentType: EmploymentType? = null,
        @Query("workplaceType") workplaceType: WorkplaceType? = null,
        @Query("experienceLevel") experienceLevel: ExperienceLevel? = null,
        @Query("currency") currency: CurrencyType? = null,
        @Query("city") city: String? = null,
        @Query("district") district: String? = null,
        @Query("state") state: String? = null,
        @Query("country") country: String? = null,
        @Query("minSalary") minSalary: Double? = null,
        @Query("maxSalary") maxSalary: Double? = null,
        @Query("minExperience") minExperience: Int? = null,
        @Query("maxExperience") maxExperience: Int? = null,
        @Query("salaryNegotiable") salaryNegotiable: Boolean? = null,
        @Query("relocationAvailable") relocationAvailable: Boolean? = null,
        @Query("accommodationProvided") accommodationProvided: Boolean? = null,
        @Query("transportationProvided") transportationProvided: Boolean? = null,
        @Query("foodProvided") foodProvided: Boolean? = null,
        @Query("industry") industry: String? = null,
        @Query("employerId") employerId: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
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
