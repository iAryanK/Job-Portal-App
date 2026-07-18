package com.aryan.jobportalapp.presentation.jobseeker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.JobSeekerResponse
import com.aryan.jobportalapp.data.repository.AuthRepository
import com.aryan.jobportalapp.data.repository.JobSeekerRepository
import kotlinx.coroutines.launch

data class JobSeekerProfileUiState(
    val isLoading: Boolean = true,
    val jobSeeker: JobSeekerResponse? = null,
    val currentUserId: String? = null,
    val profilePhotoUrl: String? = null,
    val errorMessage: String? = null
) {
    val isOwnProfile: Boolean
        get() = !currentUserId.isNullOrBlank() && currentUserId == jobSeeker?.id
}

class JobSeekerProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val jobSeekerRepository: JobSeekerRepository = JobSeekerRepository()
) : ViewModel() {

    var uiState by mutableStateOf(JobSeekerProfileUiState())
        private set

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val isInitialLoad = uiState.jobSeeker == null
            uiState = uiState.copy(
                isLoading = true,
                errorMessage = if (isInitialLoad) null else uiState.errorMessage
            )

            val userResult = authRepository.getCurrentUser()
            val user = userResult.getOrNull()
            val userId = user?.id
            if (userId.isNullOrBlank()) {
                uiState = uiState.copy(
                    isLoading = false,
                    currentUserId = null,
                    errorMessage = userResult.exceptionOrNull()?.message
                        ?: "Unable to load user profile"
                )
                return@launch
            }

            jobSeekerRepository.getJobSeekerById(userId)
                .onSuccess { jobSeeker ->
                    uiState = uiState.copy(
                        isLoading = false,
                        jobSeeker = jobSeeker,
                        currentUserId = userId,
                        profilePhotoUrl = user?.profilePhoto,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to load profile"
                    )
                }
        }
    }
}
