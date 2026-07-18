package com.aryan.jobportalapp.data.repository

import com.aryan.jobportalapp.data.remote.JobApi
import com.aryan.jobportalapp.data.remote.NetworkClient
import com.aryan.jobportalapp.data.remote.dto.ApiResponse
import com.aryan.jobportalapp.data.remote.dto.CreateJobRequest
import com.aryan.jobportalapp.data.remote.dto.JobResponse
import com.aryan.jobportalapp.data.remote.dto.JobSummaryResponse
import com.aryan.jobportalapp.data.remote.dto.PatchJobRequest
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import java.io.IOException

class JobRepository(
    private val jobApi: JobApi = NetworkClient.jobApi
) {

    suspend fun findJobs(employerId: String? = null): Result<List<JobSummaryResponse>> {
        return try {
            val response = jobApi.findJobs(employerId = employerId)
            if (response.isSuccessful) {
                Result.success(response.body()?.data?.content.orEmpty())
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(exception.message ?: "Unable to fetch jobs.")
            )
        }
    }

    suspend fun createJob(request: CreateJobRequest): Result<JobResponse> {
        return try {
            val response = jobApi.createJob(request)
            if (response.isSuccessful) {
                val job = response.body()?.data
                if (job != null) {
                    Result.success(job)
                } else {
                    Result.failure(IOException("Create job response was empty"))
                }
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(exception.message ?: "Unable to create job posting.")
            )
        }
    }

    suspend fun patchJob(id: String, request: PatchJobRequest): Result<Unit> {
        return try {
            val response = jobApi.patchJob(id, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(exception.message ?: "Unable to update job posting.")
            )
        }
    }

    suspend fun getJobById(id: String): Result<JobResponse> {
        return try {
            val response = jobApi.getJobById(id)
            if (response.isSuccessful) {
                val job = response.body()?.data
                if (job != null) {
                    Result.success(job)
                } else {
                    Result.failure(IOException("Job response was empty"))
                }
            } else if (response.code() == 404) {
                Result.failure(JobNotFoundException())
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(exception.message ?: "Unable to fetch job details.")
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

class JobNotFoundException : IOException("Job not found")
