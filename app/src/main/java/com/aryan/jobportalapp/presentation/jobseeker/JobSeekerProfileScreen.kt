package com.aryan.jobportalapp.presentation.jobseeker

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.WorkHistory
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aryan.jobportalapp.data.remote.dto.Gender
import com.aryan.jobportalapp.data.remote.dto.JobSeekerResponse
import com.aryan.jobportalapp.presentation.common.PullRefreshContainer
import com.aryan.jobportalapp.ui.theme.JobPortalAppTheme

@Composable
fun JobSeekerProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: JobSeekerProfileViewModel = viewModel(),
    onEditClick: () -> Unit = {}
) {
    val uiState = viewModel.uiState

    when {
        uiState.isLoading && uiState.jobSeeker == null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.errorMessage != null && uiState.jobSeeker == null -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
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
                    Button(onClick = viewModel::loadProfile) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Retry")
                    }
                }
            }
        }

        else -> {
            uiState.jobSeeker?.let { jobSeeker ->
                PullRefreshContainer(
                    isRefreshing = uiState.isLoading,
                    onRefresh = viewModel::loadProfile,
                    modifier = modifier
                ) {
                    JobSeekerProfileContent(
                        jobSeeker = jobSeeker,
                        profilePhotoUrl = uiState.profilePhotoUrl,
                        isOwnProfile = uiState.isOwnProfile,
                        onEditClick = onEditClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun JobSeekerProfileContent(
    jobSeeker: JobSeekerResponse,
    modifier: Modifier = Modifier,
    profilePhotoUrl: String? = null,
    isOwnProfile: Boolean = false,
    onEditClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                colorScheme.primaryContainer,
                                colorScheme.primary.copy(alpha = 0.75f),
                                colorScheme.tertiary.copy(alpha = 0.55f)
                            )
                        )
                    )
            ) {
                if (isOwnProfile) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                color = colorScheme.surface.copy(alpha = 0.92f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit profile",
                            tint = colorScheme.primary
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(start = 24.dp)
                    .offset(y = 124.dp)
                    .size(96.dp)
                    .clip(CircleShape)
                    .border(3.dp, colorScheme.background, CircleShape)
                    .background(colorScheme.surface)
            ) {
                if (!profilePhotoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = profilePhotoUrl,
                        contentDescription = "Profile photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(40.dp),
                        tint = colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(52.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = jobSeeker.name.orEmpty().ifBlank { "Your profile" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (!jobSeeker.headline.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = jobSeeker.headline,
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                jobSeeker.highestQualification?.takeIf { it.isNotBlank() }?.let {
                    InfoChip(label = it)
                }
                jobSeeker.experienceYears?.let { years ->
                    val label = if (years == 1) "1 year exp" else "$years years exp"
                    InfoChip(label = label)
                }
                jobSeeker.city?.takeIf { it.isNotBlank() }?.let {
                    InfoChip(label = it)
                }
                jobSeeker.gender?.let {
                    InfoChip(label = it.name.lowercase().replaceFirstChar(Char::titlecase))
                }
            }

            if (!jobSeeker.summary.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(28.dp))
                SectionTitle(title = "About")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = jobSeeker.summary,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurface
                )
            }

            val hasEducation = listOf(
                jobSeeker.highestQualification,
                jobSeeker.specialization,
                jobSeeker.instituteName
            ).any { !it.isNullOrBlank() } || jobSeeker.passingYear != null || jobSeeker.cgpa != null

            if (hasEducation) {
                Spacer(modifier = Modifier.height(28.dp))
                SectionTitle(title = "Education")
                Spacer(modifier = Modifier.height(12.dp))
                jobSeeker.highestQualification?.takeIf { it.isNotBlank() }?.let {
                    DetailRow(icon = Icons.Outlined.School, label = "Qualification", value = it)
                }
                jobSeeker.specialization?.takeIf { it.isNotBlank() }?.let {
                    DetailRow(icon = Icons.Outlined.Badge, label = "Specialization", value = it)
                }
                jobSeeker.instituteName?.takeIf { it.isNotBlank() }?.let {
                    DetailRow(icon = Icons.Outlined.School, label = "Institute", value = it)
                }
                jobSeeker.passingYear?.let {
                    DetailRow(icon = Icons.Outlined.Badge, label = "Passing year", value = it.toString())
                }
                jobSeeker.cgpa?.let {
                    DetailRow(icon = Icons.Outlined.Badge, label = "CGPA", value = it.toString())
                }
            }

            val hasExperience = listOf(
                jobSeeker.currentCompany,
                jobSeeker.currentDesignation,
                jobSeeker.currentCTC,
                jobSeeker.expectedCTC
            ).any { !it.isNullOrBlank() } ||
                jobSeeker.experienceYears != null ||
                jobSeeker.experienceMonths != null

            if (hasExperience) {
                Spacer(modifier = Modifier.height(28.dp))
                SectionTitle(title = "Experience")
                Spacer(modifier = Modifier.height(12.dp))
                formatExperience(jobSeeker.experienceYears, jobSeeker.experienceMonths)?.let {
                    DetailRow(icon = Icons.Outlined.WorkHistory, label = "Experience", value = it)
                }
                jobSeeker.currentCompany?.takeIf { it.isNotBlank() }?.let {
                    DetailRow(icon = Icons.Outlined.BusinessCenter, label = "Current company", value = it)
                }
                jobSeeker.currentDesignation?.takeIf { it.isNotBlank() }?.let {
                    DetailRow(icon = Icons.Outlined.Badge, label = "Designation", value = it)
                }
                jobSeeker.currentCTC?.takeIf { it.isNotBlank() }?.let {
                    DetailRow(icon = Icons.Outlined.Badge, label = "Current CTC", value = it)
                }
                jobSeeker.expectedCTC?.takeIf { it.isNotBlank() }?.let {
                    DetailRow(icon = Icons.Outlined.Badge, label = "Expected CTC", value = it)
                }
            }

            val hasPreferences = !jobSeeker.preferredJobLocation.isNullOrBlank() ||
                jobSeeker.willingToRelocate != null ||
                jobSeeker.openToRemote != null

            if (hasPreferences) {
                Spacer(modifier = Modifier.height(28.dp))
                SectionTitle(title = "Preferences")
                Spacer(modifier = Modifier.height(12.dp))
                jobSeeker.preferredJobLocation?.takeIf { it.isNotBlank() }?.let {
                    DetailRow(
                        icon = Icons.Default.LocationOn,
                        label = "Preferred location",
                        value = it
                    )
                }
                jobSeeker.willingToRelocate?.let {
                    DetailRow(
                        icon = Icons.Outlined.Badge,
                        label = "Willing to relocate",
                        value = if (it) "Yes" else "No"
                    )
                }
                jobSeeker.openToRemote?.let {
                    DetailRow(
                        icon = Icons.Outlined.Badge,
                        label = "Open to remote",
                        value = if (it) "Yes" else "No"
                    )
                }
            }

            val addressParts = listOf(
                jobSeeker.currentAddressLine1,
                jobSeeker.currentAddressLine2,
                jobSeeker.city,
                jobSeeker.district,
                jobSeeker.state,
                jobSeeker.country,
                jobSeeker.pinCode
            ).filter { !it.isNullOrBlank() }

            if (addressParts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(28.dp))
                SectionTitle(title = "Location")
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = addressParts.joinToString(", "),
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorScheme.onSurface
                    )
                }
            }

            val hasLinks = !jobSeeker.portfolioUrl.isNullOrBlank() ||
                !jobSeeker.resumeUrl.isNullOrBlank()
            if (hasLinks) {
                Spacer(modifier = Modifier.height(28.dp))
                SectionTitle(title = "Links")
                Spacer(modifier = Modifier.height(12.dp))
                jobSeeker.portfolioUrl?.takeIf { it.isNotBlank() }?.let { url ->
                    LinkRow(
                        icon = Icons.Default.Language,
                        label = url.removePrefix("https://").removePrefix("http://"),
                        onClick = {
                            val fullUrl = if (url.startsWith("http")) url else "https://$url"
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl)))
                        }
                    )
                }
                jobSeeker.resumeUrl?.takeIf { it.isNotBlank() }?.let { url ->
                    LinkRow(
                        icon = Icons.Default.Description,
                        label = "Resume",
                        onClick = {
                            val fullUrl = if (url.startsWith("http")) url else "https://$url"
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl)))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun formatExperience(years: Int?, months: Int?): String? {
    if (years == null && months == null) return null
    val parts = buildList {
        years?.takeIf { it > 0 }?.let { add(if (it == 1) "1 year" else "$it years") }
        months?.takeIf { it > 0 }?.let { add(if (it == 1) "1 month" else "$it months") }
    }
    return parts.joinToString(" ").ifBlank { null }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(6.dp))
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
}

@Composable
private fun InfoChip(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
private fun LinkRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun JobSeekerProfilePreview() {
    JobPortalAppTheme {
        JobSeekerProfileContent(
            jobSeeker = JobSeekerResponse(
                id = "preview-user",
                name = "Aryan Sharma",
                headline = "Android Developer",
                gender = Gender.MALE,
                summary = "Passionate about building clean Android apps with Kotlin and Compose.",
                highestQualification = "B.Tech",
                specialization = "Computer Science",
                instituteName = "NIT Example",
                experienceYears = 3,
                currentCompany = "Acme Tech",
                currentDesignation = "Software Engineer",
                expectedCTC = "18 LPA",
                preferredJobLocation = "Bengaluru",
                city = "Bengaluru",
                state = "Karnataka",
                country = "India",
                portfolioUrl = "https://github.com/example",
                resumeUrl = "https://example.com/resume.pdf",
                openToRemote = true,
                willingToRelocate = true
            ),
            isOwnProfile = true
        )
    }
}
