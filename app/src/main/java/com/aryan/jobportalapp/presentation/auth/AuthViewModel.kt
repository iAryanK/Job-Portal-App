package com.aryan.jobportalapp.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.AppUserRole
import com.aryan.jobportalapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

enum class AuthDestination {
    RoleSelection,
    Home
}

data class AuthUiState(
    val isSubmitting: Boolean = false,
    val isVerifying: Boolean = false,
    val showOtpDialog: Boolean = false,
    val pendingEmail: String = "",
    val toastMessage: String? = null,
    val isToastError: Boolean = false,
    val destination: AuthDestination? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun sendOtp(email: String) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isSubmitting = true,
                pendingEmail = email,
                toastMessage = null
            )

            authRepository.sendOtp(email)
                .onSuccess {
                    uiState = uiState.copy(
                        isSubmitting = false,
                        showOtpDialog = true
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isSubmitting = false,
                        toastMessage = error.message ?: "Failed to send OTP",
                        isToastError = true
                    )
                }
        }
    }

    fun verifyOtp(email: String, otp: String) {
        viewModelScope.launch {
            uiState = uiState.copy(
                isVerifying = true,
                toastMessage = null,
                destination = null
            )

            authRepository.verifyOtp(email, otp)
                .onSuccess {
                    loadCurrentUserAfterVerification()
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isVerifying = false,
                        toastMessage = error.message ?: "OTP verification failed",
                        isToastError = true
                    )
                }
        }
    }

    private suspend fun loadCurrentUserAfterVerification() {
        authRepository.getCurrentUser()
            .onSuccess { user ->
                val destination = if (user.role == AppUserRole.USER) {
                    AuthDestination.RoleSelection
                } else {
                    AuthDestination.Home
                }
                uiState = uiState.copy(
                    isVerifying = false,
                    showOtpDialog = false,
                    toastMessage = "OTP verified successfully",
                    isToastError = false,
                    destination = destination
                )
            }
            .onFailure { error ->
                uiState = uiState.copy(
                    isVerifying = false,
                    toastMessage = error.message ?: "Failed to load user profile",
                    isToastError = true
                )
            }
    }

    fun dismissOtpDialog() {
        uiState = uiState.copy(showOtpDialog = false)
    }

    fun consumeToast() {
        uiState = uiState.copy(toastMessage = null)
    }

    fun consumeDestination() {
        uiState = uiState.copy(destination = null)
    }
}
