package com.aryan.jobportalapp.data.remote

import com.aryan.jobportalapp.data.remote.dto.ApiResponse
import com.aryan.jobportalapp.data.remote.dto.ApplicationRequest
import com.aryan.jobportalapp.data.remote.dto.ApplicationResponse
import com.aryan.jobportalapp.data.remote.dto.UpdateApplicationRequest
import com.aryan.jobportalapp.data.remote.dto.UpdateInvitationRequest
import com.aryan.jobportalapp.data.remote.dto.InvitationRequest
import com.aryan.jobportalapp.data.remote.dto.InvitationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface RecruitmentApi {

    @GET("jobs/application/{jobId}")
    suspend fun getApplicationsOfJob(
        @Path("jobId") jobId: String
    ): Response<ApiResponse<List<ApplicationResponse>>>

    @GET("jobs/application/jobseeker/{jobSeekerId}")
    suspend fun getApplicationsOfJobSeeker(
        @Path("jobSeekerId") jobSeekerId: String
    ): Response<ApiResponse<List<ApplicationResponse>>>

    @POST("jobs/application")
    suspend fun createApplication(
        @Body request: ApplicationRequest
    ): Response<ApiResponse<ApplicationResponse>>

    @PATCH("jobs/application/{applicationId}")
    suspend fun updateApplicationStatus(
        @Path("applicationId") applicationId: String,
        @Body request: UpdateApplicationRequest
    ): Response<Unit>

    @GET("jobs/invitation/{jobId}")
    suspend fun getInvitationsOfJob(
        @Path("jobId") jobId: String
    ): Response<ApiResponse<List<InvitationResponse>>>

    @POST("jobs/invitation")
    suspend fun createInvitation(
        @Body request: InvitationRequest
    ): Response<ApiResponse<InvitationResponse>>

    @GET("jobs/invitation")
    suspend fun getInvitationsOfJobSeeker(): Response<ApiResponse<List<InvitationResponse>>>

    @PATCH("jobs/invitation/{invitationId}")
    suspend fun updateInvitationStatus(
        @Path("invitationId") invitationId: String,
        @Body request: UpdateInvitationRequest
    ): Response<Unit>
}
