package com.aryan.jobportalapp.presentation.employer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
