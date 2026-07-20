package com.aryan.jobportalapp.data.remote.dto

enum class ApplicationStatus {
    APPLIED,
    SHORTLISTED,
    SELECTED,
    REJECTED
}

enum class InvitationStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}

data class RecruitmentJobSeekerRef(
    val id: String? = null,
    val name: String? = null,
    val headline: String? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val experienceYears: Int? = null,
    val experienceMonths: Int? = null,
    val highestQualification: String? = null,
    val specialization: String? = null
)

data class ApplicationResponse(
    val id: String? = null,
    val message: String? = null,
    val status: ApplicationStatus? = null,
    val job: JobSummaryResponse? = null,
    val jobSeeker: RecruitmentJobSeekerRef? = null
)

data class InvitationResponse(
    val id: String? = null,
    val status: InvitationStatus? = null,
    val respondedAt: String? = null,
    val expiresAt: String? = null,
    val job: JobSummaryResponse? = null,
    val jobSeeker: RecruitmentJobSeekerRef? = null
)

data class RecruitApplicationItem(
    val applicationId: String,
    val jobId: String,
    val jobTitle: String,
    val jobSeekerId: String,
    val jobSeekerName: String,
    val jobSeekerHeadline: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val experienceYears: Int?,
    val experienceMonths: Int?,
    val highestQualification: String?,
    val specialization: String?,
    val status: ApplicationStatus,
    val message: String?
)

data class RecruitInvitationItem(
    val invitationId: String,
    val jobId: String,
    val jobTitle: String,
    val jobSeekerId: String,
    val jobSeekerName: String,
    val jobSeekerHeadline: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val experienceYears: Int?,
    val experienceMonths: Int?,
    val highestQualification: String?,
    val specialization: String?,
    val status: InvitationStatus,
    val expiresAt: String?
)

data class JobSeekerApplicationItem(
    val applicationId: String,
    val status: ApplicationStatus,
    val message: String?,
    val job: JobSummaryResponse
)

data class JobSeekerInvitationItem(
    val invitationId: String,
    val status: InvitationStatus,
    val expiresAt: String?,
    val respondedAt: String?,
    val job: JobSummaryResponse
)
