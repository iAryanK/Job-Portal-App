package com.aryan.jobportalapp.data.repository

import com.aryan.jobportalapp.data.remote.NetworkClient
import com.aryan.jobportalapp.data.remote.RecruitmentApi
import com.aryan.jobportalapp.data.remote.dto.ApiResponse
import com.aryan.jobportalapp.data.remote.dto.ApplicationRequest
import com.aryan.jobportalapp.data.remote.dto.ApplicationResponse
import com.aryan.jobportalapp.data.remote.dto.ApplicationStatus
import com.aryan.jobportalapp.data.remote.dto.InvitationEmployerRef
import com.aryan.jobportalapp.data.remote.dto.InvitationJobRef
import com.aryan.jobportalapp.data.remote.dto.InvitationRequest
import com.aryan.jobportalapp.data.remote.dto.InvitationResponse
import com.aryan.jobportalapp.data.remote.dto.InvitationStatus
import com.aryan.jobportalapp.data.remote.dto.JobSeekerApplicationItem
import com.aryan.jobportalapp.data.remote.dto.JobSeekerInvitationItem
import com.aryan.jobportalapp.data.remote.dto.RecruitmentJobRef
import com.aryan.jobportalapp.data.remote.dto.RecruitmentJobSeekerRef
import com.aryan.jobportalapp.data.remote.dto.RecruitApplicationItem
import com.aryan.jobportalapp.data.remote.dto.RecruitInvitationItem
import com.aryan.jobportalapp.data.remote.dto.UpdateApplicationRequest
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class RecruitmentRepository(
    private val recruitmentApi: RecruitmentApi = NetworkClient.recruitmentApi,
    private val jobRepository: JobRepository = JobRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) {

    private suspend fun fetchApplicationsForJob(
        jobId: String,
        jobTitle: String
    ): Result<List<RecruitApplicationItem>> {
        return try {
            val response = recruitmentApi.getApplicationsOfJob(jobId)
            if (response.isSuccessful) {
                val items = response.body()?.data.orEmpty().mapNotNull { application ->
                    application.toRecruitApplicationItem(jobId, jobTitle)
                }
                Result.success(items)
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(IOException(exception.message ?: "Unable to load applications"))
        }
    }

    private suspend fun fetchInvitationsForJob(
        jobId: String,
        jobTitle: String
    ): Result<List<RecruitInvitationItem>> {
        return try {
            val response = recruitmentApi.getInvitationsOfJob(jobId)
            if (response.isSuccessful) {
                val items = response.body()?.data.orEmpty().mapNotNull { invitation ->
                    invitation.toRecruitInvitationItem(jobId, jobTitle)
                }
                Result.success(items)
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(IOException(exception.message ?: "Unable to load invitations"))
        }
    }

    suspend fun getEmployerApplications(): Result<List<RecruitApplicationItem>> {
        return fetchForEmployerJobs(::fetchApplicationsForJob)
    }

    suspend fun getEmployerInvitations(): Result<List<RecruitInvitationItem>> {
        return fetchForEmployerJobs(::fetchInvitationsForJob)
    }

    suspend fun getJobSeekerApplications(): Result<List<JobSeekerApplicationItem>> {
        return try {
            val jobSeekerId = authRepository.getCurrentUser().getOrNull()?.id
            if (jobSeekerId.isNullOrBlank()) {
                return Result.failure(IOException("Unable to load job seeker profile"))
            }

            val response = recruitmentApi.getApplicationsOfJobSeeker(jobSeekerId)
            if (response.isSuccessful) {
                val items = response.body()?.data.orEmpty().mapNotNull { it.toJobSeekerApplicationItem() }
                Result.success(items)
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(exception.message ?: "Unable to load applications")
            )
        }
    }

    suspend fun getJobSeekerInvitations(): Result<List<JobSeekerInvitationItem>> {
        return try {
            val response = recruitmentApi.getInvitationsOfJobSeeker()
            if (response.isSuccessful) {
                val items = response.body()?.data.orEmpty().mapNotNull { it.toJobSeekerInvitationItem() }
                Result.success(items)
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(exception.message ?: "Unable to load invitations")
            )
        }
    }

    suspend fun getApplicationsOfJobSeeker(jobSeekerId: String): Result<List<ApplicationResponse>> {
        return try {
            val response = recruitmentApi.getApplicationsOfJobSeeker(jobSeekerId)
            if (response.isSuccessful) {
                Result.success(response.body()?.data.orEmpty())
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(exception.message ?: "Unable to load applications")
            )
        }
    }

    suspend fun applyToJob(
        jobId: String,
        jobSeekerId: String,
        message: String? = null
    ): Result<ApplicationResponse> {
        return try {
            val response = recruitmentApi.createApplication(
                ApplicationRequest(
                    job = RecruitmentJobRef(id = jobId),
                    message = message?.takeIf { it.isNotBlank() },
                    jobSeeker = RecruitmentJobSeekerRef(id = jobSeekerId)
                )
            )
            if (response.isSuccessful) {
                val application = response.body()?.data
                if (application != null) {
                    Result.success(application)
                } else {
                    Result.failure(IOException("Application response was empty"))
                }
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(exception.message ?: "Unable to apply for this job")
            )
        }
    }

    suspend fun updateApplicationStatus(
        applicationId: String,
        newStatus: ApplicationStatus,
        remarks: String? = null
    ): Result<Unit> {
        return try {
            val response = recruitmentApi.updateApplicationStatus(
                applicationId = applicationId,
                request = UpdateApplicationRequest(
                    newStatus = newStatus,
                    remarks = remarks?.takeIf { it.isNotBlank() }
                )
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(exception.message ?: "Unable to update application")
            )
        }
    }

    suspend fun getApplicationsForJobSeekerFromEmployerJobs(
        jobSeekerId: String,
        jobIds: List<String>
    ): Result<List<RecruitApplicationItem>> {
        if (jobIds.isEmpty()) {
            return Result.success(emptyList())
        }

        val applications = mutableListOf<RecruitApplicationItem>()
        var firstError: Throwable? = null

        for (jobId in jobIds) {
            fetchApplicationsForJob(jobId, jobTitle = "")
                .onSuccess { items ->
                    applications.addAll(items.filter { it.jobSeekerId == jobSeekerId })
                }
                .onFailure { error ->
                    if (firstError == null) {
                        firstError = error
                    }
                }
        }

        return if (applications.isEmpty() && firstError != null) {
            Result.failure(firstError!!)
        } else {
            Result.success(applications)
        }
    }

    suspend fun inviteJobSeeker(
        jobId: String,
        employerId: String,
        jobSeekerId: String,
        expiresAt: String
    ): Result<InvitationResponse> {
        return try {
            val response = recruitmentApi.createInvitation(
                InvitationRequest(
                    job = InvitationJobRef(
                        id = jobId,
                        employer = InvitationEmployerRef(id = employerId)
                    ),
                    jobSeeker = RecruitmentJobSeekerRef(id = jobSeekerId),
                    status = InvitationStatus.PENDING,
                    expiresAt = expiresAt
                )
            )
            if (response.isSuccessful) {
                val invitation = response.body()?.data
                if (invitation != null) {
                    Result.success(invitation)
                } else {
                    Result.failure(IOException("Invitation response was empty"))
                }
            } else {
                Result.failure(IOException(parseErrorMessage(response)))
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(exception.message ?: "Unable to send invitation")
            )
        }
    }

    suspend fun getActiveInvitedJobIdsForJobSeeker(
        jobSeekerId: String,
        jobIds: List<String>
    ): Result<Set<String>> {
        if (jobIds.isEmpty()) {
            return Result.success(emptySet())
        }

        val activeInvitedJobIds = mutableSetOf<String>()
        var firstError: Throwable? = null

        for (jobId in jobIds) {
            try {
                val response = recruitmentApi.getInvitationsOfJob(jobId)
                if (response.isSuccessful) {
                    response.body()?.data.orEmpty()
                        .filter { invitation ->
                            invitation.jobSeeker?.id == jobSeekerId &&
                                isInvitationActive(invitation.expiresAt)
                        }
                        .forEach { activeInvitedJobIds.add(jobId) }
                } else if (firstError == null) {
                    firstError = IOException(parseErrorMessage(response))
                }
            } catch (exception: Exception) {
                if (firstError == null) {
                    firstError = IOException(
                        exception.message ?: "Unable to load invitations"
                    )
                }
            }
        }

        return if (activeInvitedJobIds.isEmpty() && firstError != null) {
            Result.failure(firstError!!)
        } else {
            Result.success(activeInvitedJobIds)
        }
    }

    private suspend fun <T> fetchForEmployerJobs(
        fetchForJob: suspend (jobId: String, jobTitle: String) -> Result<List<T>>
    ): Result<List<T>> {
        return try {
            val employerId = authRepository.getCurrentUser().getOrNull()?.id
            if (employerId.isNullOrBlank()) {
                return Result.failure(IOException("Unable to load employer profile"))
            }

            val jobsResult = jobRepository.findJobs(employerId = employerId)
            val jobs = jobsResult.getOrElse { return Result.failure(it) }
            if (jobs.isEmpty()) {
                return Result.success(emptyList())
            }

            val items = mutableListOf<T>()
            var firstError: Throwable? = null

            for (job in jobs) {
                val jobId = job.id?.takeIf { it.isNotBlank() } ?: continue
                val jobTitle = job.title.orEmpty().ifBlank { "Untitled job" }
                fetchForJob(jobId, jobTitle)
                    .onSuccess { items.addAll(it) }
                    .onFailure { error ->
                        if (firstError == null) {
                            firstError = error
                        }
                    }
            }

            if (items.isEmpty() && firstError != null) {
                Result.failure(firstError!!)
            } else {
                Result.success(items)
            }
        } catch (exception: Exception) {
            Result.failure(
                IOException(exception.message ?: "Unable to load recruit data")
            )
        }
    }

    private fun ApplicationResponse.toRecruitApplicationItem(
        jobId: String,
        jobTitle: String
    ): RecruitApplicationItem? {
        val applicationId = id?.takeIf { it.isNotBlank() } ?: return null
        val seeker = jobSeeker ?: return null
        val seekerId = seeker.id?.takeIf { it.isNotBlank() } ?: return null
        val status = status ?: return null

        return RecruitApplicationItem(
            applicationId = applicationId,
            jobId = job?.id?.takeIf { it.isNotBlank() } ?: jobId,
            jobTitle = job?.title?.takeIf { it.isNotBlank() } ?: jobTitle,
            jobSeekerId = seekerId,
            jobSeekerName = seeker.name.orEmpty().ifBlank { "Unnamed candidate" },
            jobSeekerHeadline = seeker.headline,
            city = seeker.city,
            state = seeker.state,
            country = seeker.country,
            experienceYears = seeker.experienceYears,
            experienceMonths = seeker.experienceMonths,
            highestQualification = seeker.highestQualification,
            specialization = seeker.specialization,
            status = status,
            message = message,
            appliedAt = createdAt
        )
    }

    private fun InvitationResponse.toRecruitInvitationItem(
        jobId: String,
        jobTitle: String
    ): RecruitInvitationItem? {
        val invitationId = id?.takeIf { it.isNotBlank() } ?: return null
        val seeker = jobSeeker ?: return null
        val seekerId = seeker.id?.takeIf { it.isNotBlank() } ?: return null
        val status = status ?: return null

        return RecruitInvitationItem(
            invitationId = invitationId,
            jobId = job?.id?.takeIf { it.isNotBlank() } ?: jobId,
            jobTitle = job?.title?.takeIf { it.isNotBlank() } ?: jobTitle,
            jobSeekerId = seekerId,
            jobSeekerName = seeker.name.orEmpty().ifBlank { "Unnamed candidate" },
            jobSeekerHeadline = seeker.headline,
            city = seeker.city,
            state = seeker.state,
            country = seeker.country,
            experienceYears = seeker.experienceYears,
            experienceMonths = seeker.experienceMonths,
            highestQualification = seeker.highestQualification,
            specialization = seeker.specialization,
            status = status,
            expiresAt = expiresAt
        )
    }

    private fun ApplicationResponse.toJobSeekerApplicationItem(): JobSeekerApplicationItem? {
        val applicationId = id?.takeIf { it.isNotBlank() } ?: return null
        val jobSummary = job?.takeIf { !it.id.isNullOrBlank() } ?: return null
        val applicationStatus = status ?: return null

        return JobSeekerApplicationItem(
            applicationId = applicationId,
            status = applicationStatus,
            message = message,
            job = jobSummary
        )
    }

    private fun InvitationResponse.toJobSeekerInvitationItem(): JobSeekerInvitationItem? {
        val invitationId = id?.takeIf { it.isNotBlank() } ?: return null
        val jobSummary = job?.takeIf { !it.id.isNullOrBlank() } ?: return null
        val invitationStatus = status ?: return null

        return JobSeekerInvitationItem(
            invitationId = invitationId,
            status = invitationStatus,
            expiresAt = expiresAt,
            respondedAt = respondedAt,
            job = jobSummary
        )
    }

    private fun isInvitationActive(expiresAt: String?): Boolean {
        if (expiresAt.isNullOrBlank()) return false

        val expiresAtMillis = parseIsoInstantToEpochMillis(expiresAt) ?: return false
        return expiresAtMillis > System.currentTimeMillis()
    }

    private fun parseIsoInstantToEpochMillis(value: String): Long? {
        val normalized = value.trim().let { iso ->
            val dotIndex = iso.indexOf('.')
            if (dotIndex == -1) return@let iso

            val zoneIndex = iso.indexOfAny(charArrayOf('Z', '+', '-'), startIndex = dotIndex + 1)
            if (zoneIndex == -1) return@let iso

            val fraction = iso.substring(dotIndex + 1, zoneIndex)
            if (fraction.length <= 3) iso
            else iso.substring(0, dotIndex + 1) + fraction.take(3) + iso.substring(zoneIndex)
        }

        listOf("yyyy-MM-dd'T'HH:mm:ss.SSSX", "yyyy-MM-dd'T'HH:mm:ssX").forEach { pattern ->
            try {
                return SimpleDateFormat(pattern, Locale.US).parse(normalized)?.time
            } catch (_: ParseException) {
            }
        }
        return null
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
