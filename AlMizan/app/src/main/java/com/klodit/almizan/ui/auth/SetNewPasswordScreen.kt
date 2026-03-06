package com.klodit.almizan.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.klodit.almizan.R

// ─────────────────────────────────────────────
//  COLORS
// ─────────────────────────────────────────────
private val SnpDarkHeader  = Color(0xFF364150)
private val SnpGreenAccent = Color(0xFF4CAE4F)
private val SnpWhite       = Color.White
private val SnpBorderGrey  = Color(0xFFDDE3E8)
private val SnpTextDark    = Color(0xFF1A2B38)
private val SnpTextMid     = Color(0xFF4A6070)
private val SnpTextLight   = Color(0xFF8FA3B0)
private val SnpFieldBg     = Color(0xFFF8FAFB)
private val SnpPageBg      = Color(0xFFECEFF1)
private val SnpIconBg      = Color(0xFFE3F0FD)
private val SnpIconTint    = Color(0xFF1E88E5)
private val SnpRedWeak     = Color(0xFFE53935)

// ─────────────────────────────────────────────
//  STRINGS
// ─────────────────────────────────────────────
private data class SnpStrings(
    val title          : String,
    val subtitle       : String,
    val recoveryLabel  : String,
    val newPassLabel   : String,
    val confirmLabel   : String,
    val newPassPh      : String,
    val confirmPh      : String,
    val saveBtn        : String,
    val footer         : String,
    val weak           : String,
    val medium         : String,
    val strong         : String,
    val passwordMismatch: String
) {
    companion object {
        val french = SnpStrings(
            title           = "Définir un nouveau mot de passe",
            subtitle        = "Entrez le code à 6 chiffres envoyé à votre e-mail et choisissez un nouveau mot de passe fort.",
            recoveryLabel   = "Code de récupération",
            newPassLabel    = "Nouveau mot de passe",
            confirmLabel    = "Confirmer le mot de passe",
            newPassPh       = "••••••••",
            confirmPh       = "••••••••",
            saveBtn         = "Enregistrer le nouveau mot de passe",
            footer          = "MINISTÈRE DES FINANCES",
            weak            = "FAIBLE",
            medium          = "MOYEN",
            strong          = "FORT",
            passwordMismatch = "Les mots de passe ne correspondent pas"
        )
        val arabic = SnpStrings(
            title           = "تعيين كلمة مرور جديدة",
            subtitle        = "أدخل الرمز المكون من 6 أرقام المرسل إلى بريدك الإلكتروني واختر كلمة مرور قوية.",
            recoveryLabel   = "رمز الاسترداد",
            newPassLabel    = "كلمة المرور الجديدة",
            confirmLabel    = "تأكيد كلمة المرور",
            newPassPh       = "••••••••",
            confirmPh       = "••••••••",
            saveBtn         = "حفظ كلمة المرور الجديدة",
            footer          = "وزارة المالية",
            weak            = "ضعيف",
            medium          = "متوسط",
            strong          = "قوي",
            passwordMismatch = "كلمتا المرور غير متطابقتين"
        )
        val english = SnpStrings(
            title           = "Set New Password",
            subtitle        = "Enter the 6-digit code sent to your email and choose a strong new password.",
            recoveryLabel   = "Recovery Code",
            newPassLabel    = "New Password",
            confirmLabel    = "Confirm Password",
            newPassPh       = "••••••••",
            confirmPh       = "••••••••",
            saveBtn         = "Save New Password",
            footer          = "MINISTÈRE DES FINANCES",
            weak            = "WEAK",
            medium          = "Medium strength",
            strong          = "STRONG",
            passwordMismatch = "Passwords do not match"
        )
    }
}

// ─────────────────────────────────────────────
//  PASSWORD STRENGTH
// ─────────────────────────────────────────────
private fun snpPasswordStrength(password: String): Int {
    if (password.isEmpty()) return 0
    var score = 0
    if (password.length >= 8) score++
    if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++
    return score
}

// ─────────────────────────────────────────────
//  SET NEW PASSWORD SCREEN
// ─────────────────────────────────────────────
@Composable
fun SetNewPasswordScreen(
    onSaveClick      : (code: String, newPassword: String) -> Unit = { _, _ -> },
    onBackClick      : () -> Unit = {},
    selectedLang     : AppLanguage = AppLanguage.FRENCH,
    onLanguageChange : (AppLanguage) -> Unit = {}
) {
    val strings = when (selectedLang) {
        AppLanguage.FRENCH  -> SnpStrings.french
        AppLanguage.ARABIC  -> SnpStrings.arabic
        AppLanguage.ENGLISH -> SnpStrings.english
    }

    // 6-digit recovery code boxes
    val digits          = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    var focusedIndex    by remember { mutableIntStateOf(0) }

    // password fields
    var newPassword        by remember { mutableStateOf("") }
    var confirmPassword    by remember { mutableStateOf("") }
    var newPassVisible     by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }
    var confirmTouched     by remember { mutableStateOf(false) }

    val fullCode      = digits.joinToString("")
    val codeComplete  = digits.all { it.isNotEmpty() }
    val strength      = snpPasswordStrength(newPassword)
    val strengthLabel = when (strength) { 1 -> strings.weak; 2 -> strings.medium; 3 -> strings.strong; else -> "" }
    val strengthColor = when (strength) { 1 -> SnpRedWeak; 2 -> Color(0xFFFFA726); 3 -> SnpGreenAccent; else -> Color.Transparent }

    val passwordsMatch = newPassword == confirmPassword
    val showMismatch   = confirmTouched && confirmPassword.isNotEmpty() && !passwordsMatch

    val canSave = codeComplete && newPassword.length >= 6 && passwordsMatch && confirmPassword.isNotEmpty()

    val screenWidth   = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth     = if (screenWidth < 500.dp) screenWidth * 0.90f else 420.dp
    val overlapAmount = 32.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SnpPageBg)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── dark header ──────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SnpDarkHeader)
                .statusBarsPadding()
                .padding(top = 8.dp, bottom = overlapAmount + 24.dp)
        ) {
            Row(
                modifier          = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SnpWhite, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(8.dp))
                Image(
                    painter            = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier           = Modifier.size(44.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("AL-MIZAN", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = SnpWhite, letterSpacing = 1.sp)
                    Text("SOVEREIGN PROCUREMENT", fontSize = 9.sp, color = SnpGreenAccent, letterSpacing = 1.sp)
                }
            }
        }

        // ── white card ───────────────────────
        Card(
            modifier  = Modifier
                .width(cardWidth)
                .offset(y = -overlapAmount)
                .zIndex(1f),
            shape     = RoundedCornerShape(20.dp),
            colors    = CardDefaults.cardColors(containerColor = SnpWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier            = Modifier.fillMaxWidth().padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // key icon circle
                Box(
                    modifier         = Modifier.size(64.dp).clip(CircleShape).background(SnpIconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = SnpIconTint, modifier = Modifier.size(32.dp))
                }

                Spacer(Modifier.height(20.dp))

                Text(strings.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = SnpTextDark, textAlign = TextAlign.Center)
                Spacer(Modifier.height(10.dp))
                Text(strings.subtitle, fontSize = 13.sp, color = SnpTextMid, textAlign = TextAlign.Center, lineHeight = 19.sp)

                Spacer(Modifier.height(28.dp))

                // ── recovery code boxes ──────────────
                Text(strings.recoveryLabel, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SnpTextDark, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    digits.forEachIndexed { index, digit ->
                        val isFocused = focusedIndex == index
                        BasicTextField(
                            value         = digit,
                            onValueChange = { newVal ->
                                val clean = newVal.filter { it.isDigit() }.take(1)
                                digits[index] = clean
                                if (clean.isNotEmpty() && index < 5) {
                                    focusedIndex = index + 1
                                    focusRequesters[index + 1].requestFocus()
                                }
                                if (clean.isEmpty() && index > 0) {
                                    focusedIndex = index - 1
                                    focusRequesters[index - 1].requestFocus()
                                }
                            },
                            modifier        = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isFocused) SnpWhite else Color(0xFFF5F7F9))
                                .border(
                                    width = if (isFocused) 1.5.dp else 1.dp,
                                    color = if (isFocused) SnpGreenAccent else SnpBorderGrey,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .focusRequester(focusRequesters[index])
                                .onFocusChanged { if (it.isFocused) focusedIndex = index },
                            textStyle       = TextStyle(
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color      = SnpTextDark,
                                textAlign  = TextAlign.Center
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            cursorBrush     = SolidColor(SnpGreenAccent),
                            singleLine      = true,
                            decorationBox   = { inner -> Box(contentAlignment = Alignment.Center) { inner() } }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ── new password ─────────────────────
                Text(strings.newPassLabel, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SnpTextDark, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value                = newPassword,
                    onValueChange        = { newPassword = it },
                    placeholder          = { Text(strings.newPassPh, color = SnpTextLight, fontSize = 13.sp) },
                    leadingIcon          = { Icon(Icons.Outlined.Lock, null, tint = SnpTextLight, modifier = Modifier.size(18.dp)) },
                    trailingIcon         = {
                        TextButton(onClick = { newPassVisible = !newPassVisible }, contentPadding = PaddingValues(horizontal = 10.dp)) {
                            Text(if (newPassVisible) "Masquer" else "Afficher", fontSize = 11.sp, color = SnpGreenAccent, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    visualTransformation = if (newPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier             = Modifier.fillMaxWidth(),
                    singleLine           = true,
                    shape                = RoundedCornerShape(8.dp),
                    colors               = snpFieldColors()
                )

                // strength bar
                if (newPassword.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        repeat(3) { i ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (i < strength) strengthColor else Color(0xFFE0E0E0))
                            )
                        }
                        Text(strengthLabel, fontSize = 10.sp, color = strengthColor, fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(56.dp), textAlign = TextAlign.End)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── confirm password ─────────────────
                Text(strings.confirmLabel, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SnpTextDark, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value                = confirmPassword,
                    onValueChange        = { confirmPassword = it; confirmTouched = true },
                    placeholder          = { Text(strings.confirmPh, color = SnpTextLight, fontSize = 13.sp) },
                    leadingIcon          = { Icon(Icons.Outlined.Lock, null, tint = SnpTextLight, modifier = Modifier.size(18.dp)) },
                    trailingIcon         = {
                        TextButton(onClick = { confirmPassVisible = !confirmPassVisible }, contentPadding = PaddingValues(horizontal = 10.dp)) {
                            Text(if (confirmPassVisible) "Masquer" else "Afficher", fontSize = 11.sp, color = SnpGreenAccent, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    visualTransformation = if (confirmPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError              = showMismatch,
                    supportingText       = if (showMismatch) {
                        { Text(strings.passwordMismatch, color = SnpRedWeak, fontSize = 11.sp) }
                    } else null,
                    modifier             = Modifier.fillMaxWidth(),
                    singleLine           = true,
                    shape                = RoundedCornerShape(8.dp),
                    colors               = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = if (showMismatch) SnpRedWeak else SnpGreenAccent,
                        unfocusedBorderColor    = if (showMismatch) SnpRedWeak else SnpBorderGrey,
                        focusedTextColor        = SnpTextDark,
                        unfocusedTextColor      = SnpTextDark,
                        cursorColor             = SnpGreenAccent,
                        focusedContainerColor   = SnpFieldBg,
                        unfocusedContainerColor = SnpFieldBg,
                        errorBorderColor        = SnpRedWeak,
                        errorContainerColor     = SnpFieldBg
                    )
                )

                Spacer(Modifier.height(24.dp))

                // ── save button ──────────────────────
                Button(
                    onClick  = { onSaveClick(fullCode, newPassword) },
                    enabled  = canSave,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = SnpGreenAccent,
                        disabledContainerColor = Color(0xFFB0CDB9)
                    )
                ) {
                    Icon(Icons.Outlined.Check, null, tint = SnpWhite, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(strings.saveBtn, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SnpWhite)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // language selector
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            AppLanguage.entries.forEachIndexed { index, lang ->
                if (index > 0) Text("  •  ", fontSize = 11.sp, color = SnpTextLight)
                Text(
                    text       = lang.label,
                    fontSize   = 11.sp,
                    color      = if (selectedLang == lang) SnpGreenAccent else SnpTextLight,
                    fontWeight = if (selectedLang == lang) FontWeight.Bold else FontWeight.Normal,
                    modifier   = Modifier.clickable { onLanguageChange(lang) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(strings.footer, fontSize = 9.sp, color = SnpTextLight, letterSpacing = 1.5.sp, textAlign = TextAlign.Center)

        Spacer(Modifier.height(32.dp))
    }

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }
}

// ─────────────────────────────────────────────
//  HELPERS
// ─────────────────────────────────────────────
@Composable
private fun snpFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = SnpGreenAccent,
    unfocusedBorderColor    = SnpBorderGrey,
    focusedTextColor        = SnpTextDark,
    unfocusedTextColor      = SnpTextDark,
    cursorColor             = SnpGreenAccent,
    focusedContainerColor   = SnpFieldBg,
    unfocusedContainerColor = SnpFieldBg
)