package com.aryan.jobportalapp.presentation.onboarding

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.aryan.jobportalapp.ui.theme.JobPortalAppTheme

@Composable
fun EmployerOnboardingScreen(
    modifier: Modifier = Modifier,
    viewModel: EmployerOnboardingViewModel = viewModel(),
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
                    "Company details"
                } else {
                    "More about your company"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (uiState.step == 1) {
                    "Tell us the essentials about your organization to get started."
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
                    value = uiState.companyName,
                    onValueChange = viewModel::updateCompanyName,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Company name *") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.industry,
                    onValueChange = viewModel::updateIndustry,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Industry *") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::updateEmail,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Company email *") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.phone,
                    onValueChange = viewModel::updatePhone,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Phone *") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.city,
                    onValueChange = viewModel::updateCity,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "City *") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
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
                    value = uiState.website,
                    onValueChange = viewModel::updateWebsite,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Website") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.tagline,
                    onValueChange = viewModel::updateTagline,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Tagline") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = viewModel::updateDescription,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Description") },
                    minLines = 3,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.companySize,
                    onValueChange = viewModel::updateCompanySize,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Company size") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.establishedYear,
                    onValueChange = viewModel::updateEstablishedYear,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Established year") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.addressLine1,
                    onValueChange = viewModel::updateAddressLine1,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Address") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.state,
                    onValueChange = viewModel::updateState,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "State") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.country,
                    onValueChange = viewModel::updateCountry,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Country") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.pinCode,
                    onValueChange = viewModel::updatePinCode,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "PIN code") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
private fun EmployerOnboardingScreenPreview() {
    JobPortalAppTheme {
        EmployerOnboardingScreen()
    }
}
