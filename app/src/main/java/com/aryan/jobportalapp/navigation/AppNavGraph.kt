package com.aryan.jobportalapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aryan.jobportalapp.presentation.auth.AuthScreen
import com.aryan.jobportalapp.presentation.jobs.PublicJobsScreen
import com.aryan.jobportalapp.presentation.onboard.WelcomeScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = AppRoute.Welcome.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = AppRoute.Welcome.route) {
            WelcomeScreen(
                onContinue = {
                    navController.navigate(AppRoute.Auth.route) {
                        popUpTo(AppRoute.Welcome.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = AppRoute.Auth.route) {
            AuthScreen(
                onSkip = {
                    navController.navigate(AppRoute.PublicJobs.route) {
                        launchSingleTop = true
                    }
                },
                onAuthSuccess = {
                    navController.navigate(AppRoute.PublicJobs.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = AppRoute.PublicJobs.route) {
            PublicJobsScreen()
        }
    }
}
