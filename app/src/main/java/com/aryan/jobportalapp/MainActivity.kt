package com.aryan.jobportalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.aryan.jobportalapp.presentation.onboard.WelcomeScreen
import com.aryan.jobportalapp.ui.theme.JobPortalAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JobPortalAppTheme {
                WelcomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    onContinue = { /* Navigate to next onboarding step */ }
                )
            }
        }
    }
}