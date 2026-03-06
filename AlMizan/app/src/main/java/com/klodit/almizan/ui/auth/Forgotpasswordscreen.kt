package com.klodit.almizan.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.klodit.almizan.R

// ─────────────────────────────────────────────
//  COLORS
// ─────────────────────────────────────────────
private val FpDarkHeader  = Color(0xFF364150)
private val FpGreenAccent = Color(0xFF4CAE4F)
private val FpWhite       = Color.White
private val FpBorderGrey  = Color(0xFFDDE3E8)
private val FpTextDark    = Color(0xFF1A2B38)
private val FpTextMid     = Color(0xFF4A6070)
private val FpTextLight   = Color(0xFF8FA3B0)
private val FpFieldBg     = Color(0xFFF8FAFB)
private val FpPageBg      = Color(0xFFECEFF1)
private val FpIconBg      = Color(0xFFE3F0FD)
private val FpIconTint    = Color(0xFF1E88E5)

// ─────────────────────────────────────────────
//  STRINGS
// ─────────────────────────────────────────────
private data class FpStrings(
    val title       : String,
    val subtitle    : String,
    val emailLabel  : String,
    val emailPh     : String,
    val sendBtn     : String,
    val remembered  : String,
    val signIn      : String,
    val footer      : String
) {
    companion object {
        val french = FpStrings(
            title      = "Réinitialiser le mot de passe",
            subtitle   = "Entrez votre adresse e-mail enregistrée et nous vous enverrons les instructions pour réinitialiser votre mot de passe en toute sécurité.",
            emailLabel = "Adresse e-mail",
            emailPh    = "nom.prenom@domain.dz",
            sendBtn    = "Envoyer le code de réinitialisation",
            remembered = "Vous vous souvenez de votre mot de passe ?",
            signIn     = "Se connecter ici",
            footer     = "MINISTÈRE DES FINANCES"
        )
        val arabic = FpStrings(
            title      = "إعادة تعيين كلمة المرور",
            subtitle   = "أدخل عنوان بريدك الإلكتروني المسجل وسنرسل لك تعليمات لإعادة التعيين بأمان.",
            emailLabel = "البريد الإلكتروني",
            emailPh    = "nom.prenom@domain.dz",
            sendBtn    = "إرسال رمز إعادة التعيين",
            remembered = "هل تتذكر كلمة مرورك؟",
            signIn     = "تسجيل الدخول هنا",
            footer     = "وزارة المالية"
        )
        val english = FpStrings(
            title      = "Reset Password",
            subtitle   = "Enter your registered email address and we will send you instructions to reset your password securely.",
            emailLabel = "Email Address",
            emailPh    = "nom.prenom@domain.dz",
            sendBtn    = "Send Reset Code",
            remembered = "Remembered your password?",
            signIn     = "Sign in here",
            footer     = "MINISTÈRE DES FINANCES"
        )
    }
}

// ─────────────────────────────────────────────
//  EMAIL VALIDATOR
// ─────────────────────────────────────────────
private fun isValidEmail(email: String): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

// ─────────────────────────────────────────────
//  FORGOT PASSWORD SCREEN
// ─────────────────────────────────────────────
@Composable
fun ForgotPasswordScreen(
    onSendClick      : (email: String) -> Unit = {},
    onBackClick      : () -> Unit = {},
    onSignInClick    : () -> Unit = {},
    selectedLang     : AppLanguage = AppLanguage.FRENCH,
    onLanguageChange : (AppLanguage) -> Unit = {}
) {
    var email        by remember { mutableStateOf("") }
    var emailTouched by remember { mutableStateOf(false) }

    val emailValid  = isValidEmail(email)
    val showError   = emailTouched && email.isNotEmpty() && !emailValid
    val canSend     = emailValid

    val strings = when (selectedLang) {
        AppLanguage.FRENCH  -> FpStrings.french
        AppLanguage.ARABIC  -> FpStrings.arabic
        AppLanguage.ENGLISH -> FpStrings.english
    }

    val screenWidth   = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth     = if (screenWidth < 500.dp) screenWidth * 0.90f else 420.dp
    val overlapAmount = 32.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FpPageBg)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── dark header ──────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(FpDarkHeader)
                .statusBarsPadding()
                .padding(top = 8.dp, bottom = overlapAmount + 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FpWhite, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(8.dp))
                Image(
                    painter            = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier           = Modifier.size(44.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("AL-MIZAN", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = FpWhite, letterSpacing = 1.sp)
                    Text("SOVEREIGN PROCUREMENT", fontSize = 9.sp, color = FpGreenAccent, letterSpacing = 1.sp)
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
            colors    = CardDefaults.cardColors(containerColor = FpWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier            = Modifier.fillMaxWidth().padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // shield icon circle
                Box(
                    modifier         = Modifier.size(64.dp).clip(CircleShape).background(FpIconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = FpIconTint, modifier = Modifier.size(32.dp))
                }

                Spacer(Modifier.height(20.dp))

                Text(strings.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = FpTextDark, textAlign = TextAlign.Center)
                Spacer(Modifier.height(10.dp))
                Text(strings.subtitle, fontSize = 13.sp, color = FpTextMid, textAlign = TextAlign.Center, lineHeight = 19.sp)

                Spacer(Modifier.height(28.dp))

                // email field
                Text(strings.emailLabel, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = FpTextDark, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value         = email,
                    onValueChange = { email = it; emailTouched = true },
                    placeholder   = { Text(strings.emailPh, color = FpTextLight, fontSize = 13.sp) },
                    leadingIcon   = { Icon(Icons.Outlined.Email, null, tint = FpTextLight, modifier = Modifier.size(18.dp)) },
                    isError       = showError,
                    supportingText = if (showError) {
                        { Text("Format d'email invalide", color = Color(0xFFE53935), fontSize = 11.sp) }
                    } else null,
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    shape         = RoundedCornerShape(8.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = if (showError) Color(0xFFE53935) else FpGreenAccent,
                        unfocusedBorderColor    = if (showError) Color(0xFFE53935) else FpBorderGrey,
                        focusedTextColor        = FpTextDark,
                        unfocusedTextColor      = FpTextDark,
                        cursorColor             = FpGreenAccent,
                        focusedContainerColor   = FpFieldBg,
                        unfocusedContainerColor = FpFieldBg,
                        errorBorderColor        = Color(0xFFE53935),
                        errorContainerColor     = FpFieldBg
                    )
                )

                Spacer(Modifier.height(24.dp))

                // send button
                Button(
                    onClick  = { onSendClick(email) },
                    enabled  = canSend,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = FpGreenAccent,
                        disabledContainerColor = Color(0xFFB0CDB9)
                    )
                ) {
                    Icon(Icons.Outlined.Email, null, tint = FpWhite, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(strings.sendBtn, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FpWhite)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // sign in link
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(strings.remembered, fontSize = 13.sp, color = FpTextMid)
            Spacer(Modifier.width(4.dp))
            Text(strings.signIn, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FpTextDark, modifier = Modifier.clickable { onSignInClick() })
        }

        Spacer(Modifier.height(16.dp))

        // language selector
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            AppLanguage.entries.forEachIndexed { index, lang ->
                if (index > 0) Text("  •  ", fontSize = 11.sp, color = FpTextLight)
                Text(
                    text       = lang.label,
                    fontSize   = 11.sp,
                    color      = if (selectedLang == lang) FpGreenAccent else FpTextLight,
                    fontWeight = if (selectedLang == lang) FontWeight.Bold else FontWeight.Normal,
                    modifier   = Modifier.clickable { onLanguageChange(lang) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(strings.footer, fontSize = 9.sp, color = FpTextLight, letterSpacing = 1.5.sp, textAlign = TextAlign.Center)

        Spacer(Modifier.height(32.dp))
    }
}