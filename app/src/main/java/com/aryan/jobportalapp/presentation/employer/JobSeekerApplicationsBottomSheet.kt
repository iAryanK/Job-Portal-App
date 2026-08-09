package com.aryan.jobportalapp.presentation.employer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aryan.jobportalapp.data.remote.dto.ApplicationStatus
import com.aryan.jobportalapp.data.remote.dto.RecruitApplicationItem
import com.aryan.jobportalapp.presentation.common.enumLabel
import com.aryan.jobportalapp.presentation.jobs.formatDateLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobSeekerApplicationsBottomSheet(
    applications: List<RecruitApplicationItem>,
    isLoading: Boolean,
    pendingApplicationActions: Map<String, ApplicationPendingAction>,
    onDismiss: () -> Unit,
    onAcceptApplication: (String) -> Unit,
    onRejectApplication: (String) -> Unit
) {
    val sheetHeight = (LocalConfiguration.current.screenHeightDp * 0.8f).dp
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(sheetHeight),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Applications",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Review jobs this candidate has applied to.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (applications.isEmpty()) {
                item {
                    Text(
                        text = "No applications from this candidate yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(
                    items = applications,
                    key = { it.applicationId }
                ) { application ->
                    ApplicationSheetItem(
                        application = application,
                        pendingAction = pendingApplicationActions[application.applicationId],
                        onAccept = { onAcceptApplication(application.applicationId) },
                        onReject = { onRejectApplication(application.applicationId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ApplicationSheetItem(
    application: RecruitApplicationItem,
    pendingAction: ApplicationPendingAction?,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.WorkOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = application.jobTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    formatDateLabel(application.appliedAt)?.let { appliedOn ->
                        Text(
                            text = "Applied on $appliedOn",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = enumLabel(application.status),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (application.status == ApplicationStatus.APPLIED) {
                ApplicationActionButtonsRow(
                    pendingAction = pendingAction,
                    onAccept = onAccept,
                    onReject = onReject
                )
            }
        }
    }
}
