package com.aryan.jobportalapp.presentation.employer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.AppUserRole
import com.aryan.jobportalapp.data.remote.dto.ApplicationStatus
import com.aryan.jobportalapp.data.remote.dto.JobSeekerResponse
import com.aryan.jobportalapp.data.remote.dto.JobSummaryResponse
import com.aryan.jobportalapp.data.remote.dto.RecruitApplicationItem
import com.aryan.jobportalapp.data.repository.AuthRepository
import com.aryan.jobportalapp.data.repository.JobRepository
import com.aryan.jobportalapp.data.repository.JobSeekerRepository
import com.aryan.jobportalapp.data.repository.RecruitmentRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

data class JobSeekerDetailUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val jobSeeker: JobSeekerResponse? = null,
    val errorMessage: String? = null,
    val userRole: AppUserRole? = null,
    val employerId: String? = null,
    val employerJobs: List<JobSummaryResponse> = emptyList(),
    val isLoadingJobs: Boolean = false,
    val showInviteDialog: Boolean = false,
    val showApplicationsSheet: Boolean = false,
    val isInviting: Boolean = false,
    val activeInvitedJobIds: Set<String> = emptySet(),
    val applications: List<RecruitApplicationItem> = emptyList(),
    val pendingApplicationActions: Map<String, ApplicationPendingAction> = emptyMap(),
    val toastMessage: String? = null
) {
    val showEmployerActions: Boolean
        get() = userRole == AppUserRole.EMPLOYER && jobSeeker != null

    val appliedJobIds: Set<String>
        get() = applications.map { it.jobId }.toSet()

    val hasApplications: Boolean
        get() = applications.isNotEmpty()
}

class JobSeekerDetailViewModel(
    private val jobSeekerId: String,
    private val jobSeekerRepository: JobSeekerRepository = JobSeekerRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
    private val jobRepository: JobRepository = JobRepository(),
    private val recruitmentRepository: RecruitmentRepository = RecruitmentRepository()
) : ViewModel() {

    var uiState by mutableStateOf(JobSeekerDetailUiState())
        private set

    init {
        loadJobSeeker()
    }

    fun loadJobSeeker(isRefresh: Boolean = false) {
        if (jobSeekerId.isBlank()) {
            uiState = uiState.copy(
                isLoading = false,
                isRefreshing = false,
                errorMessage = "Candidate ID is missing"
            )
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = !isRefresh && uiState.jobSeeker == null,
                isRefreshing = isRefresh,
                errorMessage = null
            )

            val userResult = authRepository.getCurrentUser()
            val user = userResult.getOrNull()
            val userRole = user?.role
            val employerId = user?.id?.takeIf { userRole == AppUserRole.EMPLOYER }

            jobSeekerRepository.getJobSeekerById(jobSeekerId)
                .onSuccess { jobSeeker ->
                    uiState = uiState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        jobSeeker = jobSeeker,
                        userRole = userRole,
                        employerId = employerId,
                        errorMessage = null
                    )
                    if (userRole == AppUserRole.EMPLOYER && !employerId.isNullOrBlank()) {
                        loadEmployerJobs(employerId, isRefresh)
                    }
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        userRole = userRole,
                        employerId = employerId,
                        errorMessage = error.message ?: "Unable to load candidate"
                    )
                }
        }
    }

    fun openInviteDialog() {
        uiState = uiState.copy(showInviteDialog = true)
    }

    fun dismissInviteDialog() {
        uiState = uiState.copy(showInviteDialog = false)
    }

    fun openApplicationsSheet() {
        if (!uiState.hasApplications) return
        uiState = uiState.copy(showApplicationsSheet = true)
    }

    fun dismissApplicationsSheet() {
        uiState = uiState.copy(showApplicationsSheet = false)
    }

    fun inviteToJob(jobId: String) {
        val employerId = uiState.employerId
        if (employerId.isNullOrBlank() || uiState.isInviting) return
        if (jobId in uiState.activeInvitedJobIds) {
            uiState = uiState.copy(toastMessage = "Invitation already sent for this job")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isInviting = true)

            val expiresAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
                .format(Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)))

            recruitmentRepository.inviteJobSeeker(
                jobId = jobId,
                employerId = employerId,
                jobSeekerId = jobSeekerId,
                expiresAt = expiresAt
            )
                .onSuccess {
                    uiState = uiState.copy(
                        isInviting = false,
                        showInviteDialog = false,
                        activeInvitedJobIds = uiState.activeInvitedJobIds + jobId,
                        toastMessage = "Invitation sent successfully"
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isInviting = false,
                        toastMessage = error.message ?: "Unable to send invitation"
                    )
                }
        }
    }

    fun acceptApplication(applicationId: String) {
        updateApplicationStatus(
            applicationId = applicationId,
            newStatus = ApplicationStatus.SHORTLISTED,
            remarks = "Application accepted",
            pendingAction = ApplicationPendingAction.ACCEPT
        )
    }

    fun rejectApplication(applicationId: String) {
        updateApplicationStatus(
            applicationId = applicationId,
            newStatus = ApplicationStatus.REJECTED,
            remarks = "Application rejected",
            pendingAction = ApplicationPendingAction.REJECT
        )
    }

    fun consumeToast() {
        uiState = uiState.copy(toastMessage = null)
    }

    private fun updateApplicationStatus(
        applicationId: String,
        newStatus: ApplicationStatus,
        remarks: String,
        pendingAction: ApplicationPendingAction
    ) {
        if (applicationId in uiState.pendingApplicationActions) return

        viewModelScope.launch {
            uiState = uiState.copy(
                pendingApplicationActions = uiState.pendingApplicationActions + (applicationId to pendingAction)
            )

            recruitmentRepository.updateApplicationStatus(
                applicationId = applicationId,
                newStatus = newStatus,
                remarks = remarks
            )
                .onSuccess {
                    uiState = uiState.copy(
                        pendingApplicationActions = uiState.pendingApplicationActions - applicationId,
                        applications = uiState.applications.map { application ->
                            if (application.applicationId == applicationId) {
                                application.copy(status = newStatus)
                            } else {
                                application
                            }
                        },
                        toastMessage = when (newStatus) {
                            ApplicationStatus.SHORTLISTED -> "Application accepted"
                            ApplicationStatus.REJECTED -> "Application rejected"
                            else -> "Application updated"
                        }
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        pendingApplicationActions = uiState.pendingApplicationActions - applicationId,
                        toastMessage = error.message ?: "Unable to update application"
                    )
                }
        }
    }

    private fun loadEmployerJobs(employerId: String, isRefresh: Boolean = false) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingJobs = true)

            jobRepository.findJobs(employerId = employerId)
                .onSuccess { jobs ->
                    val jobIds = jobs.mapNotNull { job -> job.id?.takeIf { it.isNotBlank() } }
                    uiState = uiState.copy(
                        employerJobs = jobs,
                        isLoadingJobs = false
                    )
                    loadActiveInvitations(jobIds)
                    loadApplications(jobIds)
                }
                .onFailure {
                    uiState = uiState.copy(isLoadingJobs = false)
                    if (isRefresh) {
                        uiState = uiState.copy(
                            activeInvitedJobIds = emptySet(),
                            applications = emptyList()
                        )
                    }
                }
        }
    }

    private fun loadActiveInvitations(jobIds: List<String>) {
        viewModelScope.launch {
            recruitmentRepository.getActiveInvitedJobIdsForJobSeeker(
                jobSeekerId = jobSeekerId,
                jobIds = jobIds
            ).onSuccess { activeInvitedJobIds ->
                uiState = uiState.copy(activeInvitedJobIds = activeInvitedJobIds)
            }
        }
    }

    private fun loadApplications(jobIds: List<String>) {
        viewModelScope.launch {
            recruitmentRepository.getApplicationsForJobSeekerFromEmployerJobs(
                jobSeekerId = jobSeekerId,
                jobIds = jobIds
            ).onSuccess { applications ->
                uiState = uiState.copy(
                    applications = applications.sortedByDescending { it.appliedAt.orEmpty() }
                )
            }
        }
    }
}
