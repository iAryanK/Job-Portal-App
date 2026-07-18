package com.aryan.jobportalapp.presentation.auth

import android.content.res.Configuration
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aryan.jobportalapp.domain.PostAuthDestination
import com.aryan.jobportalapp.ui.theme.JobPortalAppTheme
import kotlinx.coroutines.delay

private const val OtpResendSeconds = 59

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(),
    onSkip: () -> Unit = {},
    onContinueWithGoogle: () -> Unit = {},
    onNavigateToRoleSelection: () -> Unit = {},
    onNavigateToEmployerOnboarding: () -> Unit = {},
    onNavigateToJobSeekerOnboarding: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    val isEmailValid by remember(email) {
        derivedStateOf { Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() }
    }

    LaunchedEffect(uiState.toastMessage) {
        val message = uiState.toastMessage ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.consumeToast()
    }

    LaunchedEffect(uiState.destination) {
        when (uiState.destination) {
            PostAuthDestination.RoleSelection -> onNavigateToRoleSelection()
            PostAuthDestination.EmployerOnboarding -> onNavigateToEmployerOnboarding()
            PostAuthDestination.JobSeekerOnboarding -> onNavigateToJobSeekerOnboarding()
            PostAuthDestination.Home -> onNavigateToHome()
            null -> Unit
        }
        if (uiState.destination != null) {
            viewModel.consumeDestination()
        }
    }

    fun submitEmail() {
        if (isEmailValid && !uiState.isSubmitting) {
            viewModel.sendOtp(email.trim())
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            TextButton(
                onClick = onSkip,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(text = "Skip")
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "JobPortal",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Find the work that fits you",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Sign in to save jobs, track applications, and get personalized opportunities.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Email ID") },
                    singleLine = true,
                    enabled = !uiState.isSubmitting,
                    isError = email.isNotBlank() && !isEmailValid,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { submitEmail() }
                    ),
                    supportingText = {
                        if (email.isNotBlank() && !isEmailValid) {
                            Text(text = "Enter a valid email ID")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { submitEmail() },
                    enabled = isEmailValid && !uiState.isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Submit",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onContinueWithGoogle,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Continue with Google",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    if (uiState.showOtpDialog) {
        OtpVerificationDialog(
            email = uiState.pendingEmail,
            isVerifying = uiState.isVerifying,
            onDismiss = { viewModel.dismissOtpDialog() },
            onConfirm = { otp ->
                viewModel.verifyOtp(uiState.pendingEmail, otp)
            },
            onResendOtp = { viewModel.sendOtp(uiState.pendingEmail) }
        )
    }
}

@Composable
private fun OtpVerificationDialog(
    email: String,
    isVerifying: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    onResendOtp: () -> Unit
) {
    var otp by rememberSaveable { mutableStateOf("") }
    var secondsRemaining by rememberSaveable { mutableIntStateOf(OtpResendSeconds) }
    var timerKey by rememberSaveable { mutableIntStateOf(0) }
    val canResend = secondsRemaining == 0 && !isVerifying
    val isOtpValid = otp.length in 4..8 && otp.all(Char::isDigit)

    LaunchedEffect(timerKey) {
        secondsRemaining = OtpResendSeconds
        while (secondsRemaining > 0) {
            delay(1_000)
            secondsRemaining--
        }
    }

    AlertDialog(
        onDismissRequest = {
            if (!isVerifying) {
                onDismiss()
            }
        },
        title = { Text(text = "Verify OTP") },
        text = {
            Column {
                Text(
                    text = "Enter the OTP sent to $email",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = otp,
                    onValueChange = { value ->
                        if (value.length <= 8 && value.all(Char::isDigit)) {
                            otp = value
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "OTP") },
                    singleLine = true,
                    enabled = !isVerifying,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (isOtpValid && !isVerifying) {
                                onConfirm(otp)
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (canResend) {
                        "Didn't receive the code?"
                    } else {
                        "Resend OTP in 00:${secondsRemaining.toString().padStart(2, '0')}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(otp) },
                enabled = isOtpValid && !isVerifying
            ) {
                if (isVerifying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "OK")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onResendOtp()
                    otp = ""
                    timerKey++
                },
                enabled = canResend
            ) {
                Text(text = "Resend OTP")
            }
        }
    )
}

@Preview(
    name = "Auth",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AuthScreenPreview() {
    JobPortalAppTheme {
        AuthScreen()
    }
}
