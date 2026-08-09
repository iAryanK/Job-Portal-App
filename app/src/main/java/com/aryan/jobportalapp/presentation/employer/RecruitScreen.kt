package com.aryan.jobportalapp.presentation.employer

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aryan.jobportalapp.data.remote.dto.ApplicationStatus
import com.aryan.jobportalapp.data.remote.dto.InvitationStatus
import com.aryan.jobportalapp.data.remote.dto.RecruitApplicationItem
import com.aryan.jobportalapp.data.remote.dto.RecruitInvitationItem
import com.aryan.jobportalapp.presentation.common.PullRefreshContainer
import com.aryan.jobportalapp.presentation.common.enumLabel
import com.aryan.jobportalapp.presentation.jobs.formatDateLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecruitScreen(
    modifier: Modifier = Modifier,
    onJobSeekerClick: (String) -> Unit = {},
    onJobClick: (String) -> Unit = {},
    viewModel: RecruitViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val selectedTabIndex = when (uiState.selectedTab) {
        RecruitTab.APPLICATIONS -> 0
        RecruitTab.INVITATIONS -> 1
    }

    LaunchedEffect(uiState.toastMessage) {
        val message = uiState.toastMessage ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.consumeToast()
    }

    Column(modifier = modifier.fillMaxSize()) {
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = uiState.selectedTab == RecruitTab.APPLICATIONS,
                onClick = { viewModel.selectTab(RecruitTab.APPLICATIONS) },
                text = { Text(text = "Applications") }
            )
            Tab(
                selected = uiState.selectedTab == RecruitTab.INVITATIONS,
                onClick = { viewModel.selectTab(RecruitTab.INVITATIONS) },
                text = { Text(text = "Invitations") }
            )
        }

        when (uiState.selectedTab) {
            RecruitTab.APPLICATIONS -> {
                RecruitApplicationsTab(
                    isLoading = uiState.isLoadingApplications,
                    isRefreshing = uiState.isRefreshingApplications,
                    hasLoaded = uiState.hasLoadedApplications,
                    applications = uiState.applications,
                    pendingApplicationActions = uiState.pendingApplicationActions,
                    errorMessage = uiState.applicationsError,
                    onRefresh = { viewModel.loadApplications(isRefresh = true) },
                    onRetry = viewModel::loadApplications,
                    onJobSeekerClick = onJobSeekerClick,
                    onJobClick = onJobClick,
                    onAcceptApplication = viewModel::acceptApplication,
                    onRejectApplication = viewModel::rejectApplication
                )
            }

            RecruitTab.INVITATIONS -> {
                RecruitInvitationsTab(
                    isLoading = uiState.isLoadingInvitations,
                    isRefreshing = uiState.isRefreshingInvitations,
                    hasLoaded = uiState.hasLoadedInvitations,
                    invitations = uiState.invitations,
                    errorMessage = uiState.invitationsError,
                    onRefresh = { viewModel.loadInvitations(isRefresh = true) },
                    onRetry = viewModel::loadInvitations,
                    onJobSeekerClick = onJobSeekerClick,
                    onJobClick = onJobClick
                )
            }
        }
    }
}

@Composable
private fun RecruitApplicationsTab(
    isLoading: Boolean,
    isRefreshing: Boolean,
    hasLoaded: Boolean,
    applications: List<RecruitApplicationItem>,
    pendingApplicationActions: Map<String, ApplicationPendingAction>,
    errorMessage: String?,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onJobSeekerClick: (String) -> Unit,
    onJobClick: (String) -> Unit,
    onAcceptApplication: (String) -> Unit,
    onRejectApplication: (String) -> Unit
) {
    PullRefreshContainer(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            isLoading && !hasLoaded -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null && applications.isEmpty() -> {
                RecruitErrorState(
                    message = errorMessage,
                    onRetry = onRetry
                )
            }

            applications.isEmpty() -> {
                RecruitEmptyState(
                    icon = Icons.AutoMirrored.Outlined.Assignment,
                    title = "No applications yet",
                    message = "Candidates who apply to your job openings will appear here."
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "${applications.size} applications",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                    }

                    items(
                        items = applications,
                        key = { it.applicationId }
                    ) { application ->
                        RecruitApplicationCard(
                            application = application,
                            pendingAction = pendingApplicationActions[application.applicationId],
                            onJobSeekerClick = { onJobSeekerClick(application.jobSeekerId) },
                            onJobClick = { onJobClick(application.jobId) },
                            onAccept = { onAcceptApplication(application.applicationId) },
                            onReject = { onRejectApplication(application.applicationId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecruitInvitationsTab(
    isLoading: Boolean,
    isRefreshing: Boolean,
    hasLoaded: Boolean,
    invitations: List<RecruitInvitationItem>,
    errorMessage: String?,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onJobSeekerClick: (String) -> Unit,
    onJobClick: (String) -> Unit
) {
    PullRefreshContainer(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            isLoading && !hasLoaded -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null && invitations.isEmpty() -> {
                RecruitErrorState(
                    message = errorMessage,
                    onRetry = onRetry
                )
            }

            invitations.isEmpty() -> {
                RecruitEmptyState(
                    icon = Icons.Outlined.MailOutline,
                    title = "No invitations sent",
                    message = "Job seekers you invite to your openings will appear here."
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "${invitations.size} invitations",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                    }

                    items(
                        items = invitations,
                        key = { it.invitationId }
                    ) { invitation ->
                        RecruitInvitationCard(
                            invitation = invitation,
                            onJobSeekerClick = { onJobSeekerClick(invitation.jobSeekerId) },
                            onJobClick = { onJobClick(invitation.jobId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecruitApplicationCard(
    application: RecruitApplicationItem,
    pendingAction: ApplicationPendingAction?,
    onJobSeekerClick: () -> Unit,
    onJobClick: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    RecruitCandidateCard(
        candidateName = application.jobSeekerName,
        candidateHeadline = application.jobSeekerHeadline,
        jobTitle = application.jobTitle,
        statusLabel = enumLabel(application.status),
        statusContainerColor = applicationStatusColor(application.status),
        statusContentColor = applicationStatusContentColor(application.status),
        subtitle = application.message,
        city = application.city,
        state = application.state,
        country = application.country,
        onJobSeekerClick = onJobSeekerClick,
        onJobClick = onJobClick,
        actionContent = if (application.status == ApplicationStatus.APPLIED) {
            {
                ApplicationActionButtonsRow(
                    pendingAction = pendingAction,
                    onAccept = onAccept,
                    onReject = onReject
                )
            }
        } else {
            null
        }
    )
}

@Composable
private fun RecruitInvitationCard(
    invitation: RecruitInvitationItem,
    onJobSeekerClick: () -> Unit,
    onJobClick: () -> Unit
) {
    RecruitCandidateCard(
        candidateName = invitation.jobSeekerName,
        candidateHeadline = invitation.jobSeekerHeadline,
        jobTitle = invitation.jobTitle,
        statusLabel = enumLabel(invitation.status),
        statusContainerColor = invitationStatusColor(invitation.status),
        statusContentColor = invitationStatusContentColor(invitation.status),
        subtitle = invitation.expiresAt?.let { expiresAt ->
            formatDateLabel(expiresAt)?.let { formatted -> "Expires $formatted" }
        },
        city = invitation.city,
        state = invitation.state,
        country = invitation.country,
        onJobSeekerClick = onJobSeekerClick,
        onJobClick = onJobClick
    )
}

@Composable
private fun RecruitCandidateCard(
    candidateName: String,
    candidateHeadline: String?,
    jobTitle: String,
    statusLabel: String,
    statusContainerColor: androidx.compose.ui.graphics.Color,
    statusContentColor: androidx.compose.ui.graphics.Color,
    subtitle: String?,
    city: String?,
    state: String?,
    country: String?,
    onJobSeekerClick: () -> Unit,
    onJobClick: () -> Unit,
    actionContent: (@Composable () -> Unit)? = null
) {
    OutlinedCard(
        onClick = onJobSeekerClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = candidateName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        candidateHeadline?.takeIf { it.isNotBlank() }?.let { headline ->
                            Text(
                                text = headline,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = statusContainerColor
                ) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusContentColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            OutlinedCard(
                onClick = onJobClick,
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WorkOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = jobTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            subtitle?.takeIf { it.isNotBlank() }?.let { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            formatRecruitLocation(city, state, country)?.let { location ->
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            actionContent?.invoke()
        }
    }
}

@Composable
private fun RecruitEmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun RecruitErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text(text = "Retry")
            }
        }
    }
}

@Composable
private fun applicationStatusColor(status: ApplicationStatus) = when (status) {
    ApplicationStatus.APPLIED -> MaterialTheme.colorScheme.secondaryContainer
    ApplicationStatus.SHORTLISTED -> MaterialTheme.colorScheme.tertiaryContainer
    ApplicationStatus.SELECTED -> MaterialTheme.colorScheme.primaryContainer
    ApplicationStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer
}

@Composable
private fun applicationStatusContentColor(status: ApplicationStatus) = when (status) {
    ApplicationStatus.APPLIED -> MaterialTheme.colorScheme.onSecondaryContainer
    ApplicationStatus.SHORTLISTED -> MaterialTheme.colorScheme.onTertiaryContainer
    ApplicationStatus.SELECTED -> MaterialTheme.colorScheme.onPrimaryContainer
    ApplicationStatus.REJECTED -> MaterialTheme.colorScheme.onErrorContainer
}

@Composable
private fun invitationStatusColor(status: InvitationStatus) = when (status) {
    InvitationStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer
    InvitationStatus.ACCEPTED -> MaterialTheme.colorScheme.primaryContainer
    InvitationStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer
}

@Composable
private fun invitationStatusContentColor(status: InvitationStatus) = when (status) {
    InvitationStatus.PENDING -> MaterialTheme.colorScheme.onSecondaryContainer
    InvitationStatus.ACCEPTED -> MaterialTheme.colorScheme.onPrimaryContainer
    InvitationStatus.REJECTED -> MaterialTheme.colorScheme.onErrorContainer
}

private fun formatRecruitLocation(
    city: String?,
    state: String?,
    country: String?
): String? {
    val parts = listOfNotNull(
        city?.takeIf { it.isNotBlank() },
        state?.takeIf { it.isNotBlank() },
        country?.takeIf { it.isNotBlank() }
    )
    return parts.takeIf { it.isNotEmpty() }?.joinToString(", ")
}
