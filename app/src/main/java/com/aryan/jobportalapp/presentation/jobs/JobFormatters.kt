package com.aryan.jobportalapp.presentation.jobs

import com.aryan.jobportalapp.data.remote.dto.CurrencyType
import com.aryan.jobportalapp.data.remote.dto.EmploymentType
import com.aryan.jobportalapp.data.remote.dto.ExperienceLevel
import com.aryan.jobportalapp.data.remote.dto.JobStatus
import com.aryan.jobportalapp.data.remote.dto.WorkplaceType
import com.aryan.jobportalapp.presentation.common.enumLabel
import java.text.NumberFormat
import java.util.Locale

fun formatEmploymentType(value: EmploymentType?): String? =
    value?.let(::enumLabel)

fun formatWorkplaceType(value: WorkplaceType?): String? =
    value?.let(::enumLabel)

fun formatExperienceLevel(value: ExperienceLevel?): String? =
    value?.let(::enumLabel)

fun formatJobStatus(value: JobStatus?): String? =
    value?.let(::enumLabel)

fun formatJobLocation(
    city: String?,
    state: String? = null,
    country: String? = null
): String? {
    val parts = listOfNotNull(
        city?.takeIf { it.isNotBlank() },
        state?.takeIf { it.isNotBlank() },
        country?.takeIf { it.isNotBlank() }
    )
    return parts.takeIf { it.isNotEmpty() }?.joinToString(", ")
}

fun formatExperienceRange(
    minimum: Int?,
    maximum: Int?
): String? {
    return when {
        minimum != null && maximum != null -> "$minimum–$maximum yrs"
        minimum != null -> "$minimum+ yrs"
        maximum != null -> "Up to $maximum yrs"
        else -> null
    }
}

fun formatSalaryRange(
    minimum: Double?,
    maximum: Double?,
    currency: CurrencyType?,
    negotiable: Boolean? = null
): String? {
    val formatter = NumberFormat.getNumberInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 0
    }
    val symbol = when (currency) {
        CurrencyType.USD -> "$"
        CurrencyType.INR -> "₹"
        null -> ""
    }
    val range = when {
        minimum != null && maximum != null ->
            "$symbol${formatter.format(minimum)} – $symbol${formatter.format(maximum)}"
        minimum != null ->
            "From $symbol${formatter.format(minimum)}"
        maximum != null ->
            "Up to $symbol${formatter.format(maximum)}"
        else -> null
    }
    return when {
        range != null && negotiable == true -> "$range · Negotiable"
        range != null -> range
        negotiable == true -> "Negotiable"
        else -> null
    }
}

fun formatDateLabel(isoInstant: String?): String? {
    if (isoInstant.isNullOrBlank()) return null
    return isoInstant.substringBefore('T').ifBlank { null }
}
