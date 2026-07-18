package com.aryan.jobportalapp.navigation

sealed class AppRoute(val route: String) {
    data object Welcome : AppRoute("welcome")
    data object Auth : AppRoute("auth")
    data object RoleSelection : AppRoute("role_selection")
    data object EmployerOnboarding : AppRoute("employer_onboarding")
    data object JobSeekerOnboarding : AppRoute("jobseeker_onboarding")
    data object Home : AppRoute("home")
    data object PublicJobs : AppRoute("public_jobs")
}
