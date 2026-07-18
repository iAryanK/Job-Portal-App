package com.aryan.jobportalapp.data.repository

import com.aryan.jobportalapp.data.auth.TokenStore
import com.aryan.jobportalapp.data.remote.AuthApi
import com.aryan.jobportalapp.data.remote.NetworkClient
import com.aryan.jobportalapp.data.remote.dto.ApiResponse
import com.aryan.jobportalapp.data.remote.dto.AppUserResponse
import com.aryan.jobportalapp.data.remote.dto.AppUserRole
import com.aryan.jobportalapp.data.remote.dto.AuthResponse
import com.aryan.jobportalapp.data.remote.dto.EmailAuthRequest
import com.aryan.jobportalapp.data.remote.dto.OtpRequest
import com.aryan.jobportalapp.data.remote.dto.UpdateAppUserRequest
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import java.io.IOException

class AuthRepository(
    private val authApi: AuthApi = NetworkClient.authApi
) {

    suspend fun sendOtp(email: String): Result<Unit> {
        return try {
            val response = authApi.sendEmailOtp(OtpRequest(email))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(
                    exception.message ?: "Unable to send OTP. Check your connection."
                )
            )
        }
    }

    suspend fun verifyOtp(email: String, otp: String): Result<AuthResponse> {
        return try {
            val response = authApi.authenticateViaEmail(EmailAuthRequest(email, otp))
            if (response.isSuccessful) {
                val authResponse = response.body()?.data
                if (authResponse != null) {
                    authResponse.accessToken?.let(TokenStore::saveAccessToken)
                    Result.success(authResponse)
                } else {
                    Result.failure(IOException("Authentication response was empty"))
                }
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(
                    exception.message ?: "Unable to verify OTP. Check your connection."
                )
            )
        }
    }

    suspend fun getCurrentUser(): Result<AppUserResponse> {
        return try {
            val response = authApi.getCurrentUser()
            if (response.isSuccessful) {
                val user = response.body()?.data
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(IOException("User profile response was empty"))
                }
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(
                    exception.message ?: "Unable to fetch user profile. Check your connection."
                )
            )
        }
    }

    suspend fun updateRole(role: AppUserRole): Result<Unit> {
        return try {
            val response = authApi.updateMe(UpdateAppUserRequest(role))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(
                    exception.message ?: "Unable to update role. Check your connection."
                )
            )
        }
    }

    suspend fun refreshSession(): Result<AuthResponse> {
        return try {
            val response = authApi.refreshToken()
            if (response.isSuccessful) {
                val authResponse = response.body()?.data
                if (authResponse != null) {
                    authResponse.accessToken?.let(TokenStore::saveAccessToken)
                    Result.success(authResponse)
                } else {
                    Result.failure(IOException("Refresh response was empty"))
                }
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(
                    exception.message ?: "Unable to refresh session. Check your connection."
                )
            )
        }
    }

    private fun <T> parseErrorMessage(response: Response<T>): String {
        val errorBody = response.errorBody()?.string()
        if (errorBody.isNullOrBlank()) {
            return "Request failed with code ${response.code()}"
        }

        return try {
            val type = object : TypeToken<ApiResponse<Any>>() {}.type
            val apiResponse: ApiResponse<Any> = NetworkClient.gson.fromJson(errorBody, type)
            apiResponse.error?.message
                ?: apiResponse.error?.subErrors?.joinToString("\n")
                ?: "Request failed with code ${response.code()}"
        } catch (_: Exception) {
            "Request failed with code ${response.code()}"
        }
    }
}
