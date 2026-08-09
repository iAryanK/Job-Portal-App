package com.aryan.jobportalapp.presentation.jobs

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.aryan.jobportalapp.presentation.common.PullRefreshContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    jobId: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onEditClick: ((String) -> Unit)? = null,
    showCompany: Boolean = true,
    showStatus: Boolean = true,
    viewModel: JobDetailViewModel = viewModel(
        key = "job_detail_$jobId",
        initializer = { JobDetailViewModel(jobId = jobId) }
    )
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    var showApplyDialog by rememberSaveable { mutableStateOf(false) }
    var applicationMessage by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(uiState.toastMessage) {
        val message = uiState.toastMessage ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.consumeToast()
    }

    if (showApplyDialog) {
        AlertDialog(
            onDismissRequest = { showApplyDialog = false },
            title = { Text(text = "Apply for this job") },
            text = {
                OutlinedTextField(
                    value = applicationMessage,
                    onValueChange = { applicationMessage = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Message (optional)") },
                    minLines = 3,
                    maxLines = 5
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showApplyDialog = false
                        viewModel.apply(applicationMessage)
                        applicationMessage = ""
                    },
                    enabled = !uiState.isApplying
                ) {
                    Text(text = "Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApplyDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Job details",
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
                },
                actions = {
                    if (uiState.showEditButton && onEditClick != null) {
                        IconButton(onClick = { onEditClick(uiState.job!!.id!!) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit job"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.showApplyButton) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = {
                            if (uiState.hasApplied) return@Button
                            showApplyDialog = true
                        },
                        enabled = !uiState.hasApplied && !uiState.isApplying,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isApplying) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (uiState.hasApplied) "Applied" else "Apply"
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        when {
            uiState.isLoading && uiState.job == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null && uiState.job == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadJob() }) {
                            Text(text = "Retry")
                        }
                    }
                }
            }

            uiState.job != null -> {
                PullRefreshContainer(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { viewModel.loadJob(isRefresh = true) },
                    modifier = Modifier.padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        JobDetailContent(
                            job = uiState.job!!,
                            showCompany = showCompany,
                            showStatus = showStatus
                        )
                    }
                }
            }
        }
    }
}
