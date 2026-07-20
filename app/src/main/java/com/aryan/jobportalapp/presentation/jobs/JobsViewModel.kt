package com.aryan.jobportalapp.presentation.jobs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.FindJobsFilter
import com.aryan.jobportalapp.data.remote.dto.JobSortOption
import com.aryan.jobportalapp.data.repository.JobRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class JobsViewModel(
    private val jobRepository: JobRepository = JobRepository()
) : ViewModel() {

    var uiState by mutableStateOf(JobsUiState())
        private set

    private var currentPage = 0
    private var searchJob: Job? = null

    init {
        reloadList()
    }

    fun onSearchQueryChange(query: String) {
        uiState = uiState.copy(searchQuery = query)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            reloadList()
        }
    }

    fun pullToRefresh() {
        if (!uiState.hasLoadedOnce) return
        reloadList(showPullIndicator = true)
    }

    fun retryLoad() {
        reloadList()
    }

    private fun reloadList(showPullIndicator: Boolean = false) {
        viewModelScope.launch {
            currentPage = 0
            val isInitialLoad = !uiState.hasLoadedOnce
            uiState = uiState.copy(
                isLoading = isInitialLoad,
                isRefreshing = showPullIndicator && !isInitialLoad,
                isReloadingList = !isInitialLoad && !showPullIndicator,
                errorMessage = null
            )
            fetchPage(page = 0, append = false)
        }
    }

    fun loadMore() {
        if (
            uiState.isLoading ||
            uiState.isRefreshing ||
            uiState.isReloadingList ||
            uiState.isLoadingMore ||
            !uiState.hasMore
        ) {
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingMore = true, errorMessage = null)
            fetchPage(page = currentPage + 1, append = true)
        }
    }

    fun onSortOptionSelected(option: JobSortOption) {
        if (uiState.sortOption == option) return
        uiState = uiState.copy(sortOption = option)
        refresh()
    }

    fun toggleQuickFilter(update: FindJobsFilter.() -> FindJobsFilter) {
        uiState = uiState.copy(filters = uiState.filters.update())
        refresh()
    }

    fun openAdvancedFilters() {
        uiState = uiState.copy(
            showAdvancedFilters = true,
            draftFilters = uiState.filters
        )
    }

    fun dismissAdvancedFilters() {
        uiState = uiState.copy(showAdvancedFilters = false)
    }

    fun updateDraftFilters(update: FindJobsFilter.() -> FindJobsFilter) {
        uiState = uiState.copy(draftFilters = uiState.draftFilters.update())
    }

    fun applyAdvancedFilters() {
        uiState = uiState.copy(
            filters = uiState.draftFilters,
            showAdvancedFilters = false
        )
        refresh()
    }

    fun clearAdvancedFilters() {
        val cleared = uiState.draftFilters.copy(
            employmentType = null,
            workplaceType = null,
            experienceLevel = null,
            currency = null,
            city = "",
            district = "",
            state = "",
            country = "",
            minSalary = "",
            maxSalary = "",
            minExperience = "",
            maxExperience = "",
            industry = "",
            salaryNegotiable = false,
            relocationAvailable = false,
            accommodationProvided = false,
            transportationProvided = false,
            foodProvided = false
        )
        uiState = uiState.copy(draftFilters = cleared)
    }

    fun clearAllFilters() {
        uiState = uiState.copy(
            filters = FindJobsFilter(),
            draftFilters = FindJobsFilter(),
            searchQuery = "",
            sortOption = JobSortOption.NEWEST
        )
        reloadList()
    }

    private fun refresh() {
        reloadList()
    }

    private suspend fun fetchPage(page: Int, append: Boolean) {
        val result = jobRepository.findJobs(
            filter = uiState.filters.toApiFilter(uiState.searchQuery),
            page = page,
            sort = uiState.sortOption.sortParam
        )

        result
            .onSuccess { pageResult ->
                currentPage = pageResult.page
                uiState = uiState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    isReloadingList = false,
                    isLoadingMore = false,
                    hasLoadedOnce = true,
                    jobs = if (append) {
                        uiState.jobs + pageResult.items
                    } else {
                        pageResult.items
                    },
                    totalElements = pageResult.totalElements,
                    hasMore = !pageResult.isLastPage,
                    errorMessage = null
                )
            }
            .onFailure { error ->
                uiState = uiState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    isReloadingList = false,
                    isLoadingMore = false,
                    hasLoadedOnce = true,
                    errorMessage = error.message ?: "Unable to load jobs"
                )
            }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 350L
    }
}
