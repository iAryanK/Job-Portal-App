package com.aryan.jobportalapp.presentation.employer

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
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aryan.jobportalapp.data.remote.dto.JobSeekerSummaryResponse
import com.aryan.jobportalapp.presentation.common.enumLabel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobSeekerSummaryCard(
    jobSeeker: JobSeekerSummaryResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = jobSeeker.name.orEmpty().ifBlank { "Unnamed candidate" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                jobSeeker.headline?.takeIf { it.isNotBlank() }?.let { headline ->
                    Text(
                        text = headline,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                formatJobSeekerExperience(
                    years = jobSeeker.experienceYears,
                    months = jobSeeker.experienceMonths
                )?.let { experience ->
                    MetaRow(
                        icon = Icons.Outlined.WorkOutline,
                        text = experience
                    )
                }

                formatJobSeekerLocation(
                    city = jobSeeker.city,
                    state = jobSeeker.state,
                    country = jobSeeker.country
                )?.let { location ->
                    MetaRow(
                        icon = Icons.Outlined.LocationOn,
                        text = location
                    )
                }

                val qualificationLine = listOfNotNull(
                    jobSeeker.highestQualification?.takeIf { it.isNotBlank() },
                    jobSeeker.specialization?.takeIf { it.isNotBlank() }
                ).joinToString(" · ")

                if (qualificationLine.isNotBlank()) {
                    MetaRow(
                        icon = Icons.Outlined.School,
                        text = qualificationLine
                    )
                }

                val traitChips = buildList {
                    if (jobSeeker.openToRemote == true) add("Remote")
                    if (jobSeeker.willingToRelocate == true) add("Relocate")
                    if (jobSeeker.currentlyWorking == true) add("Employed")
                    jobSeeker.gender?.let { add(enumLabel(it)) }
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
            }
        }
    }
}

@Composable
private fun MetaRow(
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

private fun formatJobSeekerExperience(years: Int?, months: Int?): String? {
    return when {
        years != null && years > 0 && months != null && months > 0 ->
            "$years yr $months mo exp"
        years != null && years > 0 -> "$years yr exp"
        years == 0 && months != null && months > 0 -> "$months mo exp"
        years == 0 -> "Fresher"
        else -> null
    }
}

private fun formatJobSeekerLocation(
    city: String?,
    state: String?,
    country: String?
): String? {
    val parts = listOfNotNull(
        city?.takeIf { it.isNotBlank() },
        state?.takeIf { it.isNotBlank() },
        country?.takeIf { it.isNotBlank() }
    )
    return parts.takeIf { it.isNotEmpty() }?.joinToString(", ")
}
