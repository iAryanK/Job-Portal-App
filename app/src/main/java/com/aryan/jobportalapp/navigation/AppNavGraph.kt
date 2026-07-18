package com.aryan.jobportalapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aryan.jobportalapp.data.auth.TokenStore
import com.aryan.jobportalapp.data.remote.NetworkClient
import com.aryan.jobportalapp.data.repository.AuthRepository
import com.aryan.jobportalapp.domain.PostAuthDestination
import com.aryan.jobportalapp.domain.PostAuthRouter
import com.aryan.jobportalapp.presentation.auth.AuthScreen
import com.aryan.jobportalapp.presentation.jobs.PublicJobsScreen
import com.aryan.jobportalapp.presentation.main.MainScaffold
import com.aryan.jobportalapp.presentation.onboard.WelcomeScreen
import com.aryan.jobportalapp.presentation.onboarding.EmployerOnboardingScreen
import com.aryan.jobportalapp.presentation.onboarding.JobSeekerOnboardingScreen
import com.aryan.jobportalapp.presentation.role.RoleSelectionScreen

private fun PostAuthDestination.toRoute(): String = when (this) {
    PostAuthDestination.RoleSelection -> AppRoute.RoleSelection.route
    PostAuthDestination.EmployerOnboarding -> AppRoute.EmployerOnboarding.route
    PostAuthDestination.JobSeekerOnboarding -> AppRoute.JobSeekerOnboarding.route
    PostAuthDestination.Home -> AppRoute.Home.route
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        startDestination = resolveStartDestination()
    }

    val resolvedStart = startDestination
    if (resolvedStart == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    fun navigateAsRoot(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = resolvedStart,
        modifier = modifier
    ) {
        composable(route = AppRoute.Welcome.route) {
            WelcomeScreen(
                onContinue = {
                    navigateAsRoot(AppRoute.Auth.route)
                }
            )
        }

        composable(route = AppRoute.Auth.route) {
            AuthScreen(
                onSkip = {
                    navigateAsRoot(AppRoute.PublicJobs.route)
                },
                onNavigateToRoleSelection = {
                    navigateAsRoot(AppRoute.RoleSelection.route)
                },
                onNavigateToEmployerOnboarding = {
                    navigateAsRoot(AppRoute.EmployerOnboarding.route)
                },
                onNavigateToJobSeekerOnboarding = {
                    navigateAsRoot(AppRoute.JobSeekerOnboarding.route)
                },
                onNavigateToHome = {
                    navigateAsRoot(AppRoute.Home.route)
                }
            )
        }

        composable(route = AppRoute.RoleSelection.route) {
            RoleSelectionScreen(
                onNavigateToHome = {
                    navigateAsRoot(AppRoute.Home.route)
                },
                onNavigateToEmployerOnboarding = {
                    navigateAsRoot(AppRoute.EmployerOnboarding.route)
                },
                onNavigateToJobSeekerOnboarding = {
                    navigateAsRoot(AppRoute.JobSeekerOnboarding.route)
                }
            )
        }

        composable(route = AppRoute.EmployerOnboarding.route) {
            EmployerOnboardingScreen(
                onNavigateToHome = {
                    navigateAsRoot(AppRoute.Home.route)
                }
            )
        }

        composable(route = AppRoute.JobSeekerOnboarding.route) {
            JobSeekerOnboardingScreen(
                onNavigateToHome = {
                    navigateAsRoot(AppRoute.Home.route)
                }
            )
        }

        composable(route = AppRoute.Home.route) {
            MainScaffold(
                onLogout = {
                    navigateAsRoot(AppRoute.Welcome.route)
                }
            )
        }

        composable(route = AppRoute.PublicJobs.route) {
            PublicJobsScreen()
        }
    }
}

private suspend fun resolveStartDestination(): String {
    val hasAccessToken = TokenStore.hasAccessToken()
    val hasRefreshCookie = NetworkClient.hasRefreshCookie()

    if (!hasAccessToken && !hasRefreshCookie) {
        return AppRoute.Welcome.route
    }

    val authRepository = AuthRepository()
    if (!hasAccessToken && hasRefreshCookie) {
        authRepository.refreshSession().onFailure {
            NetworkClient.clearSession()
            return AppRoute.Welcome.route
        }
    }

    return PostAuthRouter().resolveFromCurrentUser().fold(
        onSuccess = { destination -> destination.toRoute() },
        onFailure = {
            NetworkClient.clearSession()
            AppRoute.Welcome.route
        }
    )
}
