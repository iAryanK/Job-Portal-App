package com.aryan.jobportalapp.presentation.jobs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PublicJobsScreen(
    modifier: Modifier = Modifier,
    onJobClick: (String) -> Unit = {}
) {
    JobsScreen(
        modifier = modifier,
        onJobClick = onJobClick
    )
}
