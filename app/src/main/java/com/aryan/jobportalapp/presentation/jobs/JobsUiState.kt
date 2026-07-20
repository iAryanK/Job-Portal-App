package com.aryan.jobportalapp.presentation.jobs

import com.aryan.jobportalapp.data.remote.dto.FindJobsFilter
import com.aryan.jobportalapp.data.remote.dto.JobSortOption
import com.aryan.jobportalapp.data.remote.dto.JobSummaryResponse

data class JobsUiState(
    val searchQuery: String = "",
    val jobs: List<JobSummaryResponse> = emptyList(),
    val totalElements: Long = 0,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isReloadingList: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val hasLoadedOnce: Boolean = false,
    val errorMessage: String? = null,
    val filters: FindJobsFilter = FindJobsFilter(),
    val draftFilters: FindJobsFilter = FindJobsFilter(),
    val sortOption: JobSortOption = JobSortOption.NEWEST,
    val showAdvancedFilters: Boolean = false
) {
    val activeFilterCount: Int
        get() = filters.advancedFilterCount + filters.activeQuickFilterCount
}
