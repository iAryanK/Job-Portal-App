package com.aryan.jobportalapp.data.remote.dto

data class FindJobsFilter(
    val employmentType: EmploymentType? = null,
    val workplaceType: WorkplaceType? = null,
    val experienceLevel: ExperienceLevel? = null,
    val currency: CurrencyType? = null,
    val city: String = "",
    val district: String = "",
    val state: String = "",
    val country: String = "",
    val minSalary: String = "",
    val maxSalary: String = "",
    val minExperience: String = "",
    val maxExperience: String = "",
    val salaryNegotiable: Boolean = false,
    val relocationAvailable: Boolean = false,
    val accommodationProvided: Boolean = false,
    val transportationProvided: Boolean = false,
    val foodProvided: Boolean = false,
    val industry: String = "",
    val employerId: String? = null,
    val remoteOnly: Boolean = false,
    val hybridOnly: Boolean = false,
    val fullTimeOnly: Boolean = false
) {
    fun resolvedWorkplaceType(): WorkplaceType? = when {
        remoteOnly -> WorkplaceType.REMOTE
        hybridOnly -> WorkplaceType.HYBRID
        else -> workplaceType
    }

    fun resolvedEmploymentType(): EmploymentType? = when {
        fullTimeOnly -> EmploymentType.FULL_TIME
        else -> employmentType
    }

    fun toApiFilter(search: String): FindJobsApiFilter {
        return FindJobsApiFilter(
            search = search.takeIf { it.isNotBlank() },
            employmentType = resolvedEmploymentType(),
            workplaceType = resolvedWorkplaceType(),
            experienceLevel = experienceLevel,
            currency = currency,
            city = city.takeIf { it.isNotBlank() },
            district = district.takeIf { it.isNotBlank() },
            state = state.takeIf { it.isNotBlank() },
            country = country.takeIf { it.isNotBlank() },
            minSalary = minSalary.toDoubleOrNull(),
            maxSalary = maxSalary.toDoubleOrNull(),
            minExperience = minExperience.toIntOrNull(),
            maxExperience = maxExperience.toIntOrNull(),
            salaryNegotiable = salaryNegotiable.takeIf { it },
            relocationAvailable = relocationAvailable.takeIf { it },
            accommodationProvided = accommodationProvided.takeIf { it },
            transportationProvided = transportationProvided.takeIf { it },
            foodProvided = foodProvided.takeIf { it },
            industry = industry.takeIf { it.isNotBlank() },
            employerId = employerId
        )
    }

    val advancedFilterCount: Int
        get() = listOfNotNull(
            employmentType?.let { 1 },
            workplaceType?.let { 1 },
            experienceLevel?.let { 1 },
            currency?.let { 1 },
            city.takeIf { it.isNotBlank() },
            district.takeIf { it.isNotBlank() },
            state.takeIf { it.isNotBlank() },
            country.takeIf { it.isNotBlank() },
            minSalary.takeIf { it.isNotBlank() },
            maxSalary.takeIf { it.isNotBlank() },
            minExperience.takeIf { it.isNotBlank() },
            maxExperience.takeIf { it.isNotBlank() },
            industry.takeIf { it.isNotBlank() },
            salaryNegotiable.takeIf { it },
            relocationAvailable.takeIf { it && !remoteOnly && !hybridOnly },
            accommodationProvided.takeIf { it },
            transportationProvided.takeIf { it },
            foodProvided.takeIf { it }
        ).size

    val activeQuickFilterCount: Int
        get() = listOf(remoteOnly, hybridOnly, fullTimeOnly, relocationAvailable).count { it }
}

data class FindJobsApiFilter(
    val search: String? = null,
    val employmentType: EmploymentType? = null,
    val workplaceType: WorkplaceType? = null,
    val experienceLevel: ExperienceLevel? = null,
    val currency: CurrencyType? = null,
    val city: String? = null,
    val district: String? = null,
    val state: String? = null,
    val country: String? = null,
    val minSalary: Double? = null,
    val maxSalary: Double? = null,
    val minExperience: Int? = null,
    val maxExperience: Int? = null,
    val salaryNegotiable: Boolean? = null,
    val relocationAvailable: Boolean? = null,
    val accommodationProvided: Boolean? = null,
    val transportationProvided: Boolean? = null,
    val foodProvided: Boolean? = null,
    val industry: String? = null,
    val employerId: String? = null
)

enum class JobSortOption(val label: String, val sortParam: String) {
    NEWEST("Newest", "createdAt,desc"),
    OLDEST("Oldest", "createdAt,asc"),
    SALARY_HIGH("Salary: High to low", "maximumSalary,desc"),
    SALARY_LOW("Salary: Low to high", "minimumSalary,asc"),
    EXPERIENCE_LOW("Experience: Low to high", "minimumExperience,asc")
}
