package com.aryan.jobportalapp.presentation.employer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.CreateJobRequest
import com.aryan.jobportalapp.data.remote.dto.CurrencyType
import com.aryan.jobportalapp.data.remote.dto.EmploymentType
import com.aryan.jobportalapp.data.remote.dto.ExperienceLevel
import com.aryan.jobportalapp.data.remote.dto.JobResponse
import com.aryan.jobportalapp.data.remote.dto.JobStatus
import com.aryan.jobportalapp.data.remote.dto.PatchJobRequest
import com.aryan.jobportalapp.data.remote.dto.WorkplaceType
import com.aryan.jobportalapp.data.repository.JobRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

enum class JobFormMode {
    CREATE,
    EDIT
}

data class JobFormUiState(
    val mode: JobFormMode = JobFormMode.CREATE,
    val jobId: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val toastMessage: String? = null,
    val navigateBack: Boolean = false,
    val title: String = "",
    val description: String = "",
    val responsibilities: String = "",
    val requirements: String = "",
    val benefits: String = "",
    val employmentType: EmploymentType? = EmploymentType.FULL_TIME,
    val workplaceType: WorkplaceType? = WorkplaceType.HYBRID,
    val experienceLevel: ExperienceLevel? = ExperienceLevel.MID_LEVEL,
    val status: JobStatus? = JobStatus.PUBLISHED,
    val vacancies: String = "",
    val minimumExperience: String = "",
    val maximumExperience: String = "",
    val minimumSalary: String = "",
    val maximumSalary: String = "",
    val salaryNegotiable: Boolean = false,
    val currency: CurrencyType? = CurrencyType.INR,
    val city: String = "",
    val district: String = "",
    val state: String = "",
    val country: String = "",
    val pinCode: String = "",
    val relocationAvailable: Boolean = false,
    val accommodationProvided: Boolean = false,
    val transportationProvided: Boolean = false,
    val foodProvided: Boolean = false,
    val applicationDeadline: String = "",
    val expiresAt: String = ""
) {
    val isValid: Boolean
        get() = title.isNotBlank() &&
            description.isNotBlank() &&
            employmentType != null &&
            workplaceType != null &&
            experienceLevel != null &&
            city.isNotBlank()

    val screenTitle: String
        get() = when (mode) {
            JobFormMode.CREATE -> "Post a job"
            JobFormMode.EDIT -> "Edit job"
        }

    val submitLabel: String
        get() = when (mode) {
            JobFormMode.CREATE -> "Post job"
            JobFormMode.EDIT -> "Save changes"
        }
}

class CreateEditJobViewModel(
    private val jobId: String? = null,
    private val jobRepository: JobRepository = JobRepository()
) : ViewModel() {

    var uiState by mutableStateOf(
        JobFormUiState(
            mode = if (jobId.isNullOrBlank()) JobFormMode.CREATE else JobFormMode.EDIT,
            jobId = jobId,
            isLoading = !jobId.isNullOrBlank()
        )
    )
        private set

    init {
        if (!jobId.isNullOrBlank()) {
            loadJob(jobId)
        }
    }

    fun loadJob(id: String = jobId.orEmpty()) {
        if (id.isBlank()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            jobRepository.getJobById(id)
                .onSuccess { job -> uiState = uiState.copy(isLoading = false, errorMessage = null).applyJob(job) }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to load job details"
                    )
                }
        }
    }

    fun updateTitle(value: String) {
        uiState = uiState.copy(title = value)
    }

    fun updateDescription(value: String) {
        uiState = uiState.copy(description = value)
    }

    fun updateResponsibilities(value: String) {
        uiState = uiState.copy(responsibilities = value)
    }

    fun updateRequirements(value: String) {
        uiState = uiState.copy(requirements = value)
    }

    fun updateBenefits(value: String) {
        uiState = uiState.copy(benefits = value)
    }

    fun updateEmploymentType(value: EmploymentType) {
        uiState = uiState.copy(employmentType = value)
    }

    fun updateWorkplaceType(value: WorkplaceType) {
        uiState = uiState.copy(workplaceType = value)
    }

    fun updateExperienceLevel(value: ExperienceLevel) {
        uiState = uiState.copy(experienceLevel = value)
    }

    fun updateStatus(value: JobStatus) {
        uiState = uiState.copy(status = value)
    }

    fun updateVacancies(value: String) {
        uiState = uiState.copy(vacancies = value.filter(Char::isDigit))
    }

    fun updateMinimumExperience(value: String) {
        uiState = uiState.copy(minimumExperience = value.filter(Char::isDigit))
    }

    fun updateMaximumExperience(value: String) {
        uiState = uiState.copy(maximumExperience = value.filter(Char::isDigit))
    }

    fun updateMinimumSalary(value: String) {
        uiState = uiState.copy(minimumSalary = value.filter { it.isDigit() || it == '.' })
    }

    fun updateMaximumSalary(value: String) {
        uiState = uiState.copy(maximumSalary = value.filter { it.isDigit() || it == '.' })
    }

    fun updateSalaryNegotiable(value: Boolean) {
        uiState = uiState.copy(salaryNegotiable = value)
    }

    fun updateCurrency(value: CurrencyType) {
        uiState = uiState.copy(currency = value)
    }

    fun updateCity(value: String) {
        uiState = uiState.copy(city = value)
    }

    fun updateDistrict(value: String) {
        uiState = uiState.copy(district = value)
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

    fun updateRelocationAvailable(value: Boolean) {
        uiState = uiState.copy(relocationAvailable = value)
    }

    fun updateAccommodationProvided(value: Boolean) {
        uiState = uiState.copy(accommodationProvided = value)
    }

    fun updateTransportationProvided(value: Boolean) {
        uiState = uiState.copy(transportationProvided = value)
    }

    fun updateFoodProvided(value: Boolean) {
        uiState = uiState.copy(foodProvided = value)
    }

    fun updateApplicationDeadline(value: String) {
        uiState = uiState.copy(applicationDeadline = value)
    }

    fun updateExpiresAt(value: String) {
        uiState = uiState.copy(expiresAt = value)
    }

    fun submit() {
        if (!uiState.isValid || uiState.isSaving) return

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, toastMessage = null)

            when (uiState.mode) {
                JobFormMode.CREATE -> createJob()
                JobFormMode.EDIT -> patchJob()
            }
        }
    }

    fun consumeToast() {
        uiState = uiState.copy(toastMessage = null)
    }

    fun consumeNavigation() {
        uiState = uiState.copy(navigateBack = false)
    }

    private suspend fun createJob() {
        jobRepository.createJob(uiState.toCreateRequest())
            .onSuccess {
                uiState = uiState.copy(isSaving = false, navigateBack = true)
            }
            .onFailure { error ->
                uiState = uiState.copy(
                    isSaving = false,
                    toastMessage = error.message ?: "Failed to create job posting"
                )
            }
    }

    private suspend fun patchJob() {
        val id = uiState.jobId
        if (id.isNullOrBlank()) {
            uiState = uiState.copy(
                isSaving = false,
                toastMessage = "Job ID is missing"
            )
            return
        }

        jobRepository.patchJob(id, uiState.toPatchRequest())
            .onSuccess {
                uiState = uiState.copy(isSaving = false, navigateBack = true)
            }
            .onFailure { error ->
                uiState = uiState.copy(
                    isSaving = false,
                    toastMessage = error.message ?: "Failed to update job posting"
                )
            }
    }

    private fun JobFormUiState.applyJob(job: JobResponse): JobFormUiState {
        return copy(
            jobId = job.id ?: jobId,
            title = job.title.orEmpty(),
            description = job.description.orEmpty(),
            responsibilities = job.responsibilities.orEmpty(),
            requirements = job.requirements.orEmpty(),
            benefits = job.benefits.orEmpty(),
            employmentType = job.employmentType ?: employmentType,
            workplaceType = job.workplaceType ?: workplaceType,
            experienceLevel = job.experienceLevel ?: experienceLevel,
            status = job.status ?: status,
            vacancies = job.vacancies?.toString().orEmpty(),
            minimumExperience = job.minimumExperience?.toString().orEmpty(),
            maximumExperience = job.maximumExperience?.toString().orEmpty(),
            minimumSalary = job.minimumSalary?.toString().orEmpty(),
            maximumSalary = job.maximumSalary?.toString().orEmpty(),
            salaryNegotiable = job.salaryNegotiable ?: false,
            currency = job.currency ?: currency,
            city = job.city.orEmpty(),
            district = job.district.orEmpty(),
            state = job.state.orEmpty(),
            country = job.country.orEmpty(),
            pinCode = job.pinCode.orEmpty(),
            relocationAvailable = job.relocationAvailable ?: false,
            accommodationProvided = job.accommodationProvided ?: false,
            transportationProvided = job.transportationProvided ?: false,
            foodProvided = job.foodProvided ?: false,
            applicationDeadline = formatInstantForInput(job.applicationDeadline),
            expiresAt = formatInstantForInput(job.expiresAt)
        )
    }

    private fun JobFormUiState.toCreateRequest(): CreateJobRequest {
        return CreateJobRequest(
            title = title.trim(),
            description = description.trim(),
            responsibilities = responsibilities.trim().ifBlank { null },
            requirements = requirements.trim().ifBlank { null },
            benefits = benefits.trim().ifBlank { null },
            employmentType = employmentType,
            workplaceType = workplaceType,
            experienceLevel = experienceLevel,
            status = status,
            vacancies = vacancies.toIntOrNull(),
            minimumExperience = minimumExperience.toIntOrNull(),
            maximumExperience = maximumExperience.toIntOrNull(),
            minimumSalary = minimumSalary.toDoubleOrNull(),
            maximumSalary = maximumSalary.toDoubleOrNull(),
            salaryNegotiable = salaryNegotiable,
            currency = currency,
            city = city.trim(),
            district = district.trim().ifBlank { null },
            state = state.trim().ifBlank { null },
            country = country.trim().ifBlank { null },
            pinCode = pinCode.trim().ifBlank { null },
            relocationAvailable = relocationAvailable,
            accommodationProvided = accommodationProvided,
            transportationProvided = transportationProvided,
            foodProvided = foodProvided,
            applicationDeadline = parseDateToInstant(applicationDeadline),
            expiresAt = parseDateToInstant(expiresAt)
        )
    }

    private fun JobFormUiState.toPatchRequest(): PatchJobRequest {
        return PatchJobRequest(
            title = title.trim(),
            description = description.trim(),
            responsibilities = responsibilities.trim().ifBlank { null },
            requirements = requirements.trim().ifBlank { null },
            benefits = benefits.trim().ifBlank { null },
            employmentType = employmentType,
            workplaceType = workplaceType,
            experienceLevel = experienceLevel,
            status = status,
            vacancies = vacancies.toIntOrNull(),
            minimumExperience = minimumExperience.toIntOrNull(),
            maximumExperience = maximumExperience.toIntOrNull(),
            minimumSalary = minimumSalary.toDoubleOrNull(),
            maximumSalary = maximumSalary.toDoubleOrNull(),
            salaryNegotiable = salaryNegotiable,
            currency = currency,
            city = city.trim(),
            district = district.trim().ifBlank { null },
            state = state.trim().ifBlank { null },
            country = country.trim().ifBlank { null },
            pinCode = pinCode.trim().ifBlank { null },
            relocationAvailable = relocationAvailable,
            accommodationProvided = accommodationProvided,
            transportationProvided = transportationProvided,
            foodProvided = foodProvided,
            applicationDeadline = parseDateToInstant(applicationDeadline),
            expiresAt = parseDateToInstant(expiresAt)
        )
    }

    private fun formatInstantForInput(value: String?): String {
        if (value.isNullOrBlank()) return ""
        return runCatching {
            value.substringBefore('T')
        }.getOrDefault("")
    }

    private fun parseDateToInstant(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isBlank()) return null

        return try {
            LocalDate.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE)
                .atTime(23, 59, 59)
                .toInstant(ZoneOffset.UTC)
                .toString()
        } catch (_: DateTimeParseException) {
            null
        }
    }
}
