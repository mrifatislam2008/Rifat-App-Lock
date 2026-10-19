package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.common.AppLanguage
import com.example.ui.common.AppStrings
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.developer.DeveloperInfoScreen
import com.example.ui.help.TroubleshootingScreen
import com.example.ui.manage.ManageAppsScreen
import com.example.ui.onboarding.CreateCredentialScreen
import com.example.ui.onboarding.PermissionSetupScreen
import com.example.ui.onboarding.WelcomeScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.MyApplicationTheme

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object SetupCredential : Screen("setup_credential")
    object ChangeCredential : Screen("change_credential")
    object SetupPermissions : Screen("setup_permissions")
    object Dashboard : Screen("dashboard")
    object ManageApps : Screen("manage_apps")
    object Settings : Screen("settings")
    object Developer : Screen("developer")
    object Troubleshooting : Screen("troubleshooting")
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as RifatAppLockerApplication
        val preferences = app.appPreferences
        val repository = app.appRepository
        val security = app.securityUtils

        setContent {
            MyApplicationTheme {
                val languageCode by preferences.languageFlow.collectAsState()
                val currentLanguage = remember(languageCode) {
                    if (languageCode == "bn") AppLanguage.BENGALI else AppLanguage.ENGLISH
                }

                fun toggleLanguage() {
                    preferences.language = if (currentLanguage == AppLanguage.ENGLISH) "bn" else "en"
                }

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val startDestination = remember {
                    if (!preferences.isSetupCompleted) {
                        Screen.Welcome.route
                    } else {
                        Screen.Dashboard.route
                    }
                }

                val bottomNavItems = listOf(
                    Screen.Dashboard to (Icons.Default.Dashboard to "dashboard"),
                    Screen.ManageApps to (Icons.Default.Lock to "manage_apps"),
                    Screen.Settings to (Icons.Default.Settings to "security_settings"),
                    Screen.Developer to (Icons.Default.Code to "developer_info"),
                    Screen.Troubleshooting to (Icons.Default.HelpOutline to "troubleshooting")
                )

                val showBottomNav = currentRoute in listOf(
                    Screen.Dashboard.route,
                    Screen.ManageApps.route,
                    Screen.Settings.route,
                    Screen.Developer.route,
                    Screen.Troubleshooting.route
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomNav) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp
                            ) {
                                bottomNavItems.forEach { (screen, info) ->
                                    val (icon, key) = info
                                    val selected = currentRoute == screen.route
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            if (currentRoute != screen.route) {
                                                navController.navigate(screen.route) {
                                                    popUpTo(Screen.Dashboard.route) { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = AppStrings.get(key, currentLanguage)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = AppStrings.get(key, currentLanguage),
                                                fontSize = 10.sp,
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                                maxLines = 1
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            selectedTextColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = startDestination
                        ) {
                            composable(Screen.Welcome.route) {
                                WelcomeScreen(
                                    currentLanguage = currentLanguage,
                                    onToggleLanguage = { toggleLanguage() },
                                    onGetStarted = {
                                        navController.navigate(Screen.SetupCredential.route)
                                    }
                                )
                            }

                            composable(Screen.SetupCredential.route) {
                                CreateCredentialScreen(
                                    preferences = preferences,
                                    securityUtils = security,
                                    currentLanguage = currentLanguage,
                                    isChangeMode = false,
                                    onToggleLanguage = { toggleLanguage() },
                                    onCredentialSaved = {
                                        navController.navigate(Screen.SetupPermissions.route) {
                                            popUpTo(Screen.Welcome.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.ChangeCredential.route) {
                                CreateCredentialScreen(
                                    preferences = preferences,
                                    securityUtils = security,
                                    currentLanguage = currentLanguage,
                                    isChangeMode = true,
                                    onToggleLanguage = { toggleLanguage() },
                                    onNavigateBack = { navController.popBackStack() },
                                    onCredentialSaved = {
                                        navController.popBackStack()
                                    }
                                )
                            }

                            composable(Screen.SetupPermissions.route) {
                                PermissionSetupScreen(
                                    currentLanguage = currentLanguage,
                                    onToggleLanguage = { toggleLanguage() },
                                    onPermissionsCompleted = {
                                        navController.navigate(Screen.Dashboard.route) {
                                            popUpTo(Screen.SetupPermissions.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.Dashboard.route) {
                                DashboardScreen(
                                    appRepository = repository,
                                    appPreferences = preferences,
                                    currentLanguage = currentLanguage,
                                    onToggleLanguage = { toggleLanguage() },
                                    onNavigateToManageApps = { navController.navigate(Screen.ManageApps.route) },
                                    onNavigateToPermissions = { navController.navigate(Screen.SetupPermissions.route) },
                                    onNavigateToDeveloper = { navController.navigate(Screen.Developer.route) },
                                    onNavigateToTroubleshooting = { navController.navigate(Screen.Troubleshooting.route) }
                                )
                            }

                            composable(Screen.ManageApps.route) {
                                ManageAppsScreen(
                                    appRepository = repository,
                                    appPreferences = preferences,
                                    currentLanguage = currentLanguage,
                                    onToggleLanguage = { toggleLanguage() },
                                    onNavigateBack = { navController.navigate(Screen.Dashboard.route) }
                                )
                            }

                            composable(Screen.Settings.route) {
                                SettingsScreen(
                                    appPreferences = preferences,
                                    currentLanguage = currentLanguage,
                                    onToggleLanguage = { toggleLanguage() },
                                    onChangeCredentialsClick = { navController.navigate(Screen.ChangeCredential.route) },
                                    onNavigateBack = { navController.navigate(Screen.Dashboard.route) }
                                )
                            }

                            composable(Screen.Developer.route) {
                                DeveloperInfoScreen(
                                    currentLanguage = currentLanguage,
                                    onToggleLanguage = { toggleLanguage() },
                                    onNavigateBack = { navController.navigate(Screen.Dashboard.route) }
                                )
                            }

                            composable(Screen.Troubleshooting.route) {
                                TroubleshootingScreen(
                                    currentLanguage = currentLanguage,
                                    onToggleLanguage = { toggleLanguage() },
                                    onNavigateBack = { navController.navigate(Screen.Dashboard.route) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
