package com.aryan.jobportalapp.data.remote.dto

enum class EmploymentType {
    FULL_TIME,
    PART_TIME,
    CONTRACT,
    TEMPORARY,
    INTERN,
    FREELANCE,
    APPRENTICESHIP
}

enum class WorkplaceType {
    ON_SITE,
    HYBRID,
    REMOTE
}

enum class ExperienceLevel {
    FRESHER,
    ENTRY_LEVEL,
    MID_LEVEL,
    SENIOR,
    LEAD,
    MANAGER
}

enum class JobStatus {
    DRAFT,
    PUBLISHED,
    PAUSED,
    CLOSED,
    EXPIRED
}

enum class CurrencyType {
    USD,
    INR
}

data class JobEmployerSummary(
    val id: String? = null,
    val companyName: String? = null,
    val industry: String? = null,
    val logoUrl: String? = null,
    val city: String? = null,
    val verified: Boolean? = null
)

data class JobSummaryResponse(
    val id: String? = null,
    val title: String? = null,
    val requirements: String? = null,
    val employer: JobEmployerSummary? = null,
    val employmentType: EmploymentType? = null,
    val workplaceType: WorkplaceType? = null,
    val experienceLevel: ExperienceLevel? = null,
    val status: JobStatus? = null,
    val vacancies: Int? = null,
    val minimumExperience: Int? = null,
    val maximumExperience: Int? = null,
    val minimumSalary: Double? = null,
    val maximumSalary: Double? = null,
    val salaryNegotiable: Boolean? = null,
    val currency: CurrencyType? = null,
    val city: String? = null,
    val district: String? = null,
    val state: String? = null,
    val country: String? = null,
    val applicationDeadline: String? = null
)

data class JobResponse(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val responsibilities: String? = null,
    val requirements: String? = null,
    val benefits: String? = null,
    val employer: JobEmployerSummary? = null,
    val employmentType: EmploymentType? = null,
    val workplaceType: WorkplaceType? = null,
    val experienceLevel: ExperienceLevel? = null,
    val status: JobStatus? = null,
    val vacancies: Int? = null,
    val minimumExperience: Int? = null,
    val maximumExperience: Int? = null,
    val minimumSalary: Double? = null,
    val maximumSalary: Double? = null,
    val salaryNegotiable: Boolean? = null,
    val currency: CurrencyType? = null,
    val city: String? = null,
    val district: String? = null,
    val state: String? = null,
    val country: String? = null,
    val pinCode: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val relocationAvailable: Boolean? = null,
    val accommodationProvided: Boolean? = null,
    val transportationProvided: Boolean? = null,
    val foodProvided: Boolean? = null,
    val applicationDeadline: String? = null,
    val expiresAt: String? = null,
    val applicationCount: Int? = null
)

data class CreateJobRequest(
    val title: String,
    val description: String? = null,
    val responsibilities: String? = null,
    val requirements: String? = null,
    val benefits: String? = null,
    val employmentType: EmploymentType? = null,
    val workplaceType: WorkplaceType? = null,
    val experienceLevel: ExperienceLevel? = null,
    val status: JobStatus? = null,
    val vacancies: Int? = null,
    val minimumExperience: Int? = null,
    val maximumExperience: Int? = null,
    val minimumSalary: Double? = null,
    val maximumSalary: Double? = null,
    val salaryNegotiable: Boolean? = null,
    val currency: CurrencyType? = null,
    val city: String? = null,
    val district: String? = null,
    val state: String? = null,
    val country: String? = null,
    val pinCode: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val relocationAvailable: Boolean? = null,
    val accommodationProvided: Boolean? = null,
    val transportationProvided: Boolean? = null,
    val foodProvided: Boolean? = null,
    val applicationDeadline: String? = null,
    val expiresAt: String? = null
)

data class PatchJobRequest(
    val title: String? = null,
    val description: String? = null,
    val responsibilities: String? = null,
    val requirements: String? = null,
    val benefits: String? = null,
    val employmentType: EmploymentType? = null,
    val workplaceType: WorkplaceType? = null,
    val experienceLevel: ExperienceLevel? = null,
    val status: JobStatus? = null,
    val vacancies: Int? = null,
    val minimumExperience: Int? = null,
    val maximumExperience: Int? = null,
    val minimumSalary: Double? = null,
    val maximumSalary: Double? = null,
    val salaryNegotiable: Boolean? = null,
    val currency: CurrencyType? = null,
    val city: String? = null,
    val district: String? = null,
    val state: String? = null,
    val country: String? = null,
    val pinCode: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val relocationAvailable: Boolean? = null,
    val accommodationProvided: Boolean? = null,
    val transportationProvided: Boolean? = null,
    val foodProvided: Boolean? = null,
    val applicationDeadline: String? = null,
    val expiresAt: String? = null
)
