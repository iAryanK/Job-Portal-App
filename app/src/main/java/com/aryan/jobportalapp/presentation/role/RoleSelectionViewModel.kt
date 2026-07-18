package com.aryan.jobportalapp.presentation.role

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.AppUserRole
import com.aryan.jobportalapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

data class RoleSelectionUiState(
    val isUpdating: Boolean = false,
    val toastMessage: String? = null,
    val isToastError: Boolean = false,
    val navigateToHome: Boolean = false
)

class RoleSelectionViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf(RoleSelectionUiState())
        private set

    fun selectRole(role: AppUserRole) {
        if (uiState.isUpdating) return

        viewModelScope.launch {
            uiState = uiState.copy(
                isUpdating = true,
                toastMessage = null,
                navigateToHome = false
            )

            authRepository.updateRole(role)
                .onSuccess {
                    uiState = uiState.copy(
                        isUpdating = false,
                        navigateToHome = true
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isUpdating = false,
                        toastMessage = error.message ?: "Failed to update role",
                        isToastError = true
                    )
                }
        }
    }

    fun consumeToast() {
        uiState = uiState.copy(toastMessage = null)
    }

    fun consumeNavigation() {
        uiState = uiState.copy(navigateToHome = false)
    }
}
