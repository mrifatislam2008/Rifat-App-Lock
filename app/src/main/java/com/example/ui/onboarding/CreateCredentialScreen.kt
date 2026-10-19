package com.example.ui.onboarding

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LockType
import com.example.data.model.SecurityQuestion
import com.example.data.preferences.AppPreferences
import com.example.data.security.SecurityUtils
import com.example.ui.common.AppLanguage
import com.example.ui.common.AppStrings
import com.example.ui.common.CyberCard
import com.example.ui.common.GradientButton
import com.example.ui.common.PatternLockView
import com.example.ui.common.PatternState
import com.example.ui.common.RifatTopBar
import com.example.ui.common.toPatternString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCredentialScreen(
    preferences: AppPreferences,
    securityUtils: SecurityUtils,
    currentLanguage: AppLanguage,
    isChangeMode: Boolean = false,
    onToggleLanguage: () -> Unit,
    onNavigateBack: () -> Unit = {},
    onCredentialSaved: () -> Unit
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    var selectedLockType by remember { mutableStateOf(if (isChangeMode) preferences.lockType else LockType.PASSWORD) }

    // Password State
    var passwordInput by remember { mutableStateOf("") }
    var passwordConfirmInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Pattern State
    var patternFirst by remember { mutableStateOf<List<Int>?>(null) }
    var patternConfirm by remember { mutableStateOf<List<Int>?>(null) }
    var patternState by remember { mutableStateOf(PatternState.INPUT) }
    var patternStep by remember { mutableIntStateOf(1) } // 1: draw first, 2: confirm

    // Security Recovery Question State
    var selectedQuestionId by remember { mutableIntStateOf(preferences.securityQuestionId) }
    var securityAnswerInput by remember { mutableStateOf("") }
    var isQuestionDropdownExpanded by remember { mutableStateOf(false) }

    // Error / status feedback
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val presetQuestions = SecurityQuestion.PRESET_QUESTIONS
    val currentQuestion = presetQuestions.find { it.id == selectedQuestionId } ?: presetQuestions.first()

    fun validateAndSave() {
        errorMessage = null
        if (selectedLockType == LockType.PASSWORD) {
            if (passwordInput.length < 4) {
                errorMessage = if (currentLanguage == AppLanguage.BENGALI) "পাসওয়ার্ড কমপক্ষে ৪ অক্ষরের হতে হবে।" else "Password must be at least 4 characters."
                return
            }
            if (passwordInput != passwordConfirmInput) {
                errorMessage = AppStrings.get("credential_mismatch", currentLanguage)
                return
            }
        } else {
            if (patternFirst == null || patternConfirm == null) {
                errorMessage = if (currentLanguage == AppLanguage.BENGALI) "অনুগ্রহ করে প্যাটার্ন নিশ্চিত করুন।" else "Please confirm your pattern."
                return
            }
            if (patternFirst != patternConfirm) {
                errorMessage = AppStrings.get("credential_mismatch", currentLanguage)
                patternState = PatternState.ERROR
                return
            }
        }

        // Validate recovery answer
        if (securityAnswerInput.trim().length < 2) {
            errorMessage = if (currentLanguage == AppLanguage.BENGALI) "অনুগ্রহ করে রিকভারি প্রশ্নের উত্তর প্রদান করুন।" else "Please provide an answer for the security recovery question."
            return
        }

        // Salt and Hash
        val credString = if (selectedLockType == LockType.PASSWORD) passwordInput else patternFirst!!.toPatternString()
        val credSalt = securityUtils.generateSalt()
        val credHash = securityUtils.hashCredential(credString, credSalt)

        val answerSalt = securityUtils.generateSalt()
        val answerHash = securityUtils.hashSecurityAnswer(securityAnswerInput, answerSalt)

        // Save
        preferences.lockType = selectedLockType
        preferences.credentialSalt = credSalt
        preferences.credentialHash = credHash
        preferences.securityQuestionId = selectedQuestionId
        preferences.securityAnswerSalt = answerSalt
        preferences.securityAnswerHash = answerHash
        preferences.isSetupCompleted = true

        onCredentialSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        RifatTopBar(
            title = if (isChangeMode) AppStrings.get("security_settings", currentLanguage) else AppStrings.get("setup_lock", currentLanguage),
            canNavigateBack = isChangeMode,
            onNavigateBack = onNavigateBack,
            currentLanguage = currentLanguage,
            onToggleLanguage = onToggleLanguage
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isChangeMode) {
                    if (currentLanguage == AppLanguage.BENGALI) "নিরাপত্তা কোড পরিবর্তন করুন" else "Change Security Credentials"
                } else {
                    if (currentLanguage == AppLanguage.BENGALI) "লক কোড তৈরি করুন" else "Create Security Lock"
                },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (currentLanguage == AppLanguage.BENGALI) "পাসওয়ার্ড অথবা ৩×৩ প্যাটার্ন লক নির্বাচন করুন" else "Select your preferred authentication method",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Method Selector Tabs
            TabRow(
                selectedTabIndex = if (selectedLockType == LockType.PASSWORD) 0 else 1,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[if (selectedLockType == LockType.PASSWORD) 0 else 1]),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .fillMaxWidth()
            ) {
                Tab(
                    selected = selectedLockType == LockType.PASSWORD,
                    onClick = {
                        selectedLockType = LockType.PASSWORD
                        errorMessage = null
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Password, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(AppStrings.get("password", currentLanguage), fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
                Tab(
                    selected = selectedLockType == LockType.PATTERN,
                    onClick = {
                        selectedLockType = LockType.PATTERN
                        errorMessage = null
                        patternFirst = null
                        patternConfirm = null
                        patternStep = 1
                        patternState = PatternState.INPUT
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(AppStrings.get("pattern", currentLanguage), fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PASSWORD FORM
            if (selectedLockType == LockType.PASSWORD) {
                CyberCard {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = AppStrings.get("enter_password", currentLanguage),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle visibility"
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = AppStrings.get("confirm_password", currentLanguage),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = passwordConfirmInput,
                            onValueChange = { passwordConfirmInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            } else {
                // PATTERN FORM
                CyberCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (patternStep == 1) {
                                AppStrings.get("draw_pattern", currentLanguage)
                            } else {
                                AppStrings.get("confirm_pattern", currentLanguage)
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (patternStep == 1) {
                                AppStrings.get("pattern_min_dots", currentLanguage)
                            } else {
                                if (currentLanguage == AppLanguage.BENGALI) "নিশ্চিত করতে একই প্যাটার্নটি পুনরায় আঁকুন" else "Draw the same pattern again to confirm"
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PatternLockView(
                            patternState = patternState,
                            onPatternCompleted = { pattern ->
                                if (pattern.size < 4) {
                                    patternState = PatternState.ERROR
                                    errorMessage = AppStrings.get("pattern_min_dots", currentLanguage)
                                } else {
                                    if (patternStep == 1) {
                                        patternFirst = pattern
                                        patternStep = 2
                                        patternState = PatternState.INPUT
                                        errorMessage = null
                                    } else {
                                        patternConfirm = pattern
                                        if (patternFirst == pattern) {
                                            patternState = PatternState.SUCCESS
                                            errorMessage = null
                                        } else {
                                            patternState = PatternState.ERROR
                                            errorMessage = AppStrings.get("credential_mismatch", currentLanguage)
                                        }
                                    }
                                }
                            },
                            onPatternCleared = {
                                patternState = PatternState.INPUT
                            }
                        )

                        if (patternStep == 2) {
                            Text(
                                text = if (currentLanguage == AppLanguage.BENGALI) "পুনরায় প্রথম থেকে আঁকুন" else "Reset and redraw initial pattern",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clickable {
                                        patternStep = 1
                                        patternFirst = null
                                        patternConfirm = null
                                        patternState = PatternState.INPUT
                                        errorMessage = null
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECURITY RECOVERY QUESTION SECTION
            CyberCard {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.get("security_question", currentLanguage),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = isQuestionDropdownExpanded,
                        onExpandedChange = { isQuestionDropdownExpanded = !isQuestionDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = if (currentLanguage == AppLanguage.BENGALI) currentQuestion.questionBn else currentQuestion.questionEn,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isQuestionDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = isQuestionDropdownExpanded,
                            onDismissRequest = { isQuestionDropdownExpanded = false }
                        ) {
                            presetQuestions.forEach { q ->
                                DropdownMenuItem(
                                    text = {
                                        Text(if (currentLanguage == AppLanguage.BENGALI) q.questionBn else q.questionEn)
                                    },
                                    onClick = {
                                        selectedQuestionId = q.id
                                        isQuestionDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = AppStrings.get("security_answer", currentLanguage),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = securityAnswerInput,
                        onValueChange = { securityAnswerInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = {
                            Text(
                                if (currentLanguage == AppLanguage.BENGALI) "গোপন উত্তর লিখুন (কেস-সেনসিটিভ নয়)" else "Enter recovery answer (case-insensitive)"
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Error display
            AnimatedVisibility(visible = errorMessage != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            GradientButton(
                text = if (isChangeMode) {
                    if (currentLanguage == AppLanguage.BENGALI) "পরিবর্তন সংরক্ষণ করুন" else "Save Changes"
                } else {
                    if (currentLanguage == AppLanguage.BENGALI) "কোড নিশ্চিত করুন ও এগিয়ে যান" else "Confirm & Continue"
                },
                leadingIcon = Icons.Default.CheckCircle,
                onClick = { validateAndSave() }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
