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
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Info
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
import kotlinx.coroutines.delay
import com.klodit.almizan.R

// ─────────────────────────────────────────────
//  COLORS
// ─────────────────────────────────────────────
private val AlDarkHeader  = Color(0xFF364150)
private val AlGreenAccent = Color(0xFF4CAE4F)
private val AlWhite       = Color.White
private val AlTextDark    = Color(0xFF1A2B38)
private val AlTextMid     = Color(0xFF4A6070)
private val AlTextLight   = Color(0xFF8FA3B0)
private val AlPageBg      = Color(0xFFECEFF1)
private val AlRedIcon     = Color(0xFFE53935)
private val AlRedIconBg   = Color(0xFFFFEBEE)
private val AlRedNoticeBg = Color(0xFFFFF0F0)
private val AlRedNotice   = Color(0xFFE53935)
private val AlDarkBtn     = Color(0xFF1A2B38)

// ─────────────────────────────────────────────
//  STRINGS
// ─────────────────────────────────────────────
private data class AlStrings(
    val title          : String,
    val subtitle       : String,
    val tryAgain       : String,
    val resetBtn       : String,
    val contactSupport : String,
    val footer         : String
) {
    companion object {
        val french = AlStrings(
            title          = "Compte Verrouillé",
            subtitle       = "Pour votre sécurité, votre compte a été temporairement verrouillé en raison de plusieurs tentatives de connexion infructueuses.",
            tryAgain       = "Veuillez réessayer dans",
            resetBtn       = "Réinitialiser le mot de passe par e-mail",
            contactSupport = "Contacter le support",
            footer         = "MINISTÈRE DES FINANCES"
        )
        val arabic = AlStrings(
            title          = "الحساب مقفل",
            subtitle       = "لأمانك، تم قفل حسابك مؤقتًا بسبب محاولات تسجيل دخول فاشلة متعددة.",
            tryAgain       = "يرجى المحاولة مرة أخرى خلال",
            resetBtn       = "إعادة تعيين كلمة المرور عبر البريد",
            contactSupport = "التواصل مع الدعم",
            footer         = "وزارة المالية"
        )
        val english = AlStrings(
            title          = "Account Locked",
            subtitle       = "For your security, your account has been temporarily locked due to multiple unsuccessful login attempts.",
            tryAgain       = "Please try again in",
            resetBtn       = "Reset Password via Email",
            contactSupport = "Contact Support",
            footer         = "MINISTÈRE DES FINANCES"
        )
    }
}

// ─────────────────────────────────────────────
//  ACCOUNT LOCKED SCREEN
// ─────────────────────────────────────────────
@Composable
fun AccountLockedScreen(
    lockDurationSeconds : Int = 900,              // 15 minutes default
    onResetPasswordClick: () -> Unit = {},
    onContactSupport    : () -> Unit = {},
    selectedLang        : AppLanguage = AppLanguage.FRENCH,
    onLanguageChange    : (AppLanguage) -> Unit = {}
) {
    val strings = when (selectedLang) {
        AppLanguage.FRENCH  -> AlStrings.french
        AppLanguage.ARABIC  -> AlStrings.arabic
        AppLanguage.ENGLISH -> AlStrings.english
    }

    // countdown timer
    var secondsLeft by remember { mutableIntStateOf(lockDurationSeconds) }
    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }
    val timerText = "%02d:%02d".format(secondsLeft / 60, secondsLeft % 60)

    val screenWidth   = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth     = if (screenWidth < 500.dp) screenWidth * 0.90f else 420.dp
    val overlapAmount = 32.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlPageBg)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── dark header ──────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AlDarkHeader)
                .statusBarsPadding()
                .padding(top = 16.dp, bottom = overlapAmount + 24.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier              = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter            = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier           = Modifier.size(44.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("AL-MIZAN", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = AlWhite, letterSpacing = 1.sp)
                    Text("SOVEREIGN PROCUREMENT", fontSize = 9.sp, color = AlGreenAccent, letterSpacing = 1.sp)
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
            colors    = CardDefaults.cardColors(containerColor = AlWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier            = Modifier.fillMaxWidth().padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // red lock icon circle
                Box(
                    modifier         = Modifier.size(72.dp).clip(CircleShape).background(AlRedIconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint               = AlRedIcon,
                        modifier           = Modifier.size(36.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text       = strings.title,
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color      = AlTextDark,
                    textAlign  = TextAlign.Center
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text       = strings.subtitle,
                    fontSize   = 13.sp,
                    color      = AlTextMid,
                    textAlign  = TextAlign.Center,
                    lineHeight = 19.sp
                )

                Spacer(Modifier.height(24.dp))

                // countdown notice box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AlRedNoticeBg)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.Info,
                        contentDescription = null,
                        tint               = AlRedNotice,
                        modifier           = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text       = "${strings.tryAgain}  $timerText",
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color      = AlRedNotice
                    )
                }

                Spacer(Modifier.height(20.dp))

                // reset password button — dark
                Button(
                    onClick  = onResetPasswordClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = AlDarkBtn)
                ) {
                    Icon(Icons.Outlined.Email, null, tint = AlWhite, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(strings.resetBtn, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AlWhite)
                }

                Spacer(Modifier.height(14.dp))

                // contact support — text link
                Row(
                    modifier              = Modifier.clickable { onContactSupport() },
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Outlined.Info, null, tint = AlTextMid, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text       = strings.contactSupport,
                        fontSize   = 14.sp,
                        color      = AlTextDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // language selector
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            AppLanguage.entries.forEachIndexed { index, lang ->
                if (index > 0) Text("  •  ", fontSize = 11.sp, color = AlTextLight)
                Text(
                    text       = lang.label,
                    fontSize   = 11.sp,
                    color      = if (selectedLang == lang) AlGreenAccent else AlTextLight,
                    fontWeight = if (selectedLang == lang) FontWeight.Bold else FontWeight.Normal,
                    modifier   = Modifier.clickable { onLanguageChange(lang) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(strings.footer, fontSize = 9.sp, color = AlTextLight, letterSpacing = 1.5.sp, textAlign = TextAlign.Center)

        Spacer(Modifier.height(32.dp))
    }
}