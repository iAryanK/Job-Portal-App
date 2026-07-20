package com.aryan.jobportalapp.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.aryan.jobportalapp.R
import com.aryan.jobportalapp.data.remote.NetworkClient
import com.aryan.jobportalapp.data.remote.dto.AppUserRole
import com.aryan.jobportalapp.presentation.employer.EmployerHomeScreen
import com.aryan.jobportalapp.presentation.employer.EmployerMyJobsScreen
import com.aryan.jobportalapp.presentation.employer.EmployerProfileScreen
import com.aryan.jobportalapp.presentation.employer.RecruitScreen
import com.aryan.jobportalapp.presentation.employer.TalentPoolScreen
import com.aryan.jobportalapp.presentation.home.HomeScreen
import com.aryan.jobportalapp.presentation.jobseeker.ActivitiesScreen
import com.aryan.jobportalapp.presentation.jobseeker.JobSeekerProfileScreen
import com.aryan.jobportalapp.presentation.jobs.PublicJobsScreen
import kotlinx.coroutines.launch

enum class MainTab(val label: String) {
    Home("Home"),
    Jobs("Jobs"),
    Activities("Activities"),
    TalentPool("Talent Pool"),
    Recruit("Recruit"),
    MyJobs("My Jobs"),
    Profile("Profile")
}

private fun tabsForRole(role: AppUserRole?): List<MainTab> = when (role) {
    AppUserRole.EMPLOYER -> listOf(
        MainTab.Home,
        MainTab.TalentPool,
        MainTab.Recruit,
        MainTab.MyJobs
    )
    else -> listOf(
        MainTab.Home,
        MainTab.Jobs,
        MainTab.Activities,
        MainTab.Profile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(),
    openProfile: Boolean = false,
    onProfileOpened: () -> Unit = {},
    onLogout: () -> Unit = {},
    onEditEmployer: () -> Unit = {},
    onEditJobSeeker: () -> Unit = {},
    onCreateJob: () -> Unit = {},
    onJobClick: (String) -> Unit = {},
    onJobSeekerClick: (String) -> Unit = {}
) {
    val uiState = viewModel.uiState
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val tabs = remember(uiState.user?.role) { tabsForRole(uiState.user?.role) }
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Home) }

    LaunchedEffect(openProfile) {
        if (openProfile) {
            selectedTab = MainTab.Profile
            onProfileOpened()
        }
    }

    LaunchedEffect(tabs) {
        // Profile can be opened from the top-bar avatar even when it is not a bottom tab.
        if (selectedTab != MainTab.Profile && selectedTab !in tabs) {
            selectedTab = MainTab.Home
        }
    }

    if (uiState.isLoadingUser && uiState.user == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(24.dp)
                    )
                    tabs.forEach { tab ->
                        NavigationDrawerItem(
                            label = { Text(text = tab.label) },
                            selected = selectedTab == tab,
                            onClick = {
                                selectedTab = tab
                                scope.launch { drawerState.close() }
                            },
                            icon = {
                                Icon(
                                    imageVector = tabIcon(tab, selected = selectedTab == tab),
                                    contentDescription = tab.label
                                )
                            },
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                    NavigationDrawerItem(
                        label = { Text(text = "Logout") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            NetworkClient.clearSession()
                            onLogout()
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout"
                            )
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.app_name),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* notifications */ }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications"
                            )
                        }
                        IconButton(onClick = { selectedTab = MainTab.Profile }) {
                            val photoUrl = uiState.user?.profilePhoto
                            if (!photoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = photoUrl,
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tabIcon(tab, selected = selectedTab == tab),
                                    contentDescription = tab.label
                                )
                            },
                            label = { Text(text = tab.label) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    MainTab.Home -> {
                        if (uiState.user?.role == AppUserRole.EMPLOYER) {
                            EmployerHomeScreen(onPostJobClick = onCreateJob)
                        } else {
                            HomeScreen()
                        }
                    }
                    MainTab.Jobs -> PublicJobsScreen(onJobClick = onJobClick)
                    MainTab.Activities -> ActivitiesScreen(onJobClick = onJobClick)
                    MainTab.TalentPool -> TalentPoolScreen(
                        onJobSeekerClick = onJobSeekerClick
                    )
                    MainTab.Recruit -> RecruitScreen(
                        onJobSeekerClick = onJobSeekerClick,
                        onJobClick = onJobClick
                    )
                    MainTab.MyJobs -> EmployerMyJobsScreen(
                        onPostJobClick = onCreateJob,
                        onJobClick = onJobClick
                    )
                    MainTab.Profile -> {
                        if (uiState.user?.role == AppUserRole.EMPLOYER) {
                            EmployerProfileScreen(onEditClick = onEditEmployer)
                        } else {
                            JobSeekerProfileScreen(onEditClick = onEditJobSeeker)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderTab(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

private fun tabIcon(tab: MainTab, selected: Boolean): ImageVector = when (tab) {
    MainTab.Home -> if (selected) Icons.Filled.Home else Icons.Outlined.Home
    MainTab.Jobs -> if (selected) Icons.Filled.Work else Icons.Outlined.Work
    MainTab.Activities -> Icons.AutoMirrored.Outlined.Assignment
    MainTab.TalentPool -> if (selected) Icons.Filled.People else Icons.Outlined.People
    MainTab.Recruit -> if (selected) Icons.Filled.PersonSearch else Icons.Outlined.PersonSearch
    MainTab.MyJobs -> if (selected) Icons.Filled.WorkOutline else Icons.Outlined.WorkOutline
    MainTab.Profile -> if (selected) Icons.Filled.Person else Icons.Outlined.Person
}
