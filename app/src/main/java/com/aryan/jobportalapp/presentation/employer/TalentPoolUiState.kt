package com.aryan.jobportalapp.presentation.employer

import com.aryan.jobportalapp.data.remote.dto.FindJobSeekersFilter
import com.aryan.jobportalapp.data.remote.dto.Gender
import com.aryan.jobportalapp.data.remote.dto.JobSeekerSummaryResponse

data class TalentPoolFilters(
    val openToRemote: Boolean = false,
    val willingToRelocate: Boolean = false,
    val currentlyWorking: Boolean = false,
    val fresherOnly: Boolean = false,
    val gender: Gender? = null,
    val city: String = "",
    val district: String = "",
    val state: String = "",
    val country: String = "",
    val minExperienceYears: String = "",
    val maxExperienceYears: String = "",
    val highestQualification: String = "",
    val specialization: String = "",
    val preferredJobLocation: String = ""
) {
    fun toApiFilter(search: String): FindJobSeekersFilter {
        val minExperience = minExperienceYears.toIntOrNull()
            ?: if (fresherOnly) 0 else null
        val maxExperience = maxExperienceYears.toIntOrNull()
            ?: if (fresherOnly) 0 else null

        return FindJobSeekersFilter(
            search = search.takeIf { it.isNotBlank() },
            gender = gender,
            city = city.takeIf { it.isNotBlank() },
            district = district.takeIf { it.isNotBlank() },
            state = state.takeIf { it.isNotBlank() },
            country = country.takeIf { it.isNotBlank() },
            minExperienceYears = minExperience,
            maxExperienceYears = maxExperience,
            currentlyWorking = currentlyWorking.takeIf { it },
            willingToRelocate = willingToRelocate.takeIf { it },
            openToRemote = openToRemote.takeIf { it },
            preferredJobLocation = preferredJobLocation.takeIf { it.isNotBlank() },
            highestQualification = highestQualification.takeIf { it.isNotBlank() },
            specialization = specialization.takeIf { it.isNotBlank() }
        )
    }

    val advancedFilterCount: Int
        get() = listOfNotNull(
            gender?.let { 1 },
            city.takeIf { it.isNotBlank() },
            district.takeIf { it.isNotBlank() },
            state.takeIf { it.isNotBlank() },
            country.takeIf { it.isNotBlank() },
            minExperienceYears.takeIf { it.isNotBlank() && !fresherOnly },
            maxExperienceYears.takeIf { it.isNotBlank() && !fresherOnly },
            highestQualification.takeIf { it.isNotBlank() },
            specialization.takeIf { it.isNotBlank() },
            preferredJobLocation.takeIf { it.isNotBlank() }
        ).size

    val activeQuickFilterCount: Int
        get() = listOf(openToRemote, willingToRelocate, currentlyWorking, fresherOnly)
            .count { it }
}

data class TalentPoolUiState(
    val searchQuery: String = "",
    val jobSeekers: List<JobSeekerSummaryResponse> = emptyList(),
    val totalElements: Long = 0,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isReloadingList: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val hasLoadedOnce: Boolean = false,
    val errorMessage: String? = null,
    val filters: TalentPoolFilters = TalentPoolFilters(),
    val showAdvancedFilters: Boolean = false,
    val draftFilters: TalentPoolFilters = TalentPoolFilters()
) {
    val activeFilterCount: Int
        get() = filters.advancedFilterCount + filters.activeQuickFilterCount
}
