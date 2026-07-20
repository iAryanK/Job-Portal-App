package com.aryan.jobportalapp.presentation.employer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.repository.JobSeekerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TalentPoolViewModel(
    private val jobSeekerRepository: JobSeekerRepository = JobSeekerRepository()
) : ViewModel() {

    var uiState by mutableStateOf(TalentPoolUiState())
        private set

    private var currentPage = 0
    private var searchJob: Job? = null

    init {
        refresh()
    }

    fun onSearchQueryChange(query: String) {
        uiState = uiState.copy(searchQuery = query)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            refresh()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            currentPage = 0
            val isInitialLoad = !uiState.hasLoadedOnce
            uiState = uiState.copy(
                isLoading = isInitialLoad,
                isRefreshing = !isInitialLoad,
                errorMessage = null
            )
            fetchPage(page = 0, append = false)
        }
    }

    fun loadMore() {
        if (uiState.isLoading || uiState.isRefreshing || uiState.isLoadingMore || !uiState.hasMore) {
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingMore = true, errorMessage = null)
            fetchPage(page = currentPage + 1, append = true)
        }
    }

    fun toggleQuickFilter(update: TalentPoolFilters.() -> TalentPoolFilters) {
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

    fun updateDraftFilters(update: TalentPoolFilters.() -> TalentPoolFilters) {
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
            gender = null,
            city = "",
            district = "",
            state = "",
            country = "",
            minExperienceYears = "",
            maxExperienceYears = "",
            highestQualification = "",
            specialization = "",
            preferredJobLocation = ""
        )
        uiState = uiState.copy(draftFilters = cleared)
    }

    fun clearAllFilters() {
        uiState = uiState.copy(
            filters = TalentPoolFilters(),
            draftFilters = TalentPoolFilters(),
            searchQuery = ""
        )
        refresh()
    }

    private suspend fun fetchPage(page: Int, append: Boolean) {
        val result = jobSeekerRepository.findJobSeekers(
            filter = uiState.filters.toApiFilter(uiState.searchQuery),
            page = page
        )

        result
            .onSuccess { pageResult ->
                currentPage = pageResult.page
                uiState = uiState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    isLoadingMore = false,
                    hasLoadedOnce = true,
                    jobSeekers = if (append) {
                        uiState.jobSeekers + pageResult.items
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
                    isLoadingMore = false,
                    hasLoadedOnce = true,
                    errorMessage = error.message ?: "Unable to load talent pool"
                )
            }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 350L
    }
}
