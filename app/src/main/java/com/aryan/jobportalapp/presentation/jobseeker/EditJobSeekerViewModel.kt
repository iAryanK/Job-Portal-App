package com.aryan.jobportalapp.presentation.jobseeker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.Gender
import com.aryan.jobportalapp.data.remote.dto.PatchJobSeekerRequest
import com.aryan.jobportalapp.data.repository.AuthRepository
import com.aryan.jobportalapp.data.repository.JobSeekerRepository
import kotlinx.coroutines.launch

data class EditJobSeekerUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val profileId: String? = null,
    val errorMessage: String? = null,
    val toastMessage: String? = null,
    val navigateBack: Boolean = false,
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
    val summary: String = "",
    val portfolioUrl: String = "",
    val resumeUrl: String = ""
) {
    val isValid: Boolean
        get() = name.isNotBlank()
}

class EditJobSeekerViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val jobSeekerRepository: JobSeekerRepository = JobSeekerRepository()
) : ViewModel() {

    var uiState by mutableStateOf(EditJobSeekerUiState())
        private set

    init {
        loadJobSeeker()
    }

    fun loadJobSeeker() {
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

            jobSeekerRepository.getJobSeekerById(userId)
                .onSuccess { jobSeeker ->
                    uiState = uiState.copy(
                        isLoading = false,
                        profileId = jobSeeker.id ?: userId,
                        name = jobSeeker.name.orEmpty(),
                        gender = jobSeeker.gender,
                        headline = jobSeeker.headline.orEmpty(),
                        highestQualification = jobSeeker.highestQualification.orEmpty(),
                        specialization = jobSeeker.specialization.orEmpty(),
                        instituteName = jobSeeker.instituteName.orEmpty(),
                        experienceYears = jobSeeker.experienceYears?.toString().orEmpty(),
                        currentCompany = jobSeeker.currentCompany.orEmpty(),
                        currentDesignation = jobSeeker.currentDesignation.orEmpty(),
                        preferredJobLocation = jobSeeker.preferredJobLocation.orEmpty(),
                        city = jobSeeker.city.orEmpty(),
                        expectedCTC = jobSeeker.expectedCTC.orEmpty(),
                        summary = jobSeeker.summary.orEmpty(),
                        portfolioUrl = jobSeeker.portfolioUrl.orEmpty(),
                        resumeUrl = jobSeeker.resumeUrl.orEmpty(),
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

    fun updatePortfolioUrl(value: String) {
        uiState = uiState.copy(portfolioUrl = value)
    }

    fun updateResumeUrl(value: String) {
        uiState = uiState.copy(resumeUrl = value)
    }

    fun save() {
        val profileId = uiState.profileId
        if (!uiState.isValid || profileId.isNullOrBlank() || uiState.isSaving) return

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, toastMessage = null)

            val request = PatchJobSeekerRequest(
                name = uiState.name.trim(),
                gender = uiState.gender,
                headline = uiState.headline.trim().ifBlank { null },
                highestQualification = uiState.highestQualification.trim().ifBlank { null },
                specialization = uiState.specialization.trim().ifBlank { null },
                instituteName = uiState.instituteName.trim().ifBlank { null },
                experienceYears = uiState.experienceYears.toIntOrNull(),
                currentCompany = uiState.currentCompany.trim().ifBlank { null },
                currentDesignation = uiState.currentDesignation.trim().ifBlank { null },
                preferredJobLocation = uiState.preferredJobLocation.trim().ifBlank { null },
                city = uiState.city.trim().ifBlank { null },
                expectedCTC = uiState.expectedCTC.trim().ifBlank { null },
                summary = uiState.summary.trim().ifBlank { null },
                portfolioUrl = uiState.portfolioUrl.trim().ifBlank { null },
                resumeUrl = uiState.resumeUrl.trim().ifBlank { null }
            )

            jobSeekerRepository.patchJobSeeker(profileId, request)
                .onSuccess {
                    uiState = uiState.copy(
                        isSaving = false,
                        navigateBack = true
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isSaving = false,
                        toastMessage = error.message ?: "Failed to update profile"
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
