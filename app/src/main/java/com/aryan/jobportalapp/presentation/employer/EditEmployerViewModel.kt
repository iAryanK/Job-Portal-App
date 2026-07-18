package com.aryan.jobportalapp.presentation.employer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.PatchEmployerRequest
import com.aryan.jobportalapp.data.repository.AuthRepository
import com.aryan.jobportalapp.data.repository.EmployerRepository
import kotlinx.coroutines.launch

data class EditEmployerUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val profileId: String? = null,
    val errorMessage: String? = null,
    val toastMessage: String? = null,
    val navigateBack: Boolean = false,
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
    val isValid: Boolean
        get() = companyName.isNotBlank() &&
            industry.isNotBlank() &&
            email.isNotBlank() &&
            phone.isNotBlank() &&
            city.isNotBlank()
}

class EditEmployerViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val employerRepository: EmployerRepository = EmployerRepository()
) : ViewModel() {

    var uiState by mutableStateOf(EditEmployerUiState())
        private set

    init {
        loadEmployer()
    }

    fun loadEmployer() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            val userId = authRepository.getCurrentUser().getOrNull()?.id
            if (userId.isNullOrBlank()) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Unable to load user profile"
                )
                return@launch
            }

            employerRepository.getEmployerById(userId)
                .onSuccess { employer ->
                    uiState = uiState.copy(
                        isLoading = false,
                        profileId = employer.id ?: userId,
                        companyName = employer.companyName.orEmpty(),
                        industry = employer.industry.orEmpty(),
                        email = employer.email.orEmpty(),
                        phone = employer.phone.orEmpty(),
                        city = employer.city.orEmpty(),
                        website = employer.website.orEmpty(),
                        tagline = employer.tagline.orEmpty(),
                        description = employer.description.orEmpty(),
                        companySize = employer.companySize.orEmpty(),
                        establishedYear = employer.establishedYear?.toString().orEmpty(),
                        addressLine1 = employer.addressLine1.orEmpty(),
                        state = employer.state.orEmpty(),
                        country = employer.country.orEmpty(),
                        pinCode = employer.pinCode.orEmpty(),
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

    fun save() {
        val profileId = uiState.profileId
        if (!uiState.isValid || profileId.isNullOrBlank() || uiState.isSaving) return

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, toastMessage = null)

            val request = PatchEmployerRequest(
                companyName = uiState.companyName.trim(),
                industry = uiState.industry.trim().ifBlank { null },
                email = uiState.email.trim().ifBlank { null },
                phone = uiState.phone.trim().ifBlank { null },
                city = uiState.city.trim().ifBlank { null },
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
                        isSaving = false,
                        navigateBack = true
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isSaving = false,
                        toastMessage = error.message ?: "Failed to update company profile"
                    )
                }
        }
    }

    fun consumeToast() {
        uiState = uiState.copy(toastMessage = null)
    }

    fun consumeNavigation() {
        uiState = uiState.copy(navigateBack = false)
    }
}
