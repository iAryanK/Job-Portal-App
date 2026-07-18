package com.aryan.jobportalapp.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.AppUserResponse
import com.aryan.jobportalapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

data class MainUiState(
    val user: AppUserResponse? = null,
    val isLoadingUser: Boolean = false
)

class MainViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf(MainUiState())
        private set

    init {
        refreshUser()
    }

    fun refreshUser() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingUser = true)
            authRepository.getCurrentUser()
                .onSuccess { user ->
                    uiState = uiState.copy(user = user, isLoadingUser = false)
                }
                .onFailure {
                    uiState = uiState.copy(isLoadingUser = false)
                }
        }
    }
}
