package com.aryan.jobportalapp.presentation.employer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.JobSeekerResponse
import com.aryan.jobportalapp.data.repository.JobSeekerRepository
import kotlinx.coroutines.launch

data class JobSeekerDetailUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val jobSeeker: JobSeekerResponse? = null,
    val errorMessage: String? = null
)

class JobSeekerDetailViewModel(
    private val jobSeekerId: String,
    private val jobSeekerRepository: JobSeekerRepository = JobSeekerRepository()
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

            jobSeekerRepository.getJobSeekerById(jobSeekerId)
                .onSuccess { jobSeeker ->
                    uiState = uiState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        jobSeeker = jobSeeker,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = error.message ?: "Unable to load candidate"
                    )
                }
        }
    }
}
