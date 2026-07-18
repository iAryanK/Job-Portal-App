package com.aryan.jobportalapp.presentation.employer

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
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Refresh
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
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aryan.jobportalapp.data.remote.dto.EmployerResponse
import com.aryan.jobportalapp.presentation.common.PullRefreshContainer
import com.aryan.jobportalapp.ui.theme.JobPortalAppTheme

@Composable
fun EmployerProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: EmployerProfileViewModel = viewModel(),
    onEditClick: () -> Unit = {}
) {
    val uiState = viewModel.uiState

    when {
        uiState.isLoading && uiState.employer == null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.errorMessage != null && uiState.employer == null -> {
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
            uiState.employer?.let { employer ->
                PullRefreshContainer(
                    isRefreshing = uiState.isLoading,
                    onRefresh = viewModel::loadProfile,
                    modifier = modifier
                ) {
                    EmployerProfileContent(
                        employer = employer,
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
private fun EmployerProfileContent(
    employer: EmployerResponse,
    modifier: Modifier = Modifier,
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
            ) {
                if (!employer.coverImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = employer.coverImageUrl,
                        contentDescription = "Company cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        colorScheme.primaryContainer,
                                        colorScheme.primary.copy(alpha = 0.75f),
                                        colorScheme.tertiary.copy(alpha = 0.55f)
                                    )
                                )
                            )
                    )
                }

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
                            contentDescription = "Edit company profile",
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
                if (!employer.logoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = employer.logoUrl,
                        contentDescription = "Company logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Business,
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
            val companyName = employer.companyName.orEmpty().ifBlank { "Your company" }
            val isVerified = employer.verified == true
            val verifiedInlineId = "verified"
            val titleStyle = MaterialTheme.typography.headlineSmall
            Text(
                text = buildAnnotatedString {
                    append(companyName)
                    if (isVerified) {
                        append("\u00A0")
                        appendInlineContent(verifiedInlineId, "[verified]")
                    }
                },
                style = titleStyle,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                inlineContent = if (!isVerified) {
                    emptyMap()
                } else {
                    mapOf(
                        verifiedInlineId to InlineTextContent(
                            Placeholder(
                                width = titleStyle.fontSize,
                                height = titleStyle.fontSize,
                                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = colorScheme.primary,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    )
                }
            )

            if (!employer.tagline.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = employer.tagline,
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                employer.industry?.takeIf { it.isNotBlank() }?.let {
                    InfoChip(label = it)
                }
                employer.companySize?.takeIf { it.isNotBlank() }?.let {
                    InfoChip(label = "$it employees")
                }
                employer.establishedYear?.let {
                    InfoChip(label = "Est. $it")
                }
            }

            if (!employer.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(28.dp))
                SectionTitle(title = "About")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = employer.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurface
                )
            }

            val hasContact = listOf(employer.email, employer.phone, employer.website)
                .any { !it.isNullOrBlank() }
            if (hasContact) {
                Spacer(modifier = Modifier.height(28.dp))
                SectionTitle(title = "Contact")
                Spacer(modifier = Modifier.height(12.dp))
                employer.email?.takeIf { it.isNotBlank() }?.let { email ->
                    ContactRow(
                        icon = Icons.Default.Mail,
                        label = email,
                        onClick = {
                            context.startActivity(
                                Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                            )
                        }
                    )
                }
                employer.phone?.takeIf { it.isNotBlank() }?.let { phone ->
                    ContactRow(
                        icon = Icons.Default.Phone,
                        label = phone,
                        onClick = {
                            context.startActivity(
                                Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            )
                        }
                    )
                }
                employer.website?.takeIf { it.isNotBlank() }?.let { website ->
                    ContactRow(
                        icon = Icons.Default.Language,
                        label = website.removePrefix("https://").removePrefix("http://"),
                        onClick = {
                            val url = if (website.startsWith("http")) website else "https://$website"
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        }
                    )
                }
            }

            val hasCompanyDetails = !employer.gstNumber.isNullOrBlank() ||
                !employer.cinNumber.isNullOrBlank() ||
                employer.establishedYear != null ||
                !employer.companySize.isNullOrBlank()
            if (hasCompanyDetails) {
                Spacer(modifier = Modifier.height(28.dp))
                SectionTitle(title = "Company details")
                Spacer(modifier = Modifier.height(12.dp))
                employer.companySize?.takeIf { it.isNotBlank() }?.let {
                    DetailRow(icon = Icons.Outlined.Groups, label = "Company size", value = it)
                }
                employer.establishedYear?.let {
                    DetailRow(
                        icon = Icons.Outlined.CalendarMonth,
                        label = "Established",
                        value = it.toString()
                    )
                }
                employer.gstNumber?.takeIf { it.isNotBlank() }?.let {
                    DetailRow(icon = Icons.Outlined.Badge, label = "GST", value = it)
                }
                employer.cinNumber?.takeIf { it.isNotBlank() }?.let {
                    DetailRow(icon = Icons.Outlined.Badge, label = "CIN", value = it)
                }
            }

            val addressParts = listOf(
                employer.addressLine1,
                employer.addressLine2,
                employer.city,
                employer.district,
                employer.state,
                employer.country,
                employer.pinCode
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

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
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
private fun ContactRow(
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
private fun EmployerProfilePreview() {
    JobPortalAppTheme {
        EmployerProfileContent(
            employer = EmployerResponse(
                id = "preview-user",
                companyName = "Acme Technologies Private limited",
                tagline = "Building the future of work",
                industry = "Information Technology",
                companySize = "51-200",
                establishedYear = 2015,
                description = "A product engineering company focused on hiring great talent.",
                email = "hr@acme.example.com",
                phone = "+919876543210",
                website = "https://acme.example.com",
                gstNumber = "29AAAAA0000A1Z5",
                cinNumber = "U72900KA2020PTC123456",
                addressLine1 = "100 Tech Park",
                city = "Bengaluru",
                state = "Karnataka",
                country = "India",
                pinCode = "560001",
                verified = true,
                verificationStatus = "VERIFIED"
            ),
            isOwnProfile = true
        )
    }
}
