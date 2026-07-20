package com.aryan.jobportalapp.data.remote

import com.aryan.jobportalapp.data.remote.dto.ApiResponse
import com.aryan.jobportalapp.data.remote.dto.ApplicationResponse
import com.aryan.jobportalapp.data.remote.dto.InvitationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RecruitmentApi {

    @GET("jobs/application/{jobId}")
    suspend fun getApplicationsOfJob(
        @Path("jobId") jobId: String
    ): Response<ApiResponse<List<ApplicationResponse>>>

    @GET("jobs/invitation/{jobId}")
    suspend fun getInvitationsOfJob(
        @Path("jobId") jobId: String
    ): Response<ApiResponse<List<InvitationResponse>>>

    @GET("jobs/application/jobseeker/{jobSeekerId}")
    suspend fun getApplicationsOfJobSeeker(
        @Path("jobSeekerId") jobSeekerId: String
    ): Response<ApiResponse<List<ApplicationResponse>>>

    @GET("jobs/invitation")
    suspend fun getInvitationsOfJobSeeker(): Response<ApiResponse<List<InvitationResponse>>>
}
