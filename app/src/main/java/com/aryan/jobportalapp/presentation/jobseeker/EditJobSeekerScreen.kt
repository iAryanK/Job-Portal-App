package com.aryan.jobportalapp.presentation.jobseeker

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aryan.jobportalapp.data.remote.dto.Gender
import com.aryan.jobportalapp.ui.theme.JobPortalAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditJobSeekerScreen(
    modifier: Modifier = Modifier,
    viewModel: EditJobSeekerViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(uiState.toastMessage) {
        val message = uiState.toastMessage ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.consumeToast()
    }

    LaunchedEffect(uiState.navigateBack) {
        if (uiState.navigateBack) {
            onBack()
            viewModel.consumeNavigation()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit profile",
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
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null && uiState.profileId == null -> {
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
                        Button(onClick = viewModel::loadJobSeeker) {
                            Text(text = "Retry")
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = viewModel::updateName,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Full name *") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Gender",
                        style = MaterialTheme.typography.bodyMedium
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
                                label = {
                                    Text(
                                        text = gender.name.lowercase()
                                            .replaceFirstChar { it.titlecase() }
                                    )
                                },
                                enabled = !uiState.isSaving
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
                        enabled = !uiState.isSaving,
                        placeholder = { Text(text = "e.g. Backend Engineer") }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.summary,
                        onValueChange = viewModel::updateSummary,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Summary") },
                        minLines = 3,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.highestQualification,
                        onValueChange = viewModel::updateHighestQualification,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Highest qualification") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.specialization,
                        onValueChange = viewModel::updateSpecialization,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Specialization") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.instituteName,
                        onValueChange = viewModel::updateInstituteName,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Institute") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.experienceYears,
                        onValueChange = viewModel::updateExperienceYears,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Experience (years)") },
                        singleLine = true,
                        enabled = !uiState.isSaving,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.currentCompany,
                        onValueChange = viewModel::updateCurrentCompany,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Current company") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.currentDesignation,
                        onValueChange = viewModel::updateCurrentDesignation,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Current designation") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.preferredJobLocation,
                        onValueChange = viewModel::updatePreferredJobLocation,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Preferred job location") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.city,
                        onValueChange = viewModel::updateCity,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "City") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.expectedCTC,
                        onValueChange = viewModel::updateExpectedCTC,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Expected CTC") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.portfolioUrl,
                        onValueChange = viewModel::updatePortfolioUrl,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Portfolio URL") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.resumeUrl,
                        onValueChange = viewModel::updateResumeUrl,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Resume URL") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = viewModel::save,
                        enabled = uiState.isValid && !uiState.isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = "Save changes")
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EditJobSeekerScreenPreview() {
    JobPortalAppTheme {
        EditJobSeekerScreen()
    }
}
