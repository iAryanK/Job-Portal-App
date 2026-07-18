package com.aryan.jobportalapp.presentation.onboarding

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aryan.jobportalapp.data.remote.dto.Gender
import com.aryan.jobportalapp.ui.theme.JobPortalAppTheme

@Composable
fun JobSeekerOnboardingScreen(
    modifier: Modifier = Modifier,
    viewModel: JobSeekerOnboardingViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {}
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(uiState.toastMessage) {
        val message = uiState.toastMessage ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.consumeToast()
    }

    LaunchedEffect(uiState.navigateToHome) {
        if (uiState.navigateToHome) {
            onNavigateToHome()
            viewModel.consumeNavigation()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (uiState.step == 2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    TextButton(
                        onClick = { viewModel.skipOptionalStep() },
                        modifier = Modifier.align(Alignment.CenterEnd),
                        enabled = !uiState.isSubmitting
                    ) {
                        Text(text = "Skip")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (uiState.step == 1) {
                    "Your profile"
                } else {
                    "More about you"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (uiState.step == 1) {
                    "Add the basics so employers can find you."
                } else {
                    "These details are optional. You can skip and complete them later."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.step == 1) {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::updateName,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Full name *") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Gender",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Gender.entries.forEach { gender ->
                        FilterChip(
                            selected = uiState.gender == gender,
                            onClick = { viewModel.updateGender(gender) },
                            label = { Text(text = gender.name.lowercase().replaceFirstChar { it.titlecase() }) },
                            enabled = !uiState.isSubmitting
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.headline,
                    onValueChange = viewModel::updateHeadline,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Headline") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting,
                    placeholder = { Text(text = "e.g. Backend Engineer") }
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.submitStep1() },
                    enabled = uiState.isStep1Valid && !uiState.isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Next")
                    }
                }
            } else {
                OutlinedTextField(
                    value = uiState.highestQualification,
                    onValueChange = viewModel::updateHighestQualification,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Highest qualification") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.specialization,
                    onValueChange = viewModel::updateSpecialization,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Specialization") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.instituteName,
                    onValueChange = viewModel::updateInstituteName,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Institute") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.experienceYears,
                    onValueChange = viewModel::updateExperienceYears,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Experience (years)") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.currentCompany,
                    onValueChange = viewModel::updateCurrentCompany,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Current company") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.currentDesignation,
                    onValueChange = viewModel::updateCurrentDesignation,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Current designation") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.preferredJobLocation,
                    onValueChange = viewModel::updatePreferredJobLocation,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Preferred job location") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.city,
                    onValueChange = viewModel::updateCity,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "City") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.expectedCTC,
                    onValueChange = viewModel::updateExpectedCTC,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Expected CTC") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.summary,
                    onValueChange = viewModel::updateSummary,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Summary") },
                    minLines = 3,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.submitStep2() },
                    enabled = !uiState.isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Finish")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun JobSeekerOnboardingScreenPreview() {
    JobPortalAppTheme {
        JobSeekerOnboardingScreen()
    }
}
