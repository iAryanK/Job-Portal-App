package com.aryan.jobportalapp.presentation.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.CreateJobSeekerRequest
import com.aryan.jobportalapp.data.remote.dto.Gender
import com.aryan.jobportalapp.data.remote.dto.PatchJobSeekerRequest
import com.aryan.jobportalapp.data.repository.AuthRepository
import com.aryan.jobportalapp.data.repository.JobSeekerRepository
import kotlinx.coroutines.launch

data class JobSeekerOnboardingUiState(
    val step: Int = 1,
    val profileId: String? = null,
    val isSubmitting: Boolean = false,
    val toastMessage: String? = null,
    val isToastError: Boolean = false,
    val navigateToHome: Boolean = false,
    val name: String = "",
    val gender: Gender? = null,
    val headline: String = "",
    val highestQualification: String = "",
    val specialization: String = "",
    val instituteName: String = "",
    val experienceYears: String = "",
    val currentCompany: String = "",
    val currentDesignation: String = "",
    val preferredJobLocation: String = "",
    val city: String = "",
    val expectedCTC: String = "",
    val summary: String = ""
) {
    val isStep1Valid: Boolean
        get() = name.isNotBlank()
}

class JobSeekerOnboardingViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val jobSeekerRepository: JobSeekerRepository = JobSeekerRepository()
) : ViewModel() {

    var uiState by mutableStateOf(JobSeekerOnboardingUiState())
        private set

    init {
        viewModelScope.launch {
            authRepository.getCurrentUser().onSuccess { user ->
                uiState = uiState.copy(
                    profileId = user.id,
                    name = user.name.orEmpty()
                )
            }
        }
    }

    fun updateName(value: String) {
        uiState = uiState.copy(name = value)
    }

    fun updateGender(value: Gender) {
        uiState = uiState.copy(gender = value)
    }

    fun updateHeadline(value: String) {
        uiState = uiState.copy(headline = value)
    }

    fun updateHighestQualification(value: String) {
        uiState = uiState.copy(highestQualification = value)
    }

    fun updateSpecialization(value: String) {
        uiState = uiState.copy(specialization = value)
    }

    fun updateInstituteName(value: String) {
        uiState = uiState.copy(instituteName = value)
    }

    fun updateExperienceYears(value: String) {
        uiState = uiState.copy(experienceYears = value.filter(Char::isDigit).take(2))
    }

    fun updateCurrentCompany(value: String) {
        uiState = uiState.copy(currentCompany = value)
    }

    fun updateCurrentDesignation(value: String) {
        uiState = uiState.copy(currentDesignation = value)
    }

    fun updatePreferredJobLocation(value: String) {
        uiState = uiState.copy(preferredJobLocation = value)
    }

    fun updateCity(value: String) {
        uiState = uiState.copy(city = value)
    }

    fun updateExpectedCTC(value: String) {
        uiState = uiState.copy(expectedCTC = value)
    }

    fun updateSummary(value: String) {
        uiState = uiState.copy(summary = value)
    }

    fun submitStep1() {
        if (!uiState.isStep1Valid || uiState.isSubmitting) return

        viewModelScope.launch {
            uiState = uiState.copy(isSubmitting = true, toastMessage = null)

            val request = CreateJobSeekerRequest(
                name = uiState.name.trim(),
                gender = uiState.gender,
                headline = uiState.headline.trim().ifBlank { null }
            )

            jobSeekerRepository.createJobSeeker(request)
                .onSuccess { jobSeeker ->
                    uiState = uiState.copy(
                        isSubmitting = false,
                        step = 2,
                        profileId = jobSeeker.id ?: uiState.profileId
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isSubmitting = false,
                        toastMessage = error.message ?: "Failed to create job seeker profile",
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

            val request = PatchJobSeekerRequest(
                highestQualification = uiState.highestQualification.trim().ifBlank { null },
                specialization = uiState.specialization.trim().ifBlank { null },
                instituteName = uiState.instituteName.trim().ifBlank { null },
                experienceYears = uiState.experienceYears.toIntOrNull(),
                currentCompany = uiState.currentCompany.trim().ifBlank { null },
                currentDesignation = uiState.currentDesignation.trim().ifBlank { null },
                preferredJobLocation = uiState.preferredJobLocation.trim().ifBlank { null },
                city = uiState.city.trim().ifBlank { null },
                expectedCTC = uiState.expectedCTC.trim().ifBlank { null },
                summary = uiState.summary.trim().ifBlank { null }
            )

            jobSeekerRepository.patchJobSeeker(profileId, request)
                .onSuccess {
                    uiState = uiState.copy(
                        isSubmitting = false,
                        navigateToHome = true
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isSubmitting = false,
                        toastMessage = error.message ?: "Failed to update job seeker profile",
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
