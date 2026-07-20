package com.aryan.jobportalapp.presentation.jobs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aryan.jobportalapp.data.remote.dto.JobSummaryResponse

/**
 * Reusable job summary card for employer My Jobs and jobseeker job lists.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobSummaryCard(
    job: JobSummaryResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showStatus: Boolean = true,
    showCompany: Boolean = true
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            if (showCompany) {
                JobCompanyAvatar(
                    logoUrl = job.employer?.logoUrl,
                    companyName = job.employer?.companyName
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = job.title.orEmpty().ifBlank { "Untitled role" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (showCompany && !job.employer?.companyName.isNullOrBlank()) {
                    Text(
                        text = job.employer?.companyName.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                val traitChips = buildList {
                    formatEmploymentType(job.employmentType)?.let { add(it) }
                    formatWorkplaceType(job.workplaceType)?.let { add(it) }
                    formatExperienceLevel(job.experienceLevel)?.let { add(it) }
                    if (showStatus) {
                        formatJobStatus(job.status)?.let { add(it) }
                    }
                }

                if (traitChips.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        traitChips.forEach { label ->
                            SuggestionChip(
                                onClick = {},
                                enabled = false,
                                label = {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    disabledContainerColor =
                                        MaterialTheme.colorScheme.secondaryContainer,
                                    disabledLabelColor =
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                border = null
                            )
                        }
                    }
                }

                formatJobLocation(job.city, job.state, job.country)?.let { location ->
                    JobMetaRow(
                        icon = Icons.Outlined.LocationOn,
                        text = location
                    )
                }

                formatSalaryRange(
                    minimum = job.minimumSalary,
                    maximum = job.maximumSalary,
                    currency = job.currency,
                    negotiable = job.salaryNegotiable
                )?.let { salary ->
                    JobMetaRow(
                        icon = Icons.Outlined.Payments,
                        text = salary
                    )
                }

                formatExperienceRange(
                    minimum = job.minimumExperience,
                    maximum = job.maximumExperience
                )?.let { experience ->
                    JobMetaRow(
                        icon = Icons.Outlined.WorkOutline,
                        text = experience
                    )
                }
            }
        }
    }
}

@Composable
private fun JobCompanyAvatar(
    logoUrl: String?,
    companyName: String?
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(52.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (!logoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = logoUrl,
                    contentDescription = companyName,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Business,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun JobMetaRow(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
