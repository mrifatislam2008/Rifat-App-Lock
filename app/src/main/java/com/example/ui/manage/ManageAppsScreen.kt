package com.example.ui.manage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.RemoveDone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppInfo
import com.example.data.model.FilterType
import com.example.data.preferences.AppPreferences
import com.example.data.repository.AppRepository
import com.example.service.ServiceHelper
import com.example.ui.common.AppDrawableIcon
import com.example.ui.common.AppLanguage
import com.example.ui.common.AppStrings
import com.example.ui.common.CyberCard
import com.example.ui.common.RifatTopBar
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.ShieldOrange
import com.example.ui.theme.ShieldRed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAppsScreen(
    appRepository: AppRepository,
    appPreferences: AppPreferences,
    initialFilter: FilterType = FilterType.ALL,
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val installedApps by appRepository.installedApps.collectAsState()
    val isLoading by appRepository.isLoading.collectAsState()
    val lockedPackages by appPreferences.lockedPackagesFlow.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val selectedFilter by appRepository.selectedFilterFlow.collectAsState()

    val hasAccessibility = ServiceHelper.isAccessibilityServiceEnabled(context)
    val hasUsageStats = ServiceHelper.hasUsageStatsPermission(context)
    val isEnforcementOperational = hasAccessibility || hasUsageStats

    // Filter logic
    val filteredApps = remember(installedApps, lockedPackages, searchQuery, selectedFilter) {
        installedApps.filter { app ->
            val matchesSearch = searchQuery.isBlank() ||
                    app.appName.contains(searchQuery, ignoreCase = true) ||
                    app.packageName.contains(searchQuery, ignoreCase = true)

            val isLocked = lockedPackages.contains(app.packageName)
            val matchesFilter = when (selectedFilter) {
                FilterType.ALL -> true
                FilterType.LOCKED -> isLocked
                FilterType.UNLOCKED -> !isLocked
            }

            matchesSearch && matchesFilter
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RifatTopBar(
                title = AppStrings.get("manage_apps", currentLanguage),
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                currentLanguage = currentLanguage,
                onToggleLanguage = onToggleLanguage
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Enforcement Warning Banner if permissions are missing
            if (!isEnforcementOperational && lockedPackages.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ShieldOrange.copy(alpha = 0.15f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = ShieldOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.BENGALI)
                                "লক চিহ্নিত করা হয়েছে কিন্তু এক্সেসিবিলিটি সার্ভিস সক্রিয় না থাকায় তা বলবৎ হবে না।"
                            else
                                "Apps are selected, but Accessibility Service is needed to enforce actual locking.",
                            fontSize = 11.sp,
                            color = ShieldOrange,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Search & Controls Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(AppStrings.get("search_hint", currentLanguage), fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filters & Select All Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = selectedFilter == FilterType.ALL,
                            onClick = { appRepository.setAppFilter(FilterType.ALL) },
                            label = { Text("${AppStrings.get("filter_all", currentLanguage)} (${installedApps.size})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        FilterChip(
                            selected = selectedFilter == FilterType.LOCKED,
                            onClick = { appRepository.setAppFilter(FilterType.LOCKED) },
                            label = { Text("${AppStrings.get("filter_locked", currentLanguage)} (${lockedPackages.size})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ShieldGreen.copy(alpha = 0.2f),
                                selectedLabelColor = ShieldGreen
                            )
                        )
                        FilterChip(
                            selected = selectedFilter == FilterType.UNLOCKED,
                            onClick = { appRepository.setAppFilter(FilterType.UNLOCKED) },
                            label = {
                                Text(
                                    "${AppStrings.get("filter_unlocked", currentLanguage)} (${(installedApps.size - lockedPackages.size).coerceAtLeast(0)})",
                                    fontSize = 11.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                selectedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Row {
                        IconButton(
                            onClick = {
                                appRepository.lockAllApps()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = if (currentLanguage == AppLanguage.BENGALI) "সকল অ্যাপ্লিকেশন লক করা হয়েছে" else "All compatible applications locked",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Lock All",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = {
                                appRepository.unlockAllApps()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = if (currentLanguage == AppLanguage.BENGALI) "সকল অ্যাপ্লিকেশন আনলক করা হয়েছে" else "All applications unlocked",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.RemoveDone,
                                contentDescription = "Unlock All",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.BENGALI) "ইনস্টল করা অ্যাপ লোড হচ্ছে..." else "Loading installed apps...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (filteredApps.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.BENGALI) "কোনো অ্যাপ পাওয়া যায়নি" else "No applications match your filter",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredApps, key = { it.packageName }) { appInfo ->
                        val isLocked = lockedPackages.contains(appInfo.packageName)
                        AppRowItem(
                            appInfo = appInfo,
                            isLocked = isLocked,
                            icon = appRepository.getAppIcon(appInfo.packageName),
                            currentLanguage = currentLanguage,
                            onToggleLock = {
                                val newLockedState = appRepository.toggleAppLock(appInfo.packageName)
                                val msg = if (newLockedState) {
                                    "${appInfo.appName} ${AppStrings.get("app_locked_msg", currentLanguage)}"
                                } else {
                                    "${appInfo.appName} ${AppStrings.get("app_unlocked_msg", currentLanguage)}"
                                }
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = msg,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppRowItem(
    appInfo: AppInfo,
    isLocked: Boolean,
    icon: android.graphics.drawable.Drawable?,
    currentLanguage: AppLanguage,
    onToggleLock: () -> Unit
) {
    CyberCard(borderGlow = isLocked) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleLock() }
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppDrawableIcon(drawable = icon)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = appInfo.appName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (appInfo.isSystemApp) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "System",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = appInfo.packageName,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Current Protection Status Label
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isLocked) ShieldGreen else MaterialTheme.colorScheme.outline)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isLocked) {
                            if (currentLanguage == AppLanguage.BENGALI) "সুরক্ষিত" else "Protected"
                        } else {
                            if (currentLanguage == AppLanguage.BENGALI) "সুরক্ষিত নয়" else "Unprotected"
                        },
                        fontSize = 11.sp,
                        color = if (isLocked) ShieldGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = isLocked,
                onCheckedChange = { onToggleLock() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF070F1E),
                    checkedTrackColor = CyberCyan,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}
