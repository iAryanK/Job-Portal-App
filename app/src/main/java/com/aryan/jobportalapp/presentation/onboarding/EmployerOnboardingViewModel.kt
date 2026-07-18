package com.aryan.jobportalapp.presentation.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.CreateEmployerRequest
import com.aryan.jobportalapp.data.remote.dto.PatchEmployerRequest
import com.aryan.jobportalapp.data.repository.AuthRepository
import com.aryan.jobportalapp.data.repository.EmployerRepository
import kotlinx.coroutines.launch

data class EmployerOnboardingUiState(
    val step: Int = 1,
    val profileId: String? = null,
    val isSubmitting: Boolean = false,
    val toastMessage: String? = null,
    val isToastError: Boolean = false,
    val navigateToHome: Boolean = false,
    val companyName: String = "",
    val industry: String = "",
    val email: String = "",
    val phone: String = "",
    val city: String = "",
    val website: String = "",
    val tagline: String = "",
    val description: String = "",
    val companySize: String = "",
    val establishedYear: String = "",
    val addressLine1: String = "",
    val state: String = "",
    val country: String = "",
    val pinCode: String = ""
) {
    val isStep1Valid: Boolean
        get() = companyName.isNotBlank() &&
            industry.isNotBlank() &&
            email.isNotBlank() &&
            phone.isNotBlank() &&
            city.isNotBlank()
}

class EmployerOnboardingViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val employerRepository: EmployerRepository = EmployerRepository()
) : ViewModel() {

    var uiState by mutableStateOf(EmployerOnboardingUiState())
        private set

    init {
        viewModelScope.launch {
            authRepository.getCurrentUser().onSuccess { user ->
                uiState = uiState.copy(
                    profileId = user.id,
                    email = user.email.orEmpty()
                )
            }
        }
    }

    fun updateCompanyName(value: String) {
        uiState = uiState.copy(companyName = value)
    }

    fun updateIndustry(value: String) {
        uiState = uiState.copy(industry = value)
    }

    fun updateEmail(value: String) {
        uiState = uiState.copy(email = value)
    }

    fun updatePhone(value: String) {
        uiState = uiState.copy(phone = value)
    }

    fun updateCity(value: String) {
        uiState = uiState.copy(city = value)
    }

    fun updateWebsite(value: String) {
        uiState = uiState.copy(website = value)
    }

    fun updateTagline(value: String) {
        uiState = uiState.copy(tagline = value)
    }

    fun updateDescription(value: String) {
        uiState = uiState.copy(description = value)
    }

    fun updateCompanySize(value: String) {
        uiState = uiState.copy(companySize = value)
    }

    fun updateEstablishedYear(value: String) {
        uiState = uiState.copy(establishedYear = value.filter(Char::isDigit).take(4))
    }

    fun updateAddressLine1(value: String) {
        uiState = uiState.copy(addressLine1 = value)
    }

    fun updateState(value: String) {
        uiState = uiState.copy(state = value)
    }

    fun updateCountry(value: String) {
        uiState = uiState.copy(country = value)
    }

    fun updatePinCode(value: String) {
        uiState = uiState.copy(pinCode = value)
    }

    fun submitStep1() {
        if (!uiState.isStep1Valid || uiState.isSubmitting) return

        viewModelScope.launch {
            uiState = uiState.copy(isSubmitting = true, toastMessage = null)

            val request = CreateEmployerRequest(
                companyName = uiState.companyName.trim(),
                industry = uiState.industry.trim().ifBlank { null },
                email = uiState.email.trim().ifBlank { null },
                phone = uiState.phone.trim().ifBlank { null },
                city = uiState.city.trim().ifBlank { null }
            )

            employerRepository.createEmployer(request)
                .onSuccess { employer ->
                    uiState = uiState.copy(
                        isSubmitting = false,
                        step = 2,
                        profileId = employer.id ?: uiState.profileId
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isSubmitting = false,
                        toastMessage = error.message ?: "Failed to create employer profile",
                        isToastError = true
                    )
                }
        }
    }

    fun submitStep2() {
        val profileId = uiState.profileId
        if (profileId.isNullOrBlank() || uiState.isSubmitting) return

        viewModelScope.launch {
            uiState = uiState.copy(isSubmitting = true, toastMessage = null)

            val request = PatchEmployerRequest(
                website = uiState.website.trim().ifBlank { null },
                tagline = uiState.tagline.trim().ifBlank { null },
                description = uiState.description.trim().ifBlank { null },
                companySize = uiState.companySize.trim().ifBlank { null },
                establishedYear = uiState.establishedYear.toIntOrNull(),
                addressLine1 = uiState.addressLine1.trim().ifBlank { null },
                state = uiState.state.trim().ifBlank { null },
                country = uiState.country.trim().ifBlank { null },
                pinCode = uiState.pinCode.trim().ifBlank { null }
            )

            employerRepository.patchEmployer(profileId, request)
                .onSuccess {
                    uiState = uiState.copy(
                        isSubmitting = false,
                        navigateToHome = true
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isSubmitting = false,
                        toastMessage = error.message ?: "Failed to update employer profile",
                        isToastError = true
                    )
                }
        }
    }

    fun skipOptionalStep() {
        uiState = uiState.copy(navigateToHome = true)
    }

    fun consumeToast() {
        uiState = uiState.copy(toastMessage = null)
    }

    fun consumeNavigation() {
        uiState = uiState.copy(navigateToHome = false)
    }
}
