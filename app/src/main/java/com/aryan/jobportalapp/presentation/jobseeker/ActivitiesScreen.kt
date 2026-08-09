package com.aryan.jobportalapp.presentation.jobseeker

import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aryan.jobportalapp.data.remote.dto.ApplicationStatus
import com.aryan.jobportalapp.data.remote.dto.InvitationStatus
import com.aryan.jobportalapp.data.remote.dto.JobSeekerApplicationItem
import com.aryan.jobportalapp.data.remote.dto.JobSeekerInvitationItem
import com.aryan.jobportalapp.presentation.common.PullRefreshContainer
import com.aryan.jobportalapp.presentation.common.enumLabel
import com.aryan.jobportalapp.presentation.jobs.JobSummaryCard
import com.aryan.jobportalapp.presentation.jobs.formatDateLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(
    modifier: Modifier = Modifier,
    onJobClick: (String) -> Unit = {},
    viewModel: ActivitiesViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val selectedTabIndex = when (uiState.selectedTab) {
        ActivitiesTab.APPLICATIONS -> 0
        ActivitiesTab.INVITATIONS -> 1
    }

    Column(modifier = modifier.fillMaxSize()) {
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = uiState.selectedTab == ActivitiesTab.APPLICATIONS,
                onClick = { viewModel.selectTab(ActivitiesTab.APPLICATIONS) },
                text = { Text(text = "Applications") }
            )
            Tab(
                selected = uiState.selectedTab == ActivitiesTab.INVITATIONS,
                onClick = { viewModel.selectTab(ActivitiesTab.INVITATIONS) },
                text = { Text(text = "Invitations") }
            )
        }

        when (uiState.selectedTab) {
            ActivitiesTab.APPLICATIONS -> {
                ActivitiesApplicationsTab(
                    isLoading = uiState.isLoadingApplications,
                    isRefreshing = uiState.isRefreshingApplications,
                    hasLoaded = uiState.hasLoadedApplications,
                    applications = uiState.applications,
                    applicationStatusFilter = uiState.applicationStatusFilter,
                    errorMessage = uiState.applicationsError,
                    onRefresh = { viewModel.loadApplications(isRefresh = true) },
                    onRetry = viewModel::loadApplications,
                    onApplicationStatusFilterSelected = viewModel::onApplicationStatusFilterSelected,
                    onJobClick = onJobClick
                )
            }

            ActivitiesTab.INVITATIONS -> {
                ActivitiesInvitationsTab(
                    isLoading = uiState.isLoadingInvitations,
                    isRefreshing = uiState.isRefreshingInvitations,
                    hasLoaded = uiState.hasLoadedInvitations,
                    invitations = uiState.invitations,
                    invitationStatusFilter = uiState.invitationStatusFilter,
                    errorMessage = uiState.invitationsError,
                    onRefresh = { viewModel.loadInvitations(isRefresh = true) },
                    onRetry = viewModel::loadInvitations,
                    onInvitationStatusFilterSelected = viewModel::onInvitationStatusFilterSelected,
                    onJobClick = onJobClick
                )
            }
        }
    }
}

@Composable
private fun ActivitiesApplicationsTab(
    isLoading: Boolean,
    isRefreshing: Boolean,
    hasLoaded: Boolean,
    applications: List<JobSeekerApplicationItem>,
    applicationStatusFilter: ApplicationStatus?,
    errorMessage: String?,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onApplicationStatusFilterSelected: (ApplicationStatus?) -> Unit,
    onJobClick: (String) -> Unit
) {
    val filteredApplications = remember(applications, applicationStatusFilter) {
        applicationStatusFilter?.let { status ->
            applications.filter { it.status == status }
        } ?: applications
    }

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
                ActivitiesErrorState(message = errorMessage, onRetry = onRetry)
            }

            applications.isEmpty() -> {
                ActivitiesEmptyState(
                    icon = Icons.AutoMirrored.Outlined.Assignment,
                    title = "No applications yet",
                    message = "Jobs you apply to will appear here so you can track their status."
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 24.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        ActivitiesSectionHeader(
                            eyebrow = "RECENTLY",
                            title = "Applied by you",
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            ActivitiesStatusFilterDropdown(
                                selectedLabel = applicationStatusFilter?.let { enumLabel(it) } ?: "All",
                                options = listOf(null) + ApplicationStatus.entries,
                                onSelected = onApplicationStatusFilterSelected,
                                optionLabel = { status ->
                                    status?.let { enumLabel(it) } ?: "All"
                                }
                            )
                        }
                    }

                    if (filteredApplications.isEmpty()) {
                        item {
                            Text(
                                text = "No applications match this filter.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                            )
                        }
                    } else {
                        items(
                            items = filteredApplications,
                            key = { it.applicationId }
                        ) { application ->
                            JobSeekerApplicationCard(
                                application = application,
                                onJobClick = {
                                    application.job.id?.takeIf { it.isNotBlank() }?.let(onJobClick)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivitiesSectionHeader(
    eyebrow: String,
    title: String,
    modifier: Modifier = Modifier,
    filter: @Composable () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = eyebrow,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        filter()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> ActivitiesStatusFilterDropdown(
    selectedLabel: String,
    options: List<T>,
    onSelected: (T) -> Unit,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.widthIn(max = 148.dp)
    ) {
        BasicTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            textStyle = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            interactionSource = interactionSource,
            modifier = Modifier
                .menuAnchor()
                .widthIn(min = 112.dp, max = 148.dp),
            decorationBox = { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = selectedLabel,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    isError = false,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = textFieldColors,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = true,
                            isError = false,
                            interactionSource = interactionSource,
                            colors = textFieldColors,
                            shape = MaterialTheme.shapes.small
                        )
                    }
                )
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = optionLabel(option),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ActivitiesInvitationsTab(
    isLoading: Boolean,
    isRefreshing: Boolean,
    hasLoaded: Boolean,
    invitations: List<JobSeekerInvitationItem>,
    invitationStatusFilter: InvitationStatus?,
    errorMessage: String?,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onInvitationStatusFilterSelected: (InvitationStatus?) -> Unit,
    onJobClick: (String) -> Unit
) {
    val filteredInvitations = remember(invitations, invitationStatusFilter) {
        invitationStatusFilter?.let { status ->
            invitations.filter { it.status == status }
        } ?: invitations
    }

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
                ActivitiesErrorState(message = errorMessage, onRetry = onRetry)
            }

            invitations.isEmpty() -> {
                ActivitiesEmptyState(
                    icon = Icons.Outlined.MailOutline,
                    title = "No invitations yet",
                    message = "When employers invite you to apply for a role, those jobs will show up here."
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 24.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        ActivitiesSectionHeader(
                            eyebrow = "RECENTLY",
                            title = "Received invitations",
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            ActivitiesStatusFilterDropdown(
                                selectedLabel = invitationStatusFilter?.let { enumLabel(it) } ?: "All",
                                options = listOf(null) + InvitationStatus.entries,
                                onSelected = onInvitationStatusFilterSelected,
                                optionLabel = { status ->
                                    status?.let { enumLabel(it) } ?: "All"
                                }
                            )
                        }
                    }

                    if (filteredInvitations.isEmpty()) {
                        item {
                            Text(
                                text = "No invitations match this filter.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                            )
                        }
                    } else {
                        items(
                            items = filteredInvitations,
                            key = { it.invitationId }
                        ) { invitation ->
                            JobSeekerInvitationCard(
                                invitation = invitation,
                                onJobClick = {
                                    invitation.job.id?.takeIf { it.isNotBlank() }?.let(onJobClick)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JobSeekerApplicationCard(
    application: JobSeekerApplicationItem,
    onJobClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        RowWithStatusBadge(
            statusLabel = enumLabel(application.status),
            containerColor = applicationStatusColor(application.status),
            contentColor = applicationStatusContentColor(application.status)
        )
        JobSummaryCard(
            job = application.job,
            onClick = onJobClick,
            showStatus = false
        )
        application.message?.takeIf { it.isNotBlank() }?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun JobSeekerInvitationCard(
    invitation: JobSeekerInvitationItem,
    onJobClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        RowWithStatusBadge(
            statusLabel = enumLabel(invitation.status),
            containerColor = invitationStatusColor(invitation.status),
            contentColor = invitationStatusContentColor(invitation.status)
        )
        JobSummaryCard(
            job = invitation.job,
            onClick = onJobClick,
            showStatus = false
        )
        invitation.expiresAt?.let { expiresAt ->
            formatDateLabel(expiresAt)?.let { formatted ->
                Text(
                    text = "Expires $formatted",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun RowWithStatusBadge(
    statusLabel: String,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = containerColor
        ) {
            Text(
                text = statusLabel,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun ActivitiesEmptyState(
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
private fun ActivitiesErrorState(
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
