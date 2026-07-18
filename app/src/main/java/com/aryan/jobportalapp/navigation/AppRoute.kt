package com.aryan.jobportalapp.navigation

sealed class AppRoute(val route: String) {
    data object Welcome : AppRoute("welcome")
    data object Auth : AppRoute("auth")
    data object RoleSelection : AppRoute("role_selection")
    data object EmployerOnboarding : AppRoute("employer_onboarding")
    data object JobSeekerOnboarding : AppRoute("jobseeker_onboarding")
    data object Home : AppRoute("home")
    data object EditEmployer : AppRoute("edit_employer")
    data object EditJobSeeker : AppRoute("edit_jobseeker")
    data object CreateJob : AppRoute("create_job")
    data object EditJob : AppRoute("edit_job/{jobId}") {
        fun createRoute(jobId: String) = "edit_job/$jobId"
    }
    data object JobDetail : AppRoute("job_detail/{jobId}") {
        fun createRoute(jobId: String) = "job_detail/$jobId"
    }
    data object PublicJobs : AppRoute("public_jobs")
}
