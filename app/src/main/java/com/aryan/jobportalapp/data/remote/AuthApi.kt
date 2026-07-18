package com.aryan.jobportalapp.data.remote

import com.aryan.jobportalapp.data.remote.dto.ApiResponse
import com.aryan.jobportalapp.data.remote.dto.AppUserResponse
import com.aryan.jobportalapp.data.remote.dto.AuthResponse
import com.aryan.jobportalapp.data.remote.dto.EmailAuthRequest
import com.aryan.jobportalapp.data.remote.dto.OtpRequest
import com.aryan.jobportalapp.data.remote.dto.UpdateAppUserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/core/send-email-otp")
    suspend fun sendEmailOtp(@Body request: OtpRequest): Response<Unit>

    @POST("auth/core/email")
    suspend fun authenticateViaEmail(
        @Body request: EmailAuthRequest
    ): Response<ApiResponse<AuthResponse>>

    @GET("auth/core/refresh")
    suspend fun refreshToken(): Response<ApiResponse<AuthResponse>>

    @GET("auth/core/me")
    suspend fun getCurrentUser(): Response<ApiResponse<AppUserResponse>>

    @PATCH("auth/core/me")
    suspend fun updateMe(@Body request: UpdateAppUserRequest): Response<Unit>
}
