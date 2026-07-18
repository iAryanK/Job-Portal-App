package com.aryan.jobportalapp.data.repository

import com.aryan.jobportalapp.data.remote.EmployerApi
import com.aryan.jobportalapp.data.remote.NetworkClient
import com.aryan.jobportalapp.data.remote.dto.ApiResponse
import com.aryan.jobportalapp.data.remote.dto.CreateEmployerRequest
import com.aryan.jobportalapp.data.remote.dto.EmployerResponse
import com.aryan.jobportalapp.data.remote.dto.PatchEmployerRequest
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import java.io.IOException

class EmployerRepository(
    private val employerApi: EmployerApi = NetworkClient.employerApi
) {

    suspend fun getEmployerById(id: String): Result<EmployerResponse> {
        return try {
            val response = employerApi.getEmployerById(id)
            if (response.isSuccessful) {
                val employer = response.body()?.data
                if (employer != null) {
                    Result.success(employer)
                } else {
                    Result.failure(IOException("Employer response was empty"))
                }
            } else if (response.code() == 404) {
                Result.failure(ProfileNotFoundException())
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(exception.message ?: "Unable to fetch employer profile.")
            )
        }
    }

    suspend fun createEmployer(request: CreateEmployerRequest): Result<EmployerResponse> {
        return try {
            val response = employerApi.createEmployer(request)
            if (response.isSuccessful) {
                val employer = response.body()?.data
                if (employer != null) {
                    Result.success(employer)
                } else {
                    Result.failure(IOException("Create employer response was empty"))
                }
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(exception.message ?: "Unable to create employer profile.")
            )
        }
    }

    suspend fun patchEmployer(id: String, request: PatchEmployerRequest): Result<EmployerResponse> {
        return try {
            val response = employerApi.patchEmployer(id, request)
            if (response.isSuccessful) {
                val employer = response.body()?.data
                if (employer != null) {
                    Result.success(employer)
                } else {
                    Result.failure(IOException("Update employer response was empty"))
                }
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(exception.message ?: "Unable to update employer profile.")
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

class ProfileNotFoundException : IOException("Profile not found")
