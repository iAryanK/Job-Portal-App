package com.aryan.jobportalapp.presentation.jobs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.JobResponse
import com.aryan.jobportalapp.data.repository.JobRepository
import kotlinx.coroutines.launch

data class JobDetailUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val job: JobResponse? = null,
    val errorMessage: String? = null
)

class JobDetailViewModel(
    private val jobId: String,
    private val jobRepository: JobRepository = JobRepository()
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

            jobRepository.getJobById(jobId)
                .onSuccess { job ->
                    uiState = uiState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        job = job,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = error.message ?: "Unable to load job details"
                    )
                }
        }
    }
}
