package com.aryan.jobportalapp.presentation.jobseeker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryan.jobportalapp.data.remote.dto.ApplicationStatus
import com.aryan.jobportalapp.data.remote.dto.InvitationStatus
import com.aryan.jobportalapp.data.remote.dto.JobSeekerApplicationItem
import com.aryan.jobportalapp.data.remote.dto.JobSeekerInvitationItem
import com.aryan.jobportalapp.data.repository.RecruitmentRepository
import com.aryan.jobportalapp.presentation.employer.ApplicationPendingAction
import kotlinx.coroutines.launch

enum class ActivitiesTab {
    APPLICATIONS,
    INVITATIONS
}

data class ActivitiesUiState(
    val selectedTab: ActivitiesTab = ActivitiesTab.APPLICATIONS,
    val isLoadingApplications: Boolean = true,
    val isLoadingInvitations: Boolean = false,
    val isRefreshingApplications: Boolean = false,
    val isRefreshingInvitations: Boolean = false,
    val hasLoadedApplications: Boolean = false,
    val hasLoadedInvitations: Boolean = false,
    val applications: List<JobSeekerApplicationItem> = emptyList(),
    val applicationStatusFilter: ApplicationStatus? = null,
    val invitations: List<JobSeekerInvitationItem> = emptyList(),
    val invitationStatusFilter: InvitationStatus? = null,
    val applicationsError: String? = null,
    val invitationsError: String? = null,
    val pendingInvitationActions: Map<String, ApplicationPendingAction> = emptyMap(),
    val toastMessage: String? = null
)

class ActivitiesViewModel(
    private val recruitmentRepository: RecruitmentRepository = RecruitmentRepository()
) : ViewModel() {

    var uiState by mutableStateOf(ActivitiesUiState())
        private set

    init {
        loadApplications()
    }

    fun selectTab(tab: ActivitiesTab) {
        if (uiState.selectedTab == tab) return
        uiState = uiState.copy(selectedTab = tab)
        when (tab) {
            ActivitiesTab.APPLICATIONS -> {
                if (!uiState.hasLoadedApplications) {
                    loadApplications()
                }
            }
            ActivitiesTab.INVITATIONS -> {
                if (!uiState.hasLoadedInvitations) {
                    loadInvitations()
                }
            }
        }
    }

    fun onApplicationStatusFilterSelected(status: ApplicationStatus?) {
        uiState = uiState.copy(applicationStatusFilter = status)
    }

    fun onInvitationStatusFilterSelected(status: InvitationStatus?) {
        uiState = uiState.copy(invitationStatusFilter = status)
    }

    fun loadApplications(isRefresh: Boolean = false) {
        viewModelScope.launch {
            val isInitialLoad = !uiState.hasLoadedApplications
            uiState = uiState.copy(
                isLoadingApplications = isInitialLoad && !isRefresh,
                isRefreshingApplications = isRefresh,
                applicationsError = if (isInitialLoad) null else uiState.applicationsError
            )

            recruitmentRepository.getJobSeekerApplications()
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

            recruitmentRepository.getJobSeekerInvitations()
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

    fun acceptInvitation(invitationId: String) {
        updateInvitationStatus(
            invitationId = invitationId,
            newStatus = InvitationStatus.ACCEPTED,
            pendingAction = ApplicationPendingAction.ACCEPT
        )
    }

    fun rejectInvitation(invitationId: String) {
        updateInvitationStatus(
            invitationId = invitationId,
            newStatus = InvitationStatus.REJECTED,
            pendingAction = ApplicationPendingAction.REJECT
        )
    }

    fun consumeToast() {
        uiState = uiState.copy(toastMessage = null)
    }

    private fun updateInvitationStatus(
        invitationId: String,
        newStatus: InvitationStatus,
        pendingAction: ApplicationPendingAction
    ) {
        if (invitationId in uiState.pendingInvitationActions) return

        viewModelScope.launch {
            uiState = uiState.copy(
                pendingInvitationActions = uiState.pendingInvitationActions + (invitationId to pendingAction)
            )

            recruitmentRepository.updateInvitationStatus(
                invitationId = invitationId,
                status = newStatus
            )
                .onSuccess {
                    uiState = uiState.copy(
                        pendingInvitationActions = uiState.pendingInvitationActions - invitationId,
                        invitations = uiState.invitations.map { invitation ->
                            if (invitation.invitationId == invitationId) {
                                invitation.copy(status = newStatus)
                            } else {
                                invitation
                            }
                        },
                        toastMessage = when (newStatus) {
                            InvitationStatus.ACCEPTED -> "Invitation accepted"
                            InvitationStatus.REJECTED -> "Invitation rejected"
                            else -> "Invitation updated"
                        }
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        pendingInvitationActions = uiState.pendingInvitationActions - invitationId,
                        toastMessage = error.message ?: "Unable to update invitation"
                    )
                }
        }
    }
}
