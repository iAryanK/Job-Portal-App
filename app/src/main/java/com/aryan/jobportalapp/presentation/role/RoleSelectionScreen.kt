package com.aryan.jobportalapp.presentation.role

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aryan.jobportalapp.data.remote.dto.AppUserRole
import com.aryan.jobportalapp.domain.PostAuthDestination
import com.aryan.jobportalapp.ui.theme.JobPortalAppTheme

@Composable
fun RoleSelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: RoleSelectionViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToEmployerOnboarding: () -> Unit = {},
    onNavigateToJobSeekerOnboarding: () -> Unit = {}
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(uiState.toastMessage) {
        val message = uiState.toastMessage ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.consumeToast()
    }

    LaunchedEffect(uiState.destination) {
        when (uiState.destination) {
            PostAuthDestination.EmployerOnboarding -> onNavigateToEmployerOnboarding()
            PostAuthDestination.JobSeekerOnboarding -> onNavigateToJobSeekerOnboarding()
            PostAuthDestination.Home -> onNavigateToHome()
            PostAuthDestination.RoleSelection, null -> Unit
        }
        if (uiState.destination != null) {
            viewModel.consumeDestination()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Choose your role",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Tell us how you want to use JobPortal so we can personalize your experience.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { viewModel.selectRole(AppUserRole.JOBSEEKER) },
                enabled = !uiState.isUpdating,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "I'm a Job Seeker",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { viewModel.selectRole(AppUserRole.EMPLOYER) },
                enabled = !uiState.isUpdating,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "I'm an Employer",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (uiState.isUpdating) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Preview(
    name = "Role Selection",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun RoleSelectionScreenPreview() {
    JobPortalAppTheme {
        RoleSelectionScreen()
    }
}
