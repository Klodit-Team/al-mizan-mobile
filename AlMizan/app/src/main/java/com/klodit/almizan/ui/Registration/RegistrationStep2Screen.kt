package com.klodit.almizan.ui.Registration

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.klodit.almizan.ui.auth.AppLanguage

// ─────────────────────────────────────────────
//  COLORS
// ─────────────────────────────────────────────
private val DarkHeader3  = Color(0xFF364150)
private val GreenAccent3 = Color(0xFF4CAE4F)
private val GreenLight3  = Color(0xFFE8F5E9)
private val BlueLight    = Color(0xFFEEF4FB)
private val BlueAccent   = Color(0xFF1565C0)
private val White3       = Color.White
private val BorderGrey3  = Color(0xFFDDE3E8)
private val TextDark3    = Color(0xFF1A2B38)
private val TextMid3     = Color(0xFF4A6070)
private val TextLight3   = Color(0xFF8FA3B0)
private val FieldBg3     = Color(0xFFF8FAFB)
private val PageBg3      = Color(0xFFECEFF1)
private val LabelGrey3   = Color(0xFF6B8090)
private val RedWeak      = Color(0xFFE53935)

// ─────────────────────────────────────────────
//  STEP 2 STRINGS
// ─────────────────────────────────────────────
private data class Step2Strings(
    val stepLabel      : String,
    val sectionTitle   : String,
    val sectionSub     : String,
    val phone          : String,
    val email          : String,
    val password       : String,
    val confirmPassword: String,
    val phPhone        : String,
    val phEmail        : String,
    val phPassword     : String,
    val phConfirm      : String,
    val adminTitle     : String,
    val adminBody      : String,
    val identity       : String,
    val continueBtn    : String,
    val backLabel      : String,
    val footer         : String,
    val weak           : String,
    val medium         : String,
    val strong         : String,
    val passwordMatch  : String,
    val passwordNoMatch: String
) {
    companion object {
        val french = Step2Strings(
            stepLabel       = "ÉTAPE 2 SUR 3",
            sectionTitle    = "Identifiants de l'entreprise",
            sectionSub      = "Créez vos identifiants de connexion sécurisés",
            phone           = "NUMÉRO DE TÉLÉPHONE DE L'ENTREPRISE",
            email           = "EMAIL PROFESSIONNEL (IDENTIFIANT)",
            password        = "MOT DE PASSE",
            confirmPassword = "CONFIRMER LE MOT DE PASSE",
            phPhone         = "+213 555 123 456",
            phEmail         = "nom@entreprise.com",
            phPassword      = "••••••••",
            phConfirm       = "••••••••",
            adminTitle      = "Administrateur Principal",
            adminBody       = "Ce compte aura un accès administratif complet pour gérer le profil de l'organisation.",
            identity        = "VÉRIFICATION D'IDENTITÉ REQUISE",
            continueBtn     = "Continuer vers l'étape 3  →",
            backLabel       = "← Retour aux détails de l'organisation",
            footer          = "© 2024 PLATEFORME SOUVERAINE AL-MIZAN",
            weak            = "FAIBLE",
            medium          = "MOYEN",
            strong          = "FORT",
            passwordMatch   = "Les mots de passe correspondent",
            passwordNoMatch = "Les mots de passe ne correspondent pas"
        )
        val arabic = Step2Strings(
            stepLabel       = "الخطوة 2 من 3",
            sectionTitle    = "بيانات اعتماد الشركة",
            sectionSub      = "أنشئ بيانات اعتماد تسجيل الدخول الآمنة",
            phone           = "رقم هاتف الشركة",
            email           = "البريد المهني (معرّف الدخول)",
            password        = "كلمة المرور",
            confirmPassword = "تأكيد كلمة المرور",
            phPhone         = "+213 555 123 456",
            phEmail         = "اسم@شركة.com",
            phPassword      = "••••••••",
            phConfirm       = "••••••••",
            adminTitle      = "المدير الرئيسي",
            adminBody       = "سيتمتع هذا الحساب بصلاحيات إدارية كاملة لإدارة ملف المنظمة.",
            identity        = "التحقق من الهوية مطلوب",
            continueBtn     = "المتابعة إلى الخطوة 3  ←",
            backLabel       = "→ العودة إلى تفاصيل المنظمة",
            footer          = "© 2024 منصة الميزان السيادية",
            weak            = "ضعيف",
            medium          = "متوسط",
            strong          = "قوي",
            passwordMatch   = "كلمتا المرور متطابقتان",
            passwordNoMatch = "كلمتا المرور غير متطابقتين"
        )
        val english = Step2Strings(
            stepLabel       = "STEP 2 OF 3",
            sectionTitle    = "Company Credentials",
            sectionSub      = "Create your secure organization login credentials",
            phone           = "COMPANY PHONE NUMBER",
            email           = "BUSINESS EMAIL (LOGIN)",
            password        = "PASSWORD",
            confirmPassword = "CONFIRM PASSWORD",
            phPhone         = "+213 555 123 456",
            phEmail         = "name@company.com",
            phPassword      = "••••••••",
            phConfirm       = "••••••••",
            adminTitle      = "Primary Administrator",
            adminBody       = "This account will have full administrative access to manage the organization's profile.",
            identity        = "IDENTITY VERIFICATION REQUIRED",
            continueBtn     = "Continue to Step 3  →",
            backLabel       = "← Back to Organization Details",
            footer          = "© 2024 AL-MIZAN SOVEREIGN PLATFORM",
            weak            = "WEAK",
            medium          = "MEDIUM",
            strong          = "STRONG",
            passwordMatch   = "Passwords match",
            passwordNoMatch = "Passwords do not match"
        )
    }
}

// ─────────────────────────────────────────────
//  PASSWORD STRENGTH HELPER
// ─────────────────────────────────────────────
private fun passwordStrength(password: String): Int {
    if (password.isEmpty()) return 0
    var score = 0
    if (password.length >= 8) score++
    if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++
    return score
}

// ─────────────────────────────────────────────
//  SHARED HEADER SHELL
// ─────────────────────────────────────────────
@Composable
private fun RegHeader(
    onBackClick   : () -> Unit,
    onActionClick : () -> Unit = {},
    actionIcon    : @Composable () -> Unit = {
        Icon(Icons.Outlined.Info, contentDescription = "Info", tint = White3, modifier = Modifier.size(24.dp))
    }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = White3, modifier = Modifier.size(24.dp))
        }
        Text("Registration", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = White3)
        IconButton(onClick = onActionClick) { actionIcon() }
    }
}

// ─────────────────────────────────────────────
//  REGISTRATION STEP 2
// ─────────────────────────────────────────────
@Composable
fun RegistrationStep2Screen(
    onContinueClick : (String, String, String) -> Unit = { _, _, _ -> },
    onBackClick     : () -> Unit = {},
    onInfoClick     : () -> Unit = {},
    selectedLang    : AppLanguage = AppLanguage.FRENCH,
    onLanguageChange: (AppLanguage) -> Unit = {}
) {
    var phone            by remember { mutableStateOf("") }
    var email            by remember { mutableStateOf("") }
    var password         by remember { mutableStateOf("") }
    var confirmPassword  by remember { mutableStateOf("") }
    var passwordVisible  by remember { mutableStateOf(false) }
    var confirmVisible   by remember { mutableStateOf(false) }
    val phoneValid = phone.isEmpty() || isValidAlgerianPhone(phone)
    val emailValid = email.isEmpty() || isValidEmail(email)

    val passwordsMatch = password.isNotEmpty() && password == confirmPassword

    val canContinue =
        phone.isNotBlank() &&
                email.isNotBlank() &&
                phoneValid &&
                emailValid &&
                password.length >= 6 &&
                passwordsMatch

    val strings = when (selectedLang) {
        AppLanguage.FRENCH  -> Step2Strings.french
        AppLanguage.ARABIC  -> Step2Strings.arabic
        AppLanguage.ENGLISH -> Step2Strings.english
    }

    val strength      = passwordStrength(password)
    val strengthLabel = when (strength) { 1 -> strings.weak; 2 -> strings.medium; 3 -> strings.strong; else -> "" }
    val strengthColor = when (strength) { 1 -> RedWeak; 2 -> Color(0xFFFFA726); 3 -> GreenAccent3; else -> Color.Transparent }

    val screenWidth   = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth     = if (screenWidth < 500.dp) screenWidth * 0.90f else 420.dp
    val overlapAmount = 32.dp

    Column(modifier = Modifier.fillMaxSize().background(PageBg3)) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().background(DarkHeader3)
                    .statusBarsPadding().padding(bottom = overlapAmount + 16.dp)
            ) {
                RegHeader(onBackClick = onBackClick, onActionClick = onInfoClick)
            }

            Card(
                modifier  = Modifier.width(cardWidth).offset(y = -overlapAmount).zIndex(1f),
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = White3),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(strings.stepLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenAccent3, letterSpacing = 1.sp)
                        Text("66%", fontSize = 11.sp, color = TextLight3, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFFE0E0E0))) {
                        Box(modifier = Modifier.fillMaxWidth(0.66f).fillMaxHeight().clip(RoundedCornerShape(3.dp)).background(GreenAccent3))
                    }
                }
            }

            Spacer(Modifier.height((-overlapAmount.value + 8).dp))

            Column(modifier = Modifier.width(cardWidth)) {
                Text(strings.sectionTitle, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark3)
                Spacer(Modifier.height(4.dp))
                Text(strings.sectionSub, fontSize = 13.sp, color = GreenAccent3)
                Spacer(Modifier.height(24.dp))

                // phone
                Step2FieldLabel(strings.phone)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it },
                    placeholder = { Text(strings.phPhone, color = TextLight3, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Phone, null, tint = TextLight3, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(8.dp), colors = step2FieldColors())

                Spacer(Modifier.height(16.dp))

                // email
                Step2FieldLabel(strings.email)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(value = email, onValueChange = { email = it },
                    placeholder = { Text(strings.phEmail, color = TextLight3, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Email, null, tint = TextLight3, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(8.dp), colors = step2FieldColors())

                Spacer(Modifier.height(16.dp))

                // password
                Step2FieldLabel(strings.password)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    placeholder = { Text(strings.phPassword, color = TextLight3, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = TextLight3, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        TextButton(onClick = { passwordVisible = !passwordVisible }, contentPadding = PaddingValues(horizontal = 10.dp)) {
                            Text(if (passwordVisible) "Masquer" else "Afficher", fontSize = 11.sp, color = GreenAccent3, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    shape = RoundedCornerShape(8.dp), colors = step2FieldColors()
                )

                // strength bar
                if (password.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        repeat(3) { i ->
                            Box(modifier = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp))
                                .background(if (i < strength) strengthColor else Color(0xFFE0E0E0)))
                        }
                        Text(strengthLabel, fontSize = 10.sp, color = strengthColor, fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(48.dp), textAlign = TextAlign.End)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // confirm password
                Step2FieldLabel(strings.confirmPassword)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = confirmPassword, onValueChange = { confirmPassword = it },
                    placeholder = { Text(strings.phConfirm, color = TextLight3, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = TextLight3, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        TextButton(onClick = { confirmVisible = !confirmVisible }, contentPadding = PaddingValues(horizontal = 10.dp)) {
                            Text(if (confirmVisible) "Masquer" else "Afficher", fontSize = 11.sp, color = GreenAccent3, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError  = confirmPassword.isNotEmpty() && !passwordsMatch,
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    shape = RoundedCornerShape(8.dp), colors = step2FieldColors()
                )

                // match indicator
                if (confirmPassword.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (passwordsMatch) Icons.Outlined.Check else Icons.Outlined.Close,
                            contentDescription = null,
                            tint = if (passwordsMatch) GreenAccent3 else RedWeak,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (passwordsMatch) strings.passwordMatch else strings.passwordNoMatch,
                            fontSize = 11.sp,
                            color = if (passwordsMatch) GreenAccent3 else RedWeak
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // admin info box
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(BlueLight).padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Outlined.Lock, null, tint = BlueAccent, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(strings.adminTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark3)
                        Spacer(Modifier.height(3.dp))
                        Text(strings.adminBody, fontSize = 12.sp, color = TextMid3, lineHeight = 17.sp)
                    }
                }

                Spacer(Modifier.height(24.dp))
                LangSelector(selectedLang = selectedLang, onLanguageChange = onLanguageChange)
                Spacer(Modifier.height(24.dp))
            }
        }

        Column(modifier = Modifier.fillMaxWidth().background(White3).padding(horizontal = 16.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Icon(Icons.Outlined.CheckCircle, null, tint = GreenAccent3, modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(5.dp))
                Text(strings.identity, fontSize = 9.sp, color = TextLight3, letterSpacing = 1.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(10.dp))
            Button(onClick = { onContinueClick(phone, email, password) }, enabled = canContinue,
                modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenAccent3, disabledContainerColor = Color(0xFFB0CDB9))
            ) {
                Text(strings.continueBtn, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = White3)
            }
            Spacer(Modifier.height(10.dp))
            Text(strings.backLabel, fontSize = 13.sp, color = TextMid3, modifier = Modifier.clickable { onBackClick() })
            Spacer(Modifier.height(8.dp))
            Text(strings.footer, fontSize = 9.sp, color = TextLight3, letterSpacing = 0.5.sp, textAlign = TextAlign.Center)
        }
    }
}
// ─────────────────────────────────────────────
//  HELPERS
// ─────────────────────────────────────────────
@Composable
private fun Step2FieldLabel(text: String) {
    Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LabelGrey3, letterSpacing = 0.8.sp)
}

@Composable
private fun step2FieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = GreenAccent3,
    unfocusedBorderColor    = BorderGrey3,
    focusedTextColor        = TextDark3,
    unfocusedTextColor      = TextDark3,
    cursorColor             = GreenAccent3,
    focusedContainerColor   = FieldBg3,
    unfocusedContainerColor = FieldBg3
)

@Composable
private fun LangSelector(
    selectedLang    : AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        AppLanguage.entries.forEachIndexed { index, lang ->
            if (index > 0) Text("  •  ", fontSize = 11.sp, color = TextLight3)
            Text(
                text       = lang.label,
                fontSize   = 11.sp,
                color      = if (selectedLang == lang) GreenAccent3 else TextLight3,
                fontWeight = if (selectedLang == lang) FontWeight.Bold else FontWeight.Normal,
                modifier   = Modifier.clickable { onLanguageChange(lang) }
            )
        }
    }
}

// ─────────────────────────────────────────────
//  VALIDATION HELPERS
// ─────────────────────────────────────────────

private fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    return emailRegex.matches(email)
}

private fun isValidAlgerianPhone(phone: String): Boolean {
    val phoneRegex = Regex("^(\\+213|0)(5|6|7)[0-9]{8}$")
    return phoneRegex.matches(phone)
}