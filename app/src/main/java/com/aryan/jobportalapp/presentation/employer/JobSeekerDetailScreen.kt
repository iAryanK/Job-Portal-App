package com.aryan.jobportalapp.presentation.employer

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aryan.jobportalapp.data.remote.dto.JobSummaryResponse
import com.aryan.jobportalapp.presentation.common.PullRefreshContainer
import com.aryan.jobportalapp.presentation.jobseeker.JobSeekerProfileContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobSeekerDetailScreen(
    jobSeekerId: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    viewModel: JobSeekerDetailViewModel = viewModel(
        key = "job_seeker_detail_$jobSeekerId",
        initializer = { JobSeekerDetailViewModel(jobSeekerId = jobSeekerId) }
    )
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    var selectedJobId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.toastMessage) {
        val message = uiState.toastMessage ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.consumeToast()
    }

    LaunchedEffect(uiState.showInviteDialog, uiState.employerJobs, uiState.activeInvitedJobIds, uiState.appliedJobIds) {
        if (uiState.showInviteDialog && selectedJobId == null) {
            selectedJobId = uiState.employerJobs
                .firstOrNull { job ->
                    val jobId = job.id.orEmpty()
                    jobId.isNotBlank() &&
                        jobId !in uiState.activeInvitedJobIds &&
                        jobId !in uiState.appliedJobIds
                }
                ?.id
        }
    }

    if (uiState.showInviteDialog) {
        InviteJobDialog(
            jobs = uiState.employerJobs,
            isLoadingJobs = uiState.isLoadingJobs,
            isInviting = uiState.isInviting,
            activeInvitedJobIds = uiState.activeInvitedJobIds,
            appliedJobIds = uiState.appliedJobIds,
            selectedJobId = selectedJobId,
            onSelectJob = { selectedJobId = it },
            onDismiss = {
                selectedJobId = null
                viewModel.dismissInviteDialog()
            },
            onConfirm = {
                selectedJobId?.let { jobId ->
                    viewModel.inviteToJob(jobId)
                }
            }
        )
    }

    if (uiState.showApplicationsSheet) {
        JobSeekerApplicationsBottomSheet(
            applications = uiState.applications,
            isLoading = uiState.isLoadingJobs,
            pendingApplicationActions = uiState.pendingApplicationActions,
            onDismiss = viewModel::dismissApplicationsSheet,
            onAcceptApplication = viewModel::acceptApplication,
            onRejectApplication = viewModel::rejectApplication
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Candidate details",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.showEmployerActions) {
                EmployerActionsBottomBar(
                    hasApplications = uiState.hasApplications,
                    isInviting = uiState.isInviting,
                    onInviteClick = viewModel::openInviteDialog,
                    onApplicationsClick = viewModel::openApplicationsSheet
                )
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading && uiState.jobSeeker == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null && uiState.jobSeeker == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.errorMessage.orEmpty(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadJobSeeker() }) {
                            Text(text = "Retry")
                        }
                    }
                }
            }

            else -> {
                uiState.jobSeeker?.let { jobSeeker ->
                    PullRefreshContainer(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = { viewModel.loadJobSeeker(isRefresh = true) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        JobSeekerProfileContent(
                            jobSeeker = jobSeeker,
                            isOwnProfile = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmployerActionsBottomBar(
    hasApplications: Boolean,
    isInviting: Boolean,
    onInviteClick: () -> Unit,
    onApplicationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onInviteClick,
            enabled = !isInviting,
            modifier = if (hasApplications) {
                Modifier.weight(1f)
            } else {
                Modifier.fillMaxWidth()
            }
        ) {
            OutlinedButtonLoadingContent(
                isLoading = isInviting,
                label = "Invite"
            )
        }

        if (hasApplications) {
            Button(
                onClick = onApplicationsClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Applications")
            }
        }
    }
}

@Composable
private fun InviteJobDialog(
    jobs: List<JobSummaryResponse>,
    isLoadingJobs: Boolean,
    isInviting: Boolean,
    activeInvitedJobIds: Set<String>,
    appliedJobIds: Set<String>,
    selectedJobId: String?,
    onSelectJob: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Invite to a job") },
        text = {
            when {
                isLoadingJobs -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                jobs.isEmpty() -> {
                    Text(
                        text = "You don't have any job postings yet. Create a job first to send an invitation.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                else -> {
                    LazyColumn {
                        items(jobs, key = { it.id.orEmpty() }) { job ->
                            val jobId = job.id.orEmpty()
                            val alreadyInvited = jobId in activeInvitedJobIds
                            val alreadyApplied = jobId in appliedJobIds
                            val isUnavailable = alreadyInvited || alreadyApplied

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isUnavailable && !isInviting) {
                                        onSelectJob(jobId)
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedJobId == jobId,
                                    onClick = {
                                        if (!isUnavailable && !isInviting) {
                                            onSelectJob(jobId)
                                        }
                                    },
                                    enabled = !isUnavailable && !isInviting
                                )
                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    Text(
                                        text = job.title.orEmpty().ifBlank { "Untitled job" },
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    when {
                                        alreadyApplied -> {
                                            Text(
                                                text = "Already applied",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        alreadyInvited -> {
                                            Text(
                                                text = "Already invited",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoadingJobs &&
                    jobs.isNotEmpty() &&
                    !selectedJobId.isNullOrBlank() &&
                    selectedJobId !in activeInvitedJobIds &&
                    selectedJobId !in appliedJobIds &&
                    !isInviting
            ) {
                Text(text = "Send invite")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}
