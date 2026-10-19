package com.example.ui.lock

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.RifatAppLockerApplication
import com.example.data.model.LockType
import com.example.data.model.SecurityQuestion
import com.example.ui.common.AppDrawableIcon
import com.example.ui.common.AppLanguage
import com.example.ui.common.AppStrings
import com.example.ui.common.CyberCard
import com.example.ui.common.GradientButton
import com.example.ui.common.PatternLockView
import com.example.ui.common.PatternState
import com.example.ui.common.toPatternString
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ShieldRed
import kotlinx.coroutines.delay

class LockOverlayActivity : ComponentActivity() {

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val targetPackage = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""

        val app = application as RifatAppLockerApplication
        val repo = app.appRepository
        val prefs = app.appPreferences
        val security = app.securityUtils

        setContent {
            MyApplicationTheme(darkTheme = true) {
                LockScreenContent(
                    targetPackage = targetPackage,
                    appName = repo.getAppName(targetPackage),
                    appIcon = repo.getAppIcon(targetPackage),
                    lockType = prefs.lockType,
                    currentLanguage = if (prefs.language == "bn") AppLanguage.BENGALI else AppLanguage.ENGLISH,
                    onSuccessfulUnlock = {
                        repo.markPackageTemporarilyUnlocked(targetPackage)
                        finish()
                    },
                    onBackToHome = {
                        // Safe exit: Redirect back to launcher Home
                        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_HOME)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(homeIntent)
                        finish()
                    },
                    onVerifyPassword = { input ->
                        security.verifyCredential(input, prefs.credentialSalt, prefs.credentialHash)
                    },
                    onVerifyPattern = { patternList ->
                        val patternStr = patternList.toPatternString()
                        security.verifyCredential(patternStr, prefs.credentialSalt, prefs.credentialHash)
                    },
                    securityQuestionId = prefs.securityQuestionId,
                    onVerifyRecoveryAnswer = { answer ->
                        security.verifySecurityAnswer(answer, prefs.securityAnswerSalt, prefs.securityAnswerHash)
                    }
                )
            }
        }
    }
}

@Composable
fun LockScreenContent(
    targetPackage: String,
    appName: String,
    appIcon: android.graphics.drawable.Drawable?,
    lockType: LockType,
    currentLanguage: AppLanguage,
    onSuccessfulUnlock: () -> Unit,
    onBackToHome: () -> Unit,
    onVerifyPassword: (String) -> Boolean,
    onVerifyPattern: (List<Int>) -> Boolean,
    securityQuestionId: Int,
    onVerifyRecoveryAnswer: (String) -> Boolean
) {
    val context = LocalContext.current
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun errorVibrate() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 70, 70, 70), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(150)
            }
        } catch (_: Exception) {}
    }

    // Intercept hardware and gesture back button to go Home instead of letting user enter protected app
    BackHandler {
        onBackToHome()
    }

    var passwordInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var patternState by remember { mutableStateOf(PatternState.INPUT) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var failedCount by remember { mutableStateOf(0) }
    var lockoutSeconds by remember { mutableLongStateOf(0L) }

    // Recovery dialog state
    var showRecoveryDialog by remember { mutableStateOf(false) }
    var recoveryAnswerInput by remember { mutableStateOf("") }
    var recoveryError by remember { mutableStateOf<String?>(null) }

    val presetQuestions = SecurityQuestion.PRESET_QUESTIONS
    val currentQuestion = presetQuestions.find { it.id == securityQuestionId } ?: presetQuestions.first()

    LaunchedEffect(lockoutSeconds) {
        if (lockoutSeconds > 0) {
            delay(1000)
            lockoutSeconds -= 1
        }
    }

    fun handleFailedAttempt() {
        failedCount += 1
        errorVibrate()
        if (failedCount >= 5) {
            lockoutSeconds = 30
            errorMessage = if (currentLanguage == AppLanguage.BENGALI) "অতিরিক্ত ভুল প্রচেষ্টা। ৩০ সেকেন্ড অপেক্ষা করুন।" else "Too many failed attempts. Wait 30 seconds."
        } else {
            errorMessage = "${AppStrings.get("wrong_credential", currentLanguage)} ($failedCount/5)"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightDark)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Target App Icon & Identity
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AppDrawableIcon(drawable = appIcon, modifier = Modifier.size(54.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = appName.ifBlank { "Protected Application" },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (currentLanguage == AppLanguage.BENGALI) "রিফাত লকার দ্বারা সুরক্ষিত" else "Protected by Rifat App Locker",
                    fontSize = 12.sp,
                    color = CyberCyan
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // AUTHENTICATION WIDGET
            if (lockType == LockType.PASSWORD) {
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = {
                        passwordInput = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = lockoutSeconds == 0L,
                    singleLine = true,
                    placeholder = { Text(AppStrings.get("enter_password", currentLanguage)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        if (lockoutSeconds == 0L) {
                            if (onVerifyPassword(passwordInput)) {
                                onSuccessfulUnlock()
                            } else {
                                handleFailedAttempt()
                            }
                        }
                    }),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle visibility",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                GradientButton(
                    text = if (lockoutSeconds > 0) "${lockoutSeconds}s" else AppStrings.get("unlock", currentLanguage),
                    enabled = lockoutSeconds == 0L && passwordInput.isNotBlank(),
                    leadingIcon = Icons.Default.ArrowForward,
                    onClick = {
                        if (onVerifyPassword(passwordInput)) {
                            onSuccessfulUnlock()
                        } else {
                            handleFailedAttempt()
                        }
                    }
                )
            } else {
                // PATTERN GRID
                Text(
                    text = AppStrings.get("draw_pattern", currentLanguage),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                PatternLockView(
                    patternState = patternState,
                    onPatternCompleted = { pattern ->
                        if (lockoutSeconds == 0L) {
                            if (onVerifyPattern(pattern)) {
                                patternState = PatternState.SUCCESS
                                onSuccessfulUnlock()
                            } else {
                                patternState = PatternState.ERROR
                                handleFailedAttempt()
                            }
                        }
                    },
                    onPatternCleared = {
                        patternState = PatternState.INPUT
                    }
                )
            }

            // Error feedback
            AnimatedVisibility(visible = errorMessage != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = ShieldRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = ShieldRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Forgot Credential & Recovery Button
            TextButton(onClick = { showRecoveryDialog = true }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppStrings.get("forgot_credential", currentLanguage),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }

    // Emergency Recovery Dialog
    if (showRecoveryDialog) {
        AlertDialog(
            onDismissRequest = { showRecoveryDialog = false },
            title = {
                Text(
                    text = AppStrings.get("reset_credential", currentLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = if (currentLanguage == AppLanguage.BENGALI) currentQuestion.questionBn else currentQuestion.questionEn,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = recoveryAnswerInput,
                        onValueChange = {
                            recoveryAnswerInput = it
                            recoveryError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text(AppStrings.get("security_answer", currentLanguage)) },
                        shape = RoundedCornerShape(10.dp)
                    )
                    if (recoveryError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = recoveryError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (onVerifyRecoveryAnswer(recoveryAnswerInput)) {
                            showRecoveryDialog = false
                            onSuccessfulUnlock()
                        } else {
                            recoveryError = if (currentLanguage == AppLanguage.BENGALI) "উত্তর সঠিক নয়।" else "Incorrect answer."
                        }
                    }
                ) {
                    Text(if (currentLanguage == AppLanguage.BENGALI) "যাচাই করুন" else "Verify")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRecoveryDialog = false }) {
                    Text(if (currentLanguage == AppLanguage.BENGALI) "বাতিল" else "Cancel")
                }
            }
        )
    }
}
