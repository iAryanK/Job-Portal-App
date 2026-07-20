package com.aryan.jobportalapp.data.repository

import com.aryan.jobportalapp.data.remote.JobApi
import com.aryan.jobportalapp.data.remote.NetworkClient
import com.aryan.jobportalapp.data.remote.dto.ApiResponse
import com.aryan.jobportalapp.data.remote.dto.CreateJobRequest
import com.aryan.jobportalapp.data.remote.dto.FindJobsApiFilter
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
        return findJobs(
            filter = FindJobsApiFilter(employerId = employerId),
            page = 0,
            size = 50
        ).map { it.items }
    }

    suspend fun findJobs(
        filter: FindJobsApiFilter,
        page: Int = 0,
        size: Int = PAGE_SIZE,
        sort: String = "createdAt,desc"
    ): Result<PagedJobsResult> {
        return try {
            val response = jobApi.findJobs(
                search = filter.search,
                employmentType = filter.employmentType,
                workplaceType = filter.workplaceType,
                experienceLevel = filter.experienceLevel,
                currency = filter.currency,
                city = filter.city,
                district = filter.district,
                state = filter.state,
                country = filter.country,
                minSalary = filter.minSalary,
                maxSalary = filter.maxSalary,
                minExperience = filter.minExperience,
                maxExperience = filter.maxExperience,
                salaryNegotiable = filter.salaryNegotiable,
                relocationAvailable = filter.relocationAvailable,
                accommodationProvided = filter.accommodationProvided,
                transportationProvided = filter.transportationProvided,
                foodProvided = filter.foodProvided,
                industry = filter.industry,
                employerId = filter.employerId,
                page = page,
                size = size,
                sort = sort
            )
            if (response.isSuccessful) {
                val pageData = response.body()?.data
                Result.success(
                    PagedJobsResult(
                        items = pageData?.content.orEmpty(),
                        page = pageData?.number ?: page,
                        isLastPage = pageData?.last ?: true,
                        totalElements = pageData?.totalElements ?: 0
                    )
                )
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

data class PagedJobsResult(
    val items: List<JobSummaryResponse>,
    val page: Int,
    val isLastPage: Boolean,
    val totalElements: Long
)

private const val PAGE_SIZE = 20
