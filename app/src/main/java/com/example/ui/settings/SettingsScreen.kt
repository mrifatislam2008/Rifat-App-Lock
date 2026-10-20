package com.example.ui.settings

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LockType
import com.example.data.model.RelockPolicy
import com.example.data.preferences.AppPreferences
import com.example.data.security.BiometricHelper
import com.example.service.ServiceHelper
import com.example.ui.common.AppLanguage
import com.example.ui.common.AppStrings
import com.example.ui.common.CyberCard
import com.example.ui.common.RifatTopBar
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.ShieldOrange
import com.example.ui.theme.ShieldRed

@Composable
fun SettingsScreen(
    appPreferences: AppPreferences,
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    onChangeCredentialsClick: () -> Unit,
    onNavigateToPermissions: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val isAppLockerEnabled by appPreferences.isAppLockerEnabledFlow.collectAsState()
    var currentRelockPolicy by remember { mutableStateOf(appPreferences.relockPolicy) }
    var lockSelfApp by remember { mutableStateOf(appPreferences.lockSelfApp) }

    var isFingerprintEnabled by remember { mutableStateOf(appPreferences.isFingerprintEnabled) }
    var isFaceUnlockEnabled by remember { mutableStateOf(appPreferences.isFaceUnlockEnabled) }

    var showWhatsAppDialog by remember { mutableStateOf(false) }
    var whatsAppNumberInput by remember { mutableStateOf(appPreferences.developerWhatsAppNumber) }

    // Biometric hardware detection
    val biometricStatus = remember { BiometricHelper.checkBiometricStatus(context) }
    val isBiometricHardwarePresent = biometricStatus != BiometricHelper.BiometricStatus.NO_HARDWARE

    // Permissions check
    val hasAccessibility = ServiceHelper.isAccessibilityServiceEnabled(context)
    val hasUsageStats = ServiceHelper.hasUsageStatsPermission(context)
    val hasOverlay = ServiceHelper.hasOverlayPermission(context)
    val isBatteryOptimized = ServiceHelper.isIgnoringBatteryOptimizations(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        RifatTopBar(
            title = AppStrings.get("security_settings", currentLanguage),
            canNavigateBack = true,
            onNavigateBack = onNavigateBack,
            currentLanguage = currentLanguage,
            onToggleLanguage = onToggleLanguage
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // MASTER PROTECTION TOGGLE
            CyberCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = AppStrings.get("enable_protection", currentLanguage),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.BENGALI) "অ্যাপ লকার সম্পূর্ণ চালু/বন্ধ করুন" else "Master toggle for all protected apps",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isAppLockerEnabled,
                        onCheckedChange = {
                            appPreferences.isAppLockerEnabled = it
                            if (it) {
                                ServiceHelper.startMonitoringService(context)
                            } else {
                                ServiceHelper.stopMonitoringService(context)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF070F1E),
                            checkedTrackColor = CyberCyan
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1. MANDATORY PRIMARY AUTHENTICATION METHOD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.BENGALI) "প্রাথমিক প্রমাণীকরণ (বাধ্যতামূলক)" else "Primary Authentication (Mandatory)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "MANDATORY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            CyberCard {
                Column {
                    SettingsRowClickable(
                        icon = if (appPreferences.lockType == LockType.PASSWORD) Icons.Default.Password else Icons.Default.LockReset,
                        title = if (appPreferences.lockType == LockType.PASSWORD) AppStrings.get("change_password", currentLanguage) else AppStrings.get("change_pattern", currentLanguage),
                        subtitle = if (currentLanguage == AppLanguage.BENGALI) "বর্তমান পদ্ধতি: ${if (appPreferences.lockType == LockType.PASSWORD) "পাসওয়ার্ড/পিন" else "প্যাটার্ন"}" else "Active Method: ${appPreferences.lockType.name}",
                        onClick = onChangeCredentialsClick
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    SettingsRowClickable(
                        icon = Icons.Default.Security,
                        title = AppStrings.get("security_question", currentLanguage),
                        subtitle = if (currentLanguage == AppLanguage.BENGALI) "রিকভারি প্রশ্ন ও গোপন উত্তর পরিবর্তন করুন" else "Manage recovery questions and answers",
                        onClick = onChangeCredentialsClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. OPTIONAL BIOMETRIC SETTINGS (INDEPENDENT TOGGLES)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.BENGALI) "বায়োমেট্রিক নিরাপত্তা (ঐচ্ছিক)" else "Biometric Security (Optional)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ShieldGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "OPTIONAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShieldGreen
                    )
                }
            }

            CyberCard {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    // Fingerprint Unlock Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = "Fingerprint",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = AppStrings.get("fingerprint_unlock", currentLanguage),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = AppStrings.get("fingerprint_unlock_desc", currentLanguage),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isFingerprintEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled && !isBiometricHardwarePresent) {
                                    Toast.makeText(context, AppStrings.get("biometric_not_available", currentLanguage), Toast.LENGTH_SHORT).show()
                                } else {
                                    isFingerprintEnabled = enabled
                                    appPreferences.isFingerprintEnabled = enabled
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF070F1E),
                                checkedTrackColor = CyberCyan
                            )
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    // Face Unlock Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Face,
                                    contentDescription = "Face Unlock",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = AppStrings.get("face_unlock", currentLanguage),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = AppStrings.get("face_unlock_desc", currentLanguage),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isFaceUnlockEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled && !isBiometricHardwarePresent) {
                                    Toast.makeText(context, AppStrings.get("biometric_not_available", currentLanguage), Toast.LENGTH_SHORT).show()
                                } else {
                                    isFaceUnlockEnabled = enabled
                                    appPreferences.isFaceUnlockEnabled = enabled
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF070F1E),
                                checkedTrackColor = CyberCyan
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. RELOCK POLICY
            Text(
                text = AppStrings.get("relock_policy", currentLanguage),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            CyberCard {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    RelockPolicy.values().forEach { policy ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentRelockPolicy = policy
                                    appPreferences.relockPolicy = policy
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentRelockPolicy == policy,
                                onClick = {
                                    currentRelockPolicy = policy
                                    appPreferences.relockPolicy = policy
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = CyberCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (currentLanguage == AppLanguage.BENGALI) policy.titleBn else policy.titleEn,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. SELF PROTECTION & WHATSAPP SUPPORT
            Text(
                text = if (currentLanguage == AppLanguage.BENGALI) "অ্যাপ সুরক্ষা ও ডেভেলপার সাপোর্ট" else "Self Protection & Developer Support",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            CyberCard {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = AppStrings.get("lock_self_app", currentLanguage),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.BENGALI) "রিফাত অ্যাপ লকার খুলতেও পাসওয়ার্ড লাগবে" else "Require authentication to open this locker app",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = lockSelfApp,
                            onCheckedChange = {
                                lockSelfApp = it
                                appPreferences.lockSelfApp = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF070F1E),
                                checkedTrackColor = CyberCyan
                            )
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    SettingsRowClickable(
                        icon = Icons.Default.Chat,
                        title = AppStrings.get("configure_whatsapp", currentLanguage),
                        subtitle = if (appPreferences.developerWhatsAppNumber.isNotBlank())
                            (if (currentLanguage == AppLanguage.BENGALI) "নম্বর কনফিগার করা হয়েছে" else "Developer WhatsApp number configured")
                        else
                            AppStrings.get("whatsapp_number_not_set", currentLanguage),
                        onClick = { showWhatsAppDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 5. CURRENT PERMISSION ENFORCEMENT STATUS
            Text(
                text = if (currentLanguage == AppLanguage.BENGALI) "বর্তমান অনুমতি ও সিস্টেম অবস্থা" else "Enforcement Permission Health",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            CyberCard {
                Column(modifier = Modifier.padding(14.dp)) {
                    PermissionStatusRow(
                        title = AppStrings.get("accessibility_perm", currentLanguage),
                        isGranted = hasAccessibility,
                        icon = Icons.Default.Accessibility,
                        currentLanguage = currentLanguage,
                        onClick = { ServiceHelper.openAccessibilitySettings(context) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 6.dp))

                    PermissionStatusRow(
                        title = AppStrings.get("usage_stats_perm", currentLanguage),
                        isGranted = hasUsageStats,
                        icon = Icons.Default.QueryStats,
                        currentLanguage = currentLanguage,
                        onClick = { ServiceHelper.openUsageAccessSettings(context) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 6.dp))

                    PermissionStatusRow(
                        title = AppStrings.get("overlay_perm", currentLanguage),
                        isGranted = hasOverlay,
                        icon = Icons.Default.Layers,
                        currentLanguage = currentLanguage,
                        onClick = { ServiceHelper.openOverlaySettings(context) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 6.dp))

                    PermissionStatusRow(
                        title = AppStrings.get("ignore_battery", currentLanguage),
                        isGranted = isBatteryOptimized,
                        icon = Icons.Default.BatteryAlert,
                        currentLanguage = currentLanguage,
                        onClick = { ServiceHelper.requestIgnoreBatteryOptimizations(context) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // Configure WhatsApp Dialog
    if (showWhatsAppDialog) {
        AlertDialog(
            onDismissRequest = { showWhatsAppDialog = false },
            title = {
                Text(
                    text = AppStrings.get("configure_whatsapp", currentLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = if (currentLanguage == AppLanguage.BENGALI)
                            "ডেভেলপার হোয়াটসঅ্যাপ নম্বর আন্তর্জাতিক ফরম্যাটে (যেমন: +৮৮০...) লিখুন:"
                        else
                            "Enter the official Developer WhatsApp phone number in international format (+880...):",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = whatsAppNumberInput,
                        onValueChange = { whatsAppNumberInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("+8801...") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        appPreferences.developerWhatsAppNumber = whatsAppNumberInput
                        showWhatsAppDialog = false
                        Toast.makeText(context, AppStrings.get("save_success", currentLanguage), Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(if (currentLanguage == AppLanguage.BENGALI) "সংরক্ষণ করুন" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWhatsAppDialog = false }) {
                    Text(if (currentLanguage == AppLanguage.BENGALI) "বাতিল" else "Cancel")
                }
            }
        )
    }
}

@Composable
private fun PermissionStatusRow(
    title: String,
    isGranted: Boolean,
    icon: ImageVector,
    currentLanguage: AppLanguage,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isGranted) ShieldGreen else ShieldOrange,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isGranted) ShieldGreen.copy(alpha = 0.15f) else ShieldOrange.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isGranted) AppStrings.get("granted", currentLanguage) else AppStrings.get("not_granted", currentLanguage),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isGranted) ShieldGreen else ShieldOrange
            )
        }
    }
}

@Composable
private fun SettingsRowClickable(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
    }
}
