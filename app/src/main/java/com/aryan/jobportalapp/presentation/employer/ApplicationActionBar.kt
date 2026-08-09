package com.aryan.jobportalapp.presentation.employer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

enum class ApplicationPendingAction {
    ACCEPT,
    REJECT
}

@Composable
fun ApplicationActionBottomBar(
    jobTitle: String,
    pendingAction: ApplicationPendingAction?,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = jobTitle,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        ApplicationActionButtonsRow(
            pendingAction = pendingAction,
            onAccept = onAccept,
            onReject = onReject
        )
    }
}

@Composable
fun ApplicationActionButtonsRow(
    pendingAction: ApplicationPendingAction?,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading = pendingAction != null

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onAccept,
            enabled = !isLoading,
            modifier = Modifier.weight(1f)
        ) {
            if (pendingAction == ApplicationPendingAction.ACCEPT) {
                FilledButtonLoadingIndicator()
            } else {
                Text(text = "Accept")
            }
        }
        OutlinedButton(
            onClick = onReject,
            enabled = !isLoading,
            modifier = Modifier.weight(1f)
        ) {
            if (pendingAction == ApplicationPendingAction.REJECT) {
                OutlinedButtonLoadingIndicator()
            } else {
                Text(text = "Reject")
            }
        }
    }
}

@Composable
private fun FilledButtonLoadingIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.size(20.dp),
        strokeWidth = 2.dp,
        color = MaterialTheme.colorScheme.onPrimary
    )
}

@Composable
private fun OutlinedButtonLoadingIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.size(20.dp),
        strokeWidth = 2.dp,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun OutlinedButtonLoadingContent(
    isLoading: Boolean,
    label: String
) {
    if (isLoading) {
        OutlinedButtonLoadingIndicator()
    } else {
        Text(text = label)
    }
}
