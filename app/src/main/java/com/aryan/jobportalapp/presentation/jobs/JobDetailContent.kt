package com.aryan.jobportalapp.presentation.jobs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aryan.jobportalapp.data.remote.dto.JobResponse

/**
 * Reusable job detail body for employer and jobseeker screens.
 * Wrap with a Scaffold and optional action buttons at the call site.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobDetailContent(
    job: JobResponse,
    modifier: Modifier = Modifier,
    showCompany: Boolean = true,
    showStatus: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = job.title.orEmpty().ifBlank { "Untitled role" },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        if (showCompany && !job.employer?.companyName.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val logoUrl = job.employer?.logoUrl
                if (!logoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = logoUrl,
                        contentDescription = job.employer?.companyName,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Business,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Column {
                    Text(
                        text = job.employer?.companyName.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    job.employer?.industry?.takeIf { it.isNotBlank() }?.let { industry ->
                        Text(
                            text = industry,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            formatEmploymentType(job.employmentType)?.let { DetailChip(it) }
            formatWorkplaceType(job.workplaceType)?.let { DetailChip(it) }
            formatExperienceLevel(job.experienceLevel)?.let { DetailChip(it) }
            if (showStatus) {
                formatJobStatus(job.status)?.let { DetailChip(it) }
            }
        }

        val location = formatJobLocation(job.city, job.state, job.country)
        val salary = formatSalaryRange(
            minimum = job.minimumSalary,
            maximum = job.maximumSalary,
            currency = job.currency,
            negotiable = job.salaryNegotiable
        )
        val experience = formatExperienceRange(
            minimum = job.minimumExperience,
            maximum = job.maximumExperience
        )

        if (location != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (salary != null || experience != null || job.vacancies != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = listOfNotNull(
                    salary,
                    experience,
                    job.vacancies?.let { "$it opening${if (it == 1) "" else "s"}" }
                ).joinToString(" · "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))

        DetailSection(title = "About the role", body = job.description)
        DetailSection(title = "Responsibilities", body = job.responsibilities)
        DetailSection(title = "Requirements", body = job.requirements)
        DetailSection(title = "Benefits", body = job.benefits)

        val perks = buildList {
            if (job.relocationAvailable == true) add("Relocation available")
            if (job.accommodationProvided == true) add("Accommodation provided")
            if (job.transportationProvided == true) add("Transportation provided")
            if (job.foodProvided == true) add("Food provided")
        }
        if (perks.isNotEmpty()) {
            Text(
                text = "Perks",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                perks.forEach { DetailChip(it) }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        formatDateLabel(job.applicationDeadline)?.let { deadline ->
            DetailSection(title = "Application deadline", body = deadline)
        }
        formatDateLabel(job.expiresAt)?.let { expires ->
            DetailSection(title = "Expires", body = expires)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DetailSection(title: String, body: String?) {
    if (body.isNullOrBlank()) return

    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = body,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun DetailChip(text: String) {
    AssistChip(
        onClick = {},
        enabled = false,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium
            )
        }
    )
}
