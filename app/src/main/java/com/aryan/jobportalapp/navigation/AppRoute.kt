package com.aryan.jobportalapp.navigation

sealed class AppRoute(val route: String) {
    data object Welcome : AppRoute("welcome")
    data object Auth : AppRoute("auth")
}
