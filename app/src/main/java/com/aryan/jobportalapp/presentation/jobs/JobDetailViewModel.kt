package com.aryan.jobportalapp.presentation.jobs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.AppUserRole
import com.aryan.jobportalapp.data.remote.dto.JobResponse
import com.aryan.jobportalapp.data.repository.AuthRepository
import com.aryan.jobportalapp.data.repository.JobRepository
import com.aryan.jobportalapp.data.repository.RecruitmentRepository
import kotlinx.coroutines.launch

data class JobDetailUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val job: JobResponse? = null,
    val errorMessage: String? = null,
    val userRole: AppUserRole? = null,
    val currentUserId: String? = null,
    val hasApplied: Boolean = false,
    val isApplying: Boolean = false,
    val toastMessage: String? = null
) {
    val showApplyButton: Boolean
        get() = userRole == AppUserRole.JOBSEEKER && job != null

    val showEditButton: Boolean
        get() = userRole == AppUserRole.EMPLOYER && job?.id != null
}

class JobDetailViewModel(
    private val jobId: String,
    private val jobRepository: JobRepository = JobRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
    private val recruitmentRepository: RecruitmentRepository = RecruitmentRepository()
) : ViewModel() {

    var uiState by mutableStateOf(JobDetailUiState())
        private set

    init {
        loadJob()
    }

    fun loadJob(isRefresh: Boolean = false) {
        if (jobId.isBlank()) {
            uiState = uiState.copy(
                isLoading = false,
                isRefreshing = false,
                errorMessage = "Job ID is missing"
            )
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = !isRefresh && uiState.job == null,
                isRefreshing = isRefresh,
                errorMessage = null
            )

            val userResult = authRepository.getCurrentUser()
            val user = userResult.getOrNull()
            val userRole = user?.role
            val userId = user?.id

            jobRepository.getJobById(jobId)
                .onSuccess { job ->
                    var hasApplied = false
                    if (userRole == AppUserRole.JOBSEEKER && !userId.isNullOrBlank()) {
                        recruitmentRepository.getApplicationsOfJobSeeker(userId)
                            .onSuccess { applications ->
                                hasApplied = applications.any { application ->
                                    application.job?.id == jobId
                                }
                            }
                    }

                    uiState = uiState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        job = job,
                        userRole = userRole,
                        currentUserId = userId,
                        hasApplied = hasApplied,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        userRole = userRole,
                        currentUserId = userId,
                        errorMessage = error.message ?: "Unable to load job details"
                    )
                }
        }
    }

    fun apply(message: String? = null) {
        val userId = uiState.currentUserId
        if (userId.isNullOrBlank()) {
            uiState = uiState.copy(toastMessage = "Unable to apply. Please sign in again.")
            return
        }
        if (uiState.hasApplied || uiState.isApplying) return

        viewModelScope.launch {
            uiState = uiState.copy(isApplying = true)

            recruitmentRepository.applyToJob(
                jobId = jobId,
                jobSeekerId = userId,
                message = message
            )
                .onSuccess {
                    uiState = uiState.copy(
                        isApplying = false,
                        hasApplied = true,
                        toastMessage = "Application submitted successfully"
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isApplying = false,
                        toastMessage = error.message ?: "Unable to apply for this job"
                    )
                }
        }
    }

    fun consumeToast() {
        uiState = uiState.copy(toastMessage = null)
    }
}
