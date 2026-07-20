package com.aryan.jobportalapp.presentation.employer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.RecruitApplicationItem
import com.aryan.jobportalapp.data.remote.dto.RecruitInvitationItem
import com.aryan.jobportalapp.data.repository.RecruitmentRepository
import kotlinx.coroutines.launch

enum class RecruitTab {
    APPLICATIONS,
    INVITATIONS
}

data class RecruitUiState(
    val selectedTab: RecruitTab = RecruitTab.APPLICATIONS,
    val isLoadingApplications: Boolean = true,
    val isLoadingInvitations: Boolean = false,
    val isRefreshingApplications: Boolean = false,
    val isRefreshingInvitations: Boolean = false,
    val hasLoadedApplications: Boolean = false,
    val hasLoadedInvitations: Boolean = false,
    val applications: List<RecruitApplicationItem> = emptyList(),
    val invitations: List<RecruitInvitationItem> = emptyList(),
    val applicationsError: String? = null,
    val invitationsError: String? = null
)

class RecruitViewModel(
    private val recruitmentRepository: RecruitmentRepository = RecruitmentRepository()
) : ViewModel() {

    var uiState by mutableStateOf(RecruitUiState())
        private set

    init {
        loadApplications()
    }

    fun selectTab(tab: RecruitTab) {
        if (uiState.selectedTab == tab) return
        uiState = uiState.copy(selectedTab = tab)
        when (tab) {
            RecruitTab.APPLICATIONS -> {
                if (!uiState.hasLoadedApplications) {
                    loadApplications()
                }
            }
            RecruitTab.INVITATIONS -> {
                if (!uiState.hasLoadedInvitations) {
                    loadInvitations()
                }
            }
        }
    }

    fun refreshCurrentTab() {
        when (uiState.selectedTab) {
            RecruitTab.APPLICATIONS -> loadApplications(isRefresh = true)
            RecruitTab.INVITATIONS -> loadInvitations(isRefresh = true)
        }
    }

    fun loadApplications(isRefresh: Boolean = false) {
        viewModelScope.launch {
            val isInitialLoad = !uiState.hasLoadedApplications
            uiState = uiState.copy(
                isLoadingApplications = isInitialLoad && !isRefresh,
                isRefreshingApplications = isRefresh,
                applicationsError = if (isInitialLoad) null else uiState.applicationsError
            )

            recruitmentRepository.getEmployerApplications()
                .onSuccess { applications ->
                    uiState = uiState.copy(
                        isLoadingApplications = false,
                        isRefreshingApplications = false,
                        hasLoadedApplications = true,
                        applications = applications,
                        applicationsError = null
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoadingApplications = false,
                        isRefreshingApplications = false,
                        hasLoadedApplications = true,
                        applicationsError = error.message ?: "Unable to load applications"
                    )
                }
        }
    }

    fun loadInvitations(isRefresh: Boolean = false) {
        viewModelScope.launch {
            val isInitialLoad = !uiState.hasLoadedInvitations
            uiState = uiState.copy(
                isLoadingInvitations = isInitialLoad && !isRefresh,
                isRefreshingInvitations = isRefresh,
                invitationsError = if (isInitialLoad) null else uiState.invitationsError
            )

            recruitmentRepository.getEmployerInvitations()
                .onSuccess { invitations ->
                    uiState = uiState.copy(
                        isLoadingInvitations = false,
                        isRefreshingInvitations = false,
                        hasLoadedInvitations = true,
                        invitations = invitations,
                        invitationsError = null
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoadingInvitations = false,
                        isRefreshingInvitations = false,
                        hasLoadedInvitations = true,
                        invitationsError = error.message ?: "Unable to load invitations"
                    )
                }
        }
    }
}
