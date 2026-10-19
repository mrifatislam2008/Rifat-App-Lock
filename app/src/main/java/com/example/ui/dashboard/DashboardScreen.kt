package com.example.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.AppPreferences
import com.example.data.repository.AppRepository
import com.example.service.ServiceHelper
import com.example.ui.common.AppLanguage
import com.example.ui.common.AppStrings
import com.example.ui.common.CyberCard
import com.example.ui.common.RifatTopBar
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanDim
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.ShieldOrange
import com.example.ui.theme.ShieldRed

@Composable
fun DashboardScreen(
    appRepository: AppRepository,
    appPreferences: AppPreferences,
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    onNavigateToManageApps: () -> Unit,
    onNavigateToPermissions: () -> Unit,
    onNavigateToDeveloper: () -> Unit,
    onNavigateToTroubleshooting: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val installedApps by appRepository.installedApps.collectAsState()
    val isLoading by appRepository.isLoading.collectAsState()
    val isAppLockerEnabled by appPreferences.isAppLockerEnabledFlow.collectAsState()
    val lockedPackages by appPreferences.lockedPackagesFlow.collectAsState()

    val totalApps = installedApps.size
    val lockedCount = installedApps.count { lockedPackages.contains(it.packageName) }
    val unlockedCount = (totalApps - lockedCount).coerceAtLeast(0)

    val hasAccessibility = ServiceHelper.isAccessibilityServiceEnabled(context)
    val hasUsageStats = ServiceHelper.hasUsageStatsPermission(context)
    val hasOverlay = ServiceHelper.hasOverlayPermission(context)
    val isSystemReady = hasAccessibility && hasUsageStats && hasOverlay

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        RifatTopBar(
            title = AppStrings.get("dashboard", currentLanguage),
            currentLanguage = currentLanguage,
            onToggleLanguage = onToggleLanguage
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // MASTER PROTECTION STATUS HERO CARD
            CyberCard(borderGlow = isAppLockerEnabled) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                if (isAppLockerEnabled) listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surfaceVariant
                                ) else listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isAppLockerEnabled) ShieldGreen.copy(alpha = 0.15f)
                                            else ShieldRed.copy(alpha = 0.15f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isAppLockerEnabled) Icons.Default.Shield else Icons.Default.PowerSettingsNew,
                                        contentDescription = null,
                                        tint = if (isAppLockerEnabled) ShieldGreen else ShieldRed,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = if (isAppLockerEnabled) AppStrings.get("protection_active", currentLanguage)
                                        else AppStrings.get("protection_paused", currentLanguage),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isAppLockerEnabled) {
                                            if (currentLanguage == AppLanguage.BENGALI) "রিয়েল-টাইম নিরাপত্তা সক্রিয়" else "Guarding protected apps"
                                        } else {
                                            if (currentLanguage == AppLanguage.BENGALI) "লকার সাময়িক স্থগিত" else "Locker is currently disabled"
                                        },
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = isAppLockerEnabled,
                                onCheckedChange = { isEnabled ->
                                    appPreferences.isAppLockerEnabled = isEnabled
                                    if (isEnabled) {
                                        ServiceHelper.startMonitoringService(context)
                                    } else {
                                        ServiceHelper.stopMonitoringService(context)
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF070F1E),
                                    checkedTrackColor = CyberCyan,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.outline
                                )
                            )
                        }

                        if (!isSystemReady && isAppLockerEnabled) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ShieldOrange.copy(alpha = 0.12f))
                                    .clickable { onNavigateToPermissions() }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = ShieldOrange,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (currentLanguage == AppLanguage.BENGALI) "কিছু অনুমতি বাকি রয়েছে। ট্যাপ করে অনুমতি দিন।" else "Permissions incomplete. Tap to configure.",
                                        fontSize = 12.sp,
                                        color = ShieldOrange,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // STATS 3-GRID CARDS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = AppStrings.get("total_apps", currentLanguage),
                    count = totalApps,
                    icon = Icons.Default.Apps,
                    accentColor = CyberCyan,
                    isLoading = isLoading
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = AppStrings.get("locked_apps", currentLanguage),
                    count = lockedCount,
                    icon = Icons.Default.Lock,
                    accentColor = ShieldGreen,
                    isLoading = isLoading
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = AppStrings.get("unlocked_apps", currentLanguage),
                    count = unlockedCount,
                    icon = Icons.Default.LockOpen,
                    accentColor = ShieldOrange,
                    isLoading = isLoading
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // QUICK ACTIONS & MANAGE BUTTON
            CyberCard {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (currentLanguage == AppLanguage.BENGALI) "দ্রুত নিয়ন্ত্রণ" else "Quick Controls",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { appRepository.lockAllApps() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = AppStrings.get("quick_lock_all", currentLanguage),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        OutlinedButton(
                            onClick = { appRepository.unlockAllApps() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = AppStrings.get("unlock_all", currentLanguage),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Big Manage Apps banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            .clickable { onNavigateToManageApps() }
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Apps,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = AppStrings.get("manage_apps", currentLanguage),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.BENGALI) "অ্যাপ তালিকা দেখুন এবং লক/আনলক করুন" else "View installed apps & customize locks",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DEVELOPER ATTRIBUTION CARD
            CyberCard {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToDeveloper() }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(CyberCyanDim.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = AppStrings.get("developer_name", currentLanguage),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Telegram: @Molla_Mohammad_Rifat_Osama",
                                    fontSize = 11.sp,
                                    color = CyberCyan
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    icon: ImageVector,
    accentColor: Color,
    isLoading: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = accentColor
                )
            } else {
                Text(
                    text = count.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
