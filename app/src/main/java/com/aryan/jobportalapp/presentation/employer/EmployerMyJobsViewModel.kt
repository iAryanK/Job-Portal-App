package com.aryan.jobportalapp.presentation.employer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.JobSummaryResponse
import com.aryan.jobportalapp.data.repository.AuthRepository
import com.aryan.jobportalapp.data.repository.JobRepository
import kotlinx.coroutines.launch

data class EmployerMyJobsUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val jobs: List<JobSummaryResponse> = emptyList(),
    val errorMessage: String? = null
)

class EmployerMyJobsViewModel(
    private val jobRepository: JobRepository = JobRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf(EmployerMyJobsUiState())
        private set

    fun loadJobs(isRefresh: Boolean = false) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = !isRefresh && uiState.jobs.isEmpty(),
                isRefreshing = isRefresh,
                errorMessage = null
            )

            val employerId = authRepository.getCurrentUser().getOrNull()?.id
            if (employerId.isNullOrBlank()) {
                uiState = uiState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = "Unable to load employer profile"
                )
                return@launch
            }

            jobRepository.findJobs(employerId = employerId)
                .onSuccess { jobs ->
                    uiState = uiState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        jobs = jobs,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = error.message ?: "Unable to load your jobs"
                    )
                }
        }
    }
}
