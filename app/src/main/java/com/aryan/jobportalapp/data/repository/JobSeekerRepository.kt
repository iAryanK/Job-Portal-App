package com.aryan.jobportalapp.data.repository

import com.aryan.jobportalapp.data.remote.JobSeekerApi
import com.aryan.jobportalapp.data.remote.NetworkClient
import com.aryan.jobportalapp.data.remote.dto.ApiResponse
import com.aryan.jobportalapp.data.remote.dto.CreateJobSeekerRequest
import com.aryan.jobportalapp.data.remote.dto.JobSeekerResponse
import com.aryan.jobportalapp.data.remote.dto.PatchJobSeekerRequest
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import java.io.IOException

class JobSeekerRepository(
    private val jobSeekerApi: JobSeekerApi = NetworkClient.jobSeekerApi
) {

    suspend fun getJobSeekerById(id: String): Result<JobSeekerResponse> {
        return try {
            val response = jobSeekerApi.getJobSeekerById(id)
            if (response.isSuccessful) {
                val jobSeeker = response.body()?.data
                if (jobSeeker != null) {
                    Result.success(jobSeeker)
                } else {
                    Result.failure(IOException("Job seeker response was empty"))
                }
            } else if (response.code() == 404) {
                Result.failure(ProfileNotFoundException())
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(exception.message ?: "Unable to fetch job seeker profile.")
            )
        }
    }

    suspend fun createJobSeeker(request: CreateJobSeekerRequest): Result<JobSeekerResponse> {
        return try {
            val response = jobSeekerApi.createJobSeeker(request)
            if (response.isSuccessful) {
                val jobSeeker = response.body()?.data
                if (jobSeeker != null) {
                    Result.success(jobSeeker)
                } else {
                    Result.failure(IOException("Create job seeker response was empty"))
                }
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(exception.message ?: "Unable to create job seeker profile.")
            )
        }
    }

    suspend fun patchJobSeeker(id: String, request: PatchJobSeekerRequest): Result<JobSeekerResponse> {
        return try {
            val response = jobSeekerApi.patchJobSeeker(id, request)
            if (response.isSuccessful) {
                val jobSeeker = response.body()?.data
                if (jobSeeker != null) {
                    Result.success(jobSeeker)
                } else {
                    Result.failure(IOException("Update job seeker response was empty"))
                }
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(exception.message ?: "Unable to update job seeker profile.")
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
