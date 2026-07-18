package com.aryan.jobportalapp.presentation.employer

import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditJobScreen(
    jobId: String? = null,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    viewModel: CreateEditJobViewModel = viewModel(
        key = "job_form_${jobId ?: "new"}",
        initializer = { CreateEditJobViewModel(jobId = jobId) }
    )
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
                        text = uiState.screenTitle,
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

            uiState.errorMessage != null && uiState.mode == JobFormMode.EDIT -> {
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

            else -> {
                JobFormContent(
                    uiState = uiState,
                    onTitleChange = viewModel::updateTitle,
                    onDescriptionChange = viewModel::updateDescription,
                    onResponsibilitiesChange = viewModel::updateResponsibilities,
                    onRequirementsChange = viewModel::updateRequirements,
                    onBenefitsChange = viewModel::updateBenefits,
                    onEmploymentTypeChange = viewModel::updateEmploymentType,
                    onWorkplaceTypeChange = viewModel::updateWorkplaceType,
                    onExperienceLevelChange = viewModel::updateExperienceLevel,
                    onStatusChange = viewModel::updateStatus,
                    onVacanciesChange = viewModel::updateVacancies,
                    onMinimumExperienceChange = viewModel::updateMinimumExperience,
                    onMaximumExperienceChange = viewModel::updateMaximumExperience,
                    onMinimumSalaryChange = viewModel::updateMinimumSalary,
                    onMaximumSalaryChange = viewModel::updateMaximumSalary,
                    onSalaryNegotiableChange = viewModel::updateSalaryNegotiable,
                    onCurrencyChange = viewModel::updateCurrency,
                    onCityChange = viewModel::updateCity,
                    onDistrictChange = viewModel::updateDistrict,
                    onStateChange = viewModel::updateState,
                    onCountryChange = viewModel::updateCountry,
                    onPinCodeChange = viewModel::updatePinCode,
                    onRelocationAvailableChange = viewModel::updateRelocationAvailable,
                    onAccommodationProvidedChange = viewModel::updateAccommodationProvided,
                    onTransportationProvidedChange = viewModel::updateTransportationProvided,
                    onFoodProvidedChange = viewModel::updateFoodProvided,
                    onApplicationDeadlineChange = viewModel::updateApplicationDeadline,
                    onExpiresAtChange = viewModel::updateExpiresAt,
                    onSubmit = viewModel::submit,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
