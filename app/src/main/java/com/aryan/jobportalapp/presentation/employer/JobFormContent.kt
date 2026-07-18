package com.aryan.jobportalapp.presentation.employer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aryan.jobportalapp.data.remote.dto.CurrencyType
import com.aryan.jobportalapp.data.remote.dto.EmploymentType
import com.aryan.jobportalapp.data.remote.dto.ExperienceLevel
import com.aryan.jobportalapp.data.remote.dto.JobStatus
import com.aryan.jobportalapp.data.remote.dto.WorkplaceType
import com.aryan.jobportalapp.presentation.common.EnumDropdownField
import com.aryan.jobportalapp.presentation.common.enumLabel

@Composable
fun JobFormContent(
    uiState: JobFormUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onResponsibilitiesChange: (String) -> Unit,
    onRequirementsChange: (String) -> Unit,
    onBenefitsChange: (String) -> Unit,
    onEmploymentTypeChange: (EmploymentType) -> Unit,
    onWorkplaceTypeChange: (WorkplaceType) -> Unit,
    onExperienceLevelChange: (ExperienceLevel) -> Unit,
    onStatusChange: (JobStatus) -> Unit,
    onVacanciesChange: (String) -> Unit,
    onMinimumExperienceChange: (String) -> Unit,
    onMaximumExperienceChange: (String) -> Unit,
    onMinimumSalaryChange: (String) -> Unit,
    onMaximumSalaryChange: (String) -> Unit,
    onSalaryNegotiableChange: (Boolean) -> Unit,
    onCurrencyChange: (CurrencyType) -> Unit,
    onCityChange: (String) -> Unit,
    onDistrictChange: (String) -> Unit,
    onStateChange: (String) -> Unit,
    onCountryChange: (String) -> Unit,
    onPinCodeChange: (String) -> Unit,
    onRelocationAvailableChange: (Boolean) -> Unit,
    onAccommodationProvidedChange: (Boolean) -> Unit,
    onTransportationProvidedChange: (Boolean) -> Unit,
    onFoodProvidedChange: (Boolean) -> Unit,
    onApplicationDeadlineChange: (String) -> Unit,
    onExpiresAtChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val enabled = !uiState.isSaving

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        FormSectionTitle(text = "Basic details")

        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Job title *") },
            singleLine = true,
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.description,
            onValueChange = onDescriptionChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Description *") },
            minLines = 3,
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.responsibilities,
            onValueChange = onResponsibilitiesChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Responsibilities") },
            minLines = 2,
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.requirements,
            onValueChange = onRequirementsChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Requirements") },
            minLines = 2,
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.benefits,
            onValueChange = onBenefitsChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Benefits") },
            minLines = 2,
            enabled = enabled
        )

        Spacer(modifier = Modifier.height(20.dp))
        FormSectionTitle(text = "Job type")

        EnumDropdownField(
            label = "Employment type *",
            options = EmploymentType.entries,
            selected = uiState.employmentType,
            onSelected = onEmploymentTypeChange,
            optionLabel = ::enumLabel,
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(12.dp))
        EnumDropdownField(
            label = "Workplace type *",
            options = WorkplaceType.entries,
            selected = uiState.workplaceType,
            onSelected = onWorkplaceTypeChange,
            optionLabel = ::enumLabel,
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(12.dp))
        EnumDropdownField(
            label = "Experience level *",
            options = ExperienceLevel.entries,
            selected = uiState.experienceLevel,
            onSelected = onExperienceLevelChange,
            optionLabel = ::enumLabel,
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(12.dp))
        EnumDropdownField(
            label = "Status",
            options = JobStatus.entries,
            selected = uiState.status,
            onSelected = onStatusChange,
            optionLabel = ::enumLabel,
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.vacancies,
            onValueChange = onVacanciesChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Vacancies") },
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(20.dp))
        FormSectionTitle(text = "Experience & compensation")

        OutlinedTextField(
            value = uiState.minimumExperience,
            onValueChange = onMinimumExperienceChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Minimum experience (years)") },
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.maximumExperience,
            onValueChange = onMaximumExperienceChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Maximum experience (years)") },
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(12.dp))
        EnumDropdownField(
            label = "Currency",
            options = CurrencyType.entries,
            selected = uiState.currency,
            onSelected = onCurrencyChange,
            optionLabel = ::enumLabel,
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.minimumSalary,
            onValueChange = onMinimumSalaryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Minimum salary") },
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.maximumSalary,
            onValueChange = onMaximumSalaryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Maximum salary") },
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Spacer(modifier = Modifier.height(8.dp))
        BooleanField(
            label = "Salary negotiable",
            checked = uiState.salaryNegotiable,
            onCheckedChange = onSalaryNegotiableChange,
            enabled = enabled
        )

        Spacer(modifier = Modifier.height(20.dp))
        FormSectionTitle(text = "Location")

        OutlinedTextField(
            value = uiState.city,
            onValueChange = onCityChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "City *") },
            singleLine = true,
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.district,
            onValueChange = onDistrictChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "District") },
            singleLine = true,
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.state,
            onValueChange = onStateChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "State") },
            singleLine = true,
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.country,
            onValueChange = onCountryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Country") },
            singleLine = true,
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.pinCode,
            onValueChange = onPinCodeChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "PIN code") },
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(20.dp))
        FormSectionTitle(text = "Perks")

        BooleanField(
            label = "Relocation available",
            checked = uiState.relocationAvailable,
            onCheckedChange = onRelocationAvailableChange,
            enabled = enabled
        )
        BooleanField(
            label = "Accommodation provided",
            checked = uiState.accommodationProvided,
            onCheckedChange = onAccommodationProvidedChange,
            enabled = enabled
        )
        BooleanField(
            label = "Transportation provided",
            checked = uiState.transportationProvided,
            onCheckedChange = onTransportationProvidedChange,
            enabled = enabled
        )
        BooleanField(
            label = "Food provided",
            checked = uiState.foodProvided,
            onCheckedChange = onFoodProvidedChange,
            enabled = enabled
        )

        Spacer(modifier = Modifier.height(20.dp))
        FormSectionTitle(text = "Timeline")

        OutlinedTextField(
            value = uiState.applicationDeadline,
            onValueChange = onApplicationDeadlineChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Application deadline") },
            placeholder = { Text(text = "YYYY-MM-DD") },
            singleLine = true,
            enabled = enabled
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.expiresAt,
            onValueChange = onExpiresAtChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Expires at") },
            placeholder = { Text(text = "YYYY-MM-DD") },
            singleLine = true,
            enabled = enabled
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onSubmit,
            enabled = uiState.isValid && !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = uiState.submitLabel)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun FormSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun BooleanField(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}
