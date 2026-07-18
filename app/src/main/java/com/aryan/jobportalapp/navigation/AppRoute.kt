package com.aryan.jobportalapp.navigation

sealed class AppRoute(val route: String) {
    data object Welcome : AppRoute("welcome")
    data object Auth : AppRoute("auth")
    data object RoleSelection : AppRoute("role_selection")
    data object Home : AppRoute("home")
    data object PublicJobs : AppRoute("public_jobs")
}
