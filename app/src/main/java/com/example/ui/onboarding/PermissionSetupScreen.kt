package com.example.ui.onboarding

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.service.ServiceHelper
import com.example.ui.common.AppLanguage
import com.example.ui.common.AppStrings
import com.example.ui.common.CyberCard
import com.example.ui.common.GradientButton
import com.example.ui.common.RifatTopBar
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.ShieldRed

@Composable
fun PermissionSetupScreen(
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    onNavigateBack: () -> Unit = {},
    canNavigateBack: Boolean = false,
    onPermissionsCompleted: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scrollState = rememberScrollState()

    var hasAccessibility by remember { mutableStateOf(ServiceHelper.isAccessibilityServiceEnabled(context)) }
    var hasUsageStats by remember { mutableStateOf(ServiceHelper.hasUsageStatsPermission(context)) }
    var hasOverlay by remember { mutableStateOf(ServiceHelper.hasOverlayPermission(context)) }
    var hasBatteryOpt by remember { mutableStateOf(ServiceHelper.isIgnoringBatteryOptimizations(context)) }

    fun refreshPermissions() {
        hasAccessibility = ServiceHelper.isAccessibilityServiceEnabled(context)
        hasUsageStats = ServiceHelper.hasUsageStatsPermission(context)
        hasOverlay = ServiceHelper.hasOverlayPermission(context)
        hasBatteryOpt = ServiceHelper.isIgnoringBatteryOptimizations(context)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        RifatTopBar(
            title = AppStrings.get("grant_permissions", currentLanguage),
            canNavigateBack = canNavigateBack,
            onNavigateBack = onNavigateBack,
            currentLanguage = currentLanguage,
            onToggleLanguage = onToggleLanguage
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (currentLanguage == AppLanguage.BENGALI) "নিরাপত্তা অনুমতি সেটআপ" else "System Permissions Setup",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.BENGALI) "সঠিক কার্যক্ষমতার জন্য অনুমতিগুলো প্রয়োজন" else "Required to detect and guard protected applications",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = { refreshPermissions() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Accessibility Service Card
            PermissionCard(
                icon = Icons.Default.AccessibilityNew,
                title = AppStrings.get("accessibility_perm", currentLanguage),
                desc = AppStrings.get("accessibility_perm_desc", currentLanguage),
                isGranted = hasAccessibility,
                currentLanguage = currentLanguage,
                onGrantClick = {
                    ServiceHelper.openAccessibilitySettings(context)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Usage Access Card
            PermissionCard(
                icon = Icons.Default.DataUsage,
                title = AppStrings.get("usage_stats_perm", currentLanguage),
                desc = AppStrings.get("usage_stats_perm_desc", currentLanguage),
                isGranted = hasUsageStats,
                currentLanguage = currentLanguage,
                onGrantClick = {
                    ServiceHelper.openUsageAccessSettings(context)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Overlay Permission Card
            PermissionCard(
                icon = Icons.Default.Layers,
                title = AppStrings.get("overlay_perm", currentLanguage),
                desc = AppStrings.get("overlay_perm_desc", currentLanguage),
                isGranted = hasOverlay,
                currentLanguage = currentLanguage,
                onGrantClick = {
                    ServiceHelper.openOverlaySettings(context)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Battery Optimization / OEM Card
            PermissionCard(
                icon = Icons.Default.BatteryAlert,
                title = AppStrings.get("ignore_battery", currentLanguage),
                desc = if (currentLanguage == AppLanguage.BENGALI) "TECNO / HiOS / MIUI ডিভাইসে ব্যাকগ্রাউন্ডে সার্ভিস সচল রাখতে এটি অন করুন।" else "Prevents OEM aggressive battery managers from killing the locker service.",
                isGranted = hasBatteryOpt,
                currentLanguage = currentLanguage,
                onGrantClick = {
                    ServiceHelper.requestIgnoreBatteryOptimizations(context)
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            GradientButton(
                text = if (hasAccessibility || hasUsageStats) {
                    if (currentLanguage == AppLanguage.BENGALI) "ড্যাশবোর্ডে প্রবেশ করুন" else "Go to Dashboard"
                } else {
                    if (currentLanguage == AppLanguage.BENGALI) "অনুমতি ছাড়া এগিয়ে যান" else "Continue to Dashboard"
                },
                leadingIcon = Icons.Default.CheckCircle,
                onClick = onPermissionsCompleted
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PermissionCard(
    icon: ImageVector,
    title: String,
    desc: String,
    isGranted: Boolean,
    currentLanguage: AppLanguage,
    onGrantClick: () -> Unit
) {
    CyberCard(borderGlow = isGranted) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isGranted) ShieldGreen.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isGranted) ShieldGreen else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isGranted) AppStrings.get("granted", currentLanguage) else AppStrings.get("not_granted", currentLanguage),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = if (isGranted) ShieldGreen else ShieldRed
                    )
                }

                if (isGranted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Granted",
                        tint = ShieldGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = desc,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )

            if (!isGranted) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onGrantClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.BENGALI) "অনুমতি প্রদান করুন" else "Enable in Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
