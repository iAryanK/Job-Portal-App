package com.aryan.jobportalapp.presentation.role

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.AppUserRole
import com.aryan.jobportalapp.data.repository.AuthRepository
import com.aryan.jobportalapp.domain.PostAuthDestination
import com.aryan.jobportalapp.domain.PostAuthRouter
import kotlinx.coroutines.launch

data class RoleSelectionUiState(
    val isUpdating: Boolean = false,
    val toastMessage: String? = null,
    val isToastError: Boolean = false,
    val destination: PostAuthDestination? = null
)

class RoleSelectionViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val postAuthRouter: PostAuthRouter = PostAuthRouter()
) : ViewModel() {

    var uiState by mutableStateOf(RoleSelectionUiState())
        private set

    fun selectRole(role: AppUserRole) {
        if (uiState.isUpdating) return

        viewModelScope.launch {
            uiState = uiState.copy(
                isUpdating = true,
                toastMessage = null,
                destination = null
            )

            authRepository.updateRole(role)
                .onSuccess {
                    resolveDestinationAfterRoleUpdate()
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

    private suspend fun resolveDestinationAfterRoleUpdate() {
        postAuthRouter.resolveFromCurrentUser()
            .onSuccess { destination ->
                uiState = uiState.copy(
                    isUpdating = false,
                    destination = destination
                )
            }
            .onFailure { error ->
                uiState = uiState.copy(
                    isUpdating = false,
                    toastMessage = error.message ?: "Failed to check profile",
                    isToastError = true
                )
            }
    }

    fun consumeToast() {
        uiState = uiState.copy(toastMessage = null)
    }

    fun consumeDestination() {
        uiState = uiState.copy(destination = null)
    }
}
