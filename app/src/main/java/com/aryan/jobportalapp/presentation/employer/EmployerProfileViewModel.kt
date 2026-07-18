package com.aryan.jobportalapp.presentation.employer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.EmployerResponse
import com.aryan.jobportalapp.data.repository.AuthRepository
import com.aryan.jobportalapp.data.repository.EmployerRepository
import kotlinx.coroutines.launch

data class EmployerProfileUiState(
    val isLoading: Boolean = true,
    val employer: EmployerResponse? = null,
    val currentUserId: String? = null,
    val errorMessage: String? = null
) {
    val isOwnProfile: Boolean
        get() = !currentUserId.isNullOrBlank() && currentUserId == employer?.id
}

class EmployerProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val employerRepository: EmployerRepository = EmployerRepository()
) : ViewModel() {

    var uiState by mutableStateOf(EmployerProfileUiState())
        private set

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val isInitialLoad = uiState.employer == null
            uiState = uiState.copy(
                isLoading = true,
                errorMessage = if (isInitialLoad) null else uiState.errorMessage
            )

            val userResult = authRepository.getCurrentUser()
            val userId = userResult.getOrNull()?.id
            if (userId.isNullOrBlank()) {
                uiState = uiState.copy(
                    isLoading = false,
                    currentUserId = null,
                    errorMessage = userResult.exceptionOrNull()?.message
                        ?: "Unable to load user profile"
                )
                return@launch
            }

            employerRepository.getEmployerById(userId)
                .onSuccess { employer ->
                    uiState = uiState.copy(
                        isLoading = false,
                        employer = employer,
                        currentUserId = userId,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to load company profile"
                    )
                }
        }
    }
}
