package com.aryan.jobportalapp.presentation.employer

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.aryan.jobportalapp.ui.theme.JobPortalAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmployerScreen(
    modifier: Modifier = Modifier,
    viewModel: EditEmployerViewModel = viewModel(),
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
                        text = "Edit company",
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
                        Button(onClick = viewModel::loadEmployer) {
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
                        value = uiState.companyName,
                        onValueChange = viewModel::updateCompanyName,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Company name *") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.industry,
                        onValueChange = viewModel::updateIndustry,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Industry *") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = viewModel::updateEmail,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Company email *") },
                        singleLine = true,
                        enabled = !uiState.isSaving,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.phone,
                        onValueChange = viewModel::updatePhone,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Phone *") },
                        singleLine = true,
                        enabled = !uiState.isSaving,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.city,
                        onValueChange = viewModel::updateCity,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "City *") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.website,
                        onValueChange = viewModel::updateWebsite,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Website") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.tagline,
                        onValueChange = viewModel::updateTagline,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Tagline") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = viewModel::updateDescription,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Description") },
                        minLines = 3,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.companySize,
                        onValueChange = viewModel::updateCompanySize,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Company size") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.establishedYear,
                        onValueChange = viewModel::updateEstablishedYear,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Established year") },
                        singleLine = true,
                        enabled = !uiState.isSaving,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.addressLine1,
                        onValueChange = viewModel::updateAddressLine1,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Address") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.state,
                        onValueChange = viewModel::updateState,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "State") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.country,
                        onValueChange = viewModel::updateCountry,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Country") },
                        singleLine = true,
                        enabled = !uiState.isSaving
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.pinCode,
                        onValueChange = viewModel::updatePinCode,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "PIN code") },
                        singleLine = true,
                        enabled = !uiState.isSaving,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
private fun EditEmployerScreenPreview() {
    JobPortalAppTheme {
        EditEmployerScreen()
    }
}
