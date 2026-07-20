package com.aryan.jobportalapp.presentation.jobs

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aryan.jobportalapp.data.remote.dto.CurrencyType
import com.aryan.jobportalapp.data.remote.dto.EmploymentType
import com.aryan.jobportalapp.data.remote.dto.ExperienceLevel
import com.aryan.jobportalapp.data.remote.dto.FindJobsFilter
import com.aryan.jobportalapp.data.remote.dto.JobSortOption
import com.aryan.jobportalapp.data.remote.dto.WorkplaceType
import com.aryan.jobportalapp.presentation.common.EnumDropdownField
import com.aryan.jobportalapp.presentation.common.PullRefreshContainer
import com.aryan.jobportalapp.presentation.common.enumLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsScreen(
    modifier: Modifier = Modifier,
    onJobClick: (String) -> Unit = {},
    viewModel: JobsViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            if (totalItems == 0) return@derivedStateOf false
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex >= totalItems - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadMore()
        }
    }

    if (uiState.showAdvancedFilters) {
        JobsAdvancedFiltersSheet(
            filters = uiState.draftFilters,
            onDismiss = viewModel::dismissAdvancedFilters,
            onApply = viewModel::applyAdvancedFilters,
            onClear = viewModel::clearAdvancedFilters,
            onFiltersChange = viewModel::updateDraftFilters
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        PullRefreshContainer(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::pullToRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                uiState.isLoading && !uiState.hasLoadedOnce -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.errorMessage != null &&
                    !uiState.hasLoadedOnce &&
                    uiState.jobs.isEmpty() -> {
                    JobsErrorState(
                        message = uiState.errorMessage.orEmpty(),
                        onRetry = viewModel::retryLoad
                    )
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            JobsSearchField(
                                query = uiState.searchQuery,
                                onQueryChange = viewModel::onSearchQueryChange
                            )
                            JobsQuickFilters(
                                filters = uiState.filters,
                                sortOption = uiState.sortOption,
                                onToggleRemote = {
                                    viewModel.toggleQuickFilter {
                                        copy(
                                            remoteOnly = !remoteOnly,
                                            hybridOnly = if (!remoteOnly) false else hybridOnly
                                        )
                                    }
                                },
                                onToggleHybrid = {
                                    viewModel.toggleQuickFilter {
                                        copy(
                                            hybridOnly = !hybridOnly,
                                            remoteOnly = if (!hybridOnly) false else remoteOnly
                                        )
                                    }
                                },
                                onToggleFullTime = {
                                    viewModel.toggleQuickFilter {
                                        copy(fullTimeOnly = !fullTimeOnly)
                                    }
                                },
                                onToggleRelocation = {
                                    viewModel.toggleQuickFilter {
                                        copy(relocationAvailable = !relocationAvailable)
                                    }
                                },
                                onSortSelected = viewModel::onSortOptionSelected,
                                onClearAll = viewModel::clearAllFilters,
                                activeFilterCount = uiState.activeFilterCount
                            )
                        }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 12.dp,
                                bottom = 88.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (uiState.totalElements > 0) {
                                item {
                                    Text(
                                        text = "${uiState.totalElements} jobs found",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }

                            if (uiState.isReloadingList) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                }
                            }

                            if (uiState.errorMessage != null && uiState.jobs.isEmpty()) {
                                item {
                                    JobsInlineError(
                                        message = uiState.errorMessage.orEmpty(),
                                        onRetry = viewModel::retryLoad
                                    )
                                }
                            } else if (uiState.jobs.isEmpty()) {
                                item {
                                    JobsEmptyState(
                                        hasActiveFilters = uiState.activeFilterCount > 0 ||
                                            uiState.searchQuery.isNotBlank()
                                    )
                                }
                            } else {
                                items(
                                    items = uiState.jobs,
                                    key = { it.id.orEmpty() }
                                ) { job ->
                                    JobSummaryCard(
                                        job = job,
                                        onClick = { job.id?.let(onJobClick) },
                                        showStatus = false,
                                        showCompany = true
                                    )
                                }

                                if (uiState.isLoadingMore) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(28.dp),
                                                strokeWidth = 3.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        BadgedBox(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            badge = {
                if (uiState.filters.advancedFilterCount > 0) {
                    Badge {
                        Text(text = uiState.filters.advancedFilterCount.toString())
                    }
                }
            }
        ) {
            ExtendedFloatingActionButton(
                onClick = viewModel::openAdvancedFilters,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = null
                    )
                },
                text = { Text(text = "Filters") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun JobsSearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = {
            Text(
                text = "Search jobs, companies, skills…",
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { /* debounced search runs automatically */ }
        )
    )
}

@Composable
private fun JobsQuickFilters(
    filters: FindJobsFilter,
    sortOption: JobSortOption,
    onToggleRemote: () -> Unit,
    onToggleHybrid: () -> Unit,
    onToggleFullTime: () -> Unit,
    onToggleRelocation: () -> Unit,
    onSortSelected: (JobSortOption) -> Unit,
    onClearAll: () -> Unit,
    activeFilterCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            JobsFilterChip(
                selected = filters.remoteOnly,
                onClick = onToggleRemote,
                label = "Remote"
            )
            JobsFilterChip(
                selected = filters.hybridOnly,
                onClick = onToggleHybrid,
                label = "Hybrid"
            )
            JobsFilterChip(
                selected = filters.fullTimeOnly,
                onClick = onToggleFullTime,
                label = "Full time"
            )
            JobsFilterChip(
                selected = filters.relocationAvailable,
                onClick = onToggleRelocation,
                label = "Relocation"
            )
            if (activeFilterCount > 0) {
                TextButton(onClick = onClearAll) {
                    Text(text = "Clear all")
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            JobSortOption.entries.forEach { option ->
                FilterChip(
                    selected = sortOption == option,
                    onClick = { onSortSelected(option) },
                    label = { Text(text = option.label) },
                    leadingIcon = if (sortOption == option) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }
    }
}

@Composable
private fun JobsFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label) },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JobsAdvancedFiltersSheet(
    filters: FindJobsFilter,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit,
    onFiltersChange: (FindJobsFilter.() -> FindJobsFilter) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Column {
                    Text(
                        text = "Advanced filters",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Refine by role type, location, salary, and benefits",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider()

            Text(
                text = "Job details",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            EnumDropdownField(
                label = "Employment type",
                options = EmploymentType.entries,
                selected = filters.employmentType,
                onSelected = { value -> onFiltersChange { copy(employmentType = value) } },
                optionLabel = ::enumLabel
            )

            EnumDropdownField(
                label = "Workplace type",
                options = WorkplaceType.entries,
                selected = filters.workplaceType,
                onSelected = { value -> onFiltersChange { copy(workplaceType = value) } },
                optionLabel = ::enumLabel
            )

            EnumDropdownField(
                label = "Experience level",
                options = ExperienceLevel.entries,
                selected = filters.experienceLevel,
                onSelected = { value -> onFiltersChange { copy(experienceLevel = value) } },
                optionLabel = ::enumLabel
            )

            EnumDropdownField(
                label = "Currency",
                options = CurrencyType.entries,
                selected = filters.currency,
                onSelected = { value -> onFiltersChange { copy(currency = value) } },
                optionLabel = ::enumLabel
            )

            OutlinedTextField(
                value = filters.industry,
                onValueChange = { value -> onFiltersChange { copy(industry = value) } },
                label = { Text(text = "Industry") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Text(
                text = "Location",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = filters.city,
                onValueChange = { value -> onFiltersChange { copy(city = value) } },
                label = { Text(text = "City") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = filters.district,
                onValueChange = { value -> onFiltersChange { copy(district = value) } },
                label = { Text(text = "District") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = filters.state,
                onValueChange = { value -> onFiltersChange { copy(state = value) } },
                label = { Text(text = "State") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = filters.country,
                onValueChange = { value -> onFiltersChange { copy(country = value) } },
                label = { Text(text = "Country") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Text(
                text = "Salary & experience",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = filters.minSalary,
                    onValueChange = { value ->
                        onFiltersChange { copy(minSalary = value.filter { it.isDigit() || it == '.' }) }
                    },
                    label = { Text(text = "Min salary") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
                OutlinedTextField(
                    value = filters.maxSalary,
                    onValueChange = { value ->
                        onFiltersChange { copy(maxSalary = value.filter { it.isDigit() || it == '.' }) }
                    },
                    label = { Text(text = "Max salary") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = filters.minExperience,
                    onValueChange = { value ->
                        onFiltersChange { copy(minExperience = value.filter { it.isDigit() }) }
                    },
                    label = { Text(text = "Min exp (yrs)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
                OutlinedTextField(
                    value = filters.maxExperience,
                    onValueChange = { value ->
                        onFiltersChange { copy(maxExperience = value.filter { it.isDigit() }) }
                    },
                    label = { Text(text = "Max exp (yrs)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
            }

            Text(
                text = "Benefits",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            JobsBenefitToggle(
                label = "Salary negotiable",
                checked = filters.salaryNegotiable,
                onCheckedChange = { checked ->
                    onFiltersChange { copy(salaryNegotiable = checked) }
                }
            )
            JobsBenefitToggle(
                label = "Relocation available",
                checked = filters.relocationAvailable,
                onCheckedChange = { checked ->
                    onFiltersChange { copy(relocationAvailable = checked) }
                }
            )
            JobsBenefitToggle(
                label = "Accommodation provided",
                checked = filters.accommodationProvided,
                onCheckedChange = { checked ->
                    onFiltersChange { copy(accommodationProvided = checked) }
                }
            )
            JobsBenefitToggle(
                label = "Transportation provided",
                checked = filters.transportationProvided,
                onCheckedChange = { checked ->
                    onFiltersChange { copy(transportationProvided = checked) }
                }
            )
            JobsBenefitToggle(
                label = "Food provided",
                checked = filters.foodProvided,
                onCheckedChange = { checked ->
                    onFiltersChange { copy(foodProvided = checked) }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onClear,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Clear")
                }
                Button(
                    onClick = onApply,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Apply filters")
                }
            }
        }
    }
}

@Composable
private fun JobsBenefitToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun JobsEmptyState(hasActiveFilters: Boolean) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.WorkOutline,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = if (hasActiveFilters) "No jobs match your filters" else "No jobs available",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = if (hasActiveFilters) {
                    "Try adjusting your search or filter criteria."
                } else {
                    "New openings will appear here as employers post jobs."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun JobsInlineError(
    message: String,
    onRetry: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
                Text(text = "Retry")
            }
        }
    }
}

@Composable
private fun JobsErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text(text = "Retry")
            }
        }
    }
}
