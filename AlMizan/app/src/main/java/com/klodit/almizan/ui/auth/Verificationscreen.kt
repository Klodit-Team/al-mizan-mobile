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
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.klodit.almizan.R

// ─────────────────────────────────────────────
//  COLORS
// ─────────────────────────────────────────────
private val DarkHeader  = Color(0xFF364150)
private val GreenAccent = Color(0xFF4CAE4F)
private val White       = Color.White
private val BorderGrey  = Color(0xFFDDE3E8)
private val TextDark    = Color(0xFF1A2B38)
private val TextMid     = Color(0xFF4A6070)
private val TextLight   = Color(0xFF8FA3B0)
private val PageBg      = Color(0xFFECEFF1)
private val IconBg      = Color(0xFFEDF0F2)

// ─────────────────────────────────────────────
//  STRINGS PER LANGUAGE  (private — never
//  passed directly to a public function)
// ─────────────────────────────────────────────
private data class VerifStrings(
    val title        : String,
    val subtitle     : String,
    val expiresLabel : String,
    val verifyBtn    : String,
    val noCode       : String,
    val resend       : String,
    val footerLine1  : String,
    val footerLine2  : String,
    val logout       : String,
    val notifications: String,
    val noNotifs     : String
) {
    companion object {
        val french = VerifStrings(
            title         = "Vérification Sécurisée",
            subtitle      = "Veuillez entrer le code TOTP à 6 chiffres de votre application d'authentification pour accéder à votre tableau de bord.",
            expiresLabel  = "Code expire dans  ",
            verifyBtn     = "Vérifier & Accéder",
            noCode        = "Vous n'avez pas reçu de code ?",
            resend        = "Renvoyer le code",
            footerLine1   = "© 2024 Al-Mizan B2B Procurement. Tous droits réservés.",
            footerLine2   = "Session sécurisée ID : AMZ-882-X9",
            logout        = "Se déconnecter",
            notifications = "Notifications",
            noNotifs      = "Aucune notification"
        )
        val arabic = VerifStrings(
            title         = "التحقق الآمن",
            subtitle      = "يرجى إدخال رمز TOTP المكون من 6 أرقام من تطبيق المصادقة للوصول إلى لوحة التحكم.",
            expiresLabel  = "ينتهي الرمز خلال  ",
            verifyBtn     = "تحقق والدخول",
            noCode        = "لم تتلقَّ الرمز؟",
            resend        = "إعادة إرسال الرمز",
            footerLine1   = "© 2024 Al-Mizan B2B. جميع الحقوق محفوظة.",
            footerLine2   = "معرف الجلسة الآمنة: AMZ-882-X9",
            logout        = "تسجيل الخروج",
            notifications = "الإشعارات",
            noNotifs      = "لا توجد إشعارات"
        )
        val english = VerifStrings(
            title         = "Secure Verification",
            subtitle      = "Please enter the 6-digit TOTP code from your authentication app to access your dashboard.",
            expiresLabel  = "Code expires in  ",
            verifyBtn     = "Verify & Access",
            noCode        = "Didn't receive a code?",
            resend        = "Resend Code",
            footerLine1   = "© 2024 Al-Mizan B2B Procurement. All rights reserved.",
            footerLine2   = "Secure Session ID: AMZ-882-X9",
            logout        = "Sign out",
            notifications = "Notifications",
            noNotifs      = "No notifications"
        )
    }
}

// ─────────────────────────────────────────────
//  FAKE NOTIFICATIONS
// ─────────────────────────────────────────────
private val sampleNotifications = listOf(
    "Nouvel appel d'offres publié",
    "Votre soumission a été reçue",
    "Résultat d'évaluation disponible"
)

// ─────────────────────────────────────────────
//  TOP NAV BAR
//  Only plain types (String) as parameters
//  so there is no private-type-in-public-function error
// ─────────────────────────────────────────────
@Composable
fun AlMizanTopBar(
    onLogoutClick : () -> Unit = {},
    onNotifClick  : () -> Unit = {},
    logoutLabel   : String = "Se déconnecter",
    notifTitle    : String = "Notifications",
    noNotifsLabel : String = "Aucune notification"
) {
    var showAccountMenu by remember { mutableStateOf(false) }
    var showNotifMenu   by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkHeader)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // left: logo + title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter            = painterResource(id = R.drawable.logo),
                contentDescription = "Al-Mizan Logo",
                modifier           = Modifier.size(44.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text          = "AL-MIZAN",
                fontSize      = 18.sp,
                fontWeight    = FontWeight.Bold,
                color         = White,
                letterSpacing = 1.sp
            )
        }

        // right: notif + account
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {

            // notifications dropdown
            Box {
                IconButton(onClick = { showNotifMenu = !showNotifMenu }) {
                    Icon(
                        imageVector        = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint               = White,
                        modifier           = Modifier.size(26.dp)
                    )
                }
                DropdownMenu(
                    expanded         = showNotifMenu,
                    onDismissRequest = { showNotifMenu = false },
                    modifier         = Modifier.width(260.dp).background(White)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F7F9))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(notifTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                    }
                    HorizontalDivider(color = BorderGrey)
                    if (sampleNotifications.isEmpty()) {
                        DropdownMenuItem(
                            text    = { Text(noNotifsLabel, color = TextLight, fontSize = 13.sp) },
                            onClick = { showNotifMenu = false }
                        )
                    } else {
                        sampleNotifications.forEach { notif ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .clip(CircleShape)
                                                .background(GreenAccent)
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Text(notif, fontSize = 13.sp, color = TextDark, lineHeight = 18.sp)
                                    }
                                },
                                onClick = { onNotifClick(); showNotifMenu = false }
                            )
                            HorizontalDivider(color = BorderGrey, thickness = 0.5.dp)
                        }
                    }
                }
            }

            // account dropdown
            Box {
                IconButton(onClick = { showAccountMenu = !showAccountMenu }) {
                    Icon(
                        imageVector        = Icons.Outlined.AccountCircle,
                        contentDescription = "Account",
                        tint               = White,
                        modifier           = Modifier.size(26.dp)
                    )
                }
                DropdownMenu(
                    expanded         = showAccountMenu,
                    onDismissRequest = { showAccountMenu = false },
                    modifier         = Modifier.width(180.dp).background(White)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text       = logoutLabel,
                                fontSize   = 14.sp,
                                color      = Color(0xFFE53935),
                                fontWeight = FontWeight.Medium
                            )
                        },
                        onClick = { showAccountMenu = false; onLogoutClick() }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  VERIFICATION SCREEN
// ─────────────────────────────────────────────
@Composable
fun VerificationScreen(
    onVerifyClick    : (code: String) -> Unit = {},
    onResendClick    : () -> Unit = {},
    onLogoutClick    : () -> Unit = {},
    onNotifClick     : () -> Unit = {},
    selectedLang     : AppLanguage = AppLanguage.FRENCH,
    onLanguageChange : (AppLanguage) -> Unit = {}
) {
    val strings = when (selectedLang) {
        AppLanguage.FRENCH  -> VerifStrings.french
        AppLanguage.ARABIC  -> VerifStrings.arabic
        AppLanguage.ENGLISH -> VerifStrings.english
    }

    val digits          = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    var focusedIndex    by remember { mutableIntStateOf(0) }
    var secondsLeft     by remember { mutableIntStateOf(120) }
    var timerTrigger    by remember { mutableIntStateOf(0) }

    LaunchedEffect(timerTrigger) {
        secondsLeft = 120
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    val timerText    = "%02d:%02d".format(secondsLeft / 60, secondsLeft % 60)
    val timerExpired = secondsLeft == 0
    val fullCode     = digits.joinToString("")
    val codeComplete = digits.all { it.isNotEmpty() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // navbar — pass plain Strings, not the private VerifStrings object
        AlMizanTopBar(
            onLogoutClick = onLogoutClick,
            onNotifClick  = onNotifClick,
            logoutLabel   = strings.logout,
            notifTitle    = strings.notifications,
            noNotifsLabel = strings.noNotifs
        )

        Spacer(Modifier.height(32.dp))

        // white card
        Card(
            modifier  = Modifier.fillMaxWidth(0.90f),
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier            = Modifier.fillMaxWidth().padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // circle icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(IconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        tint               = TextMid,
                        modifier           = Modifier.size(34.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text       = strings.title,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextDark,
                    textAlign  = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text       = strings.subtitle,
                    fontSize   = 13.sp,
                    color      = TextMid,
                    textAlign  = TextAlign.Center,
                    lineHeight = 19.sp
                )

                Spacer(Modifier.height(28.dp))

                // 6 digit boxes
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
                                .background(if (isFocused) White else Color(0xFFF5F7F9))
                                .border(
                                    width = if (isFocused) 1.5.dp else 1.dp,
                                    color = if (isFocused) GreenAccent else BorderGrey,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .focusRequester(focusRequesters[index])
                                .onFocusChanged { if (it.isFocused) focusedIndex = index },
                            textStyle       = TextStyle(
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color      = TextDark,
                                textAlign  = TextAlign.Center
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            cursorBrush     = SolidColor(GreenAccent),
                            singleLine      = true,
                            decorationBox   = { inner ->
                                Box(contentAlignment = Alignment.Center) { inner() }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // timer
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint               = if (timerExpired) Color(0xFFE53935) else TextLight,
                        modifier           = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(strings.expiresLabel, fontSize = 12.sp, color = TextLight)
                    Text(
                        text       = timerText,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color      = if (timerExpired) Color(0xFFE53935) else TextDark
                    )
                }

                Spacer(Modifier.height(20.dp))

                // verify button
                Button(
                    onClick  = { onVerifyClick(fullCode) },
                    enabled  = codeComplete && !timerExpired,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(8.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = GreenAccent,
                        disabledContainerColor = Color(0xFFB0CDB9)
                    )
                ) {
                    Text(
                        text       = strings.verifyBtn,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color      = White
                    )
                }

                Spacer(Modifier.height(20.dp))

                // resend
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(strings.noCode, fontSize = 12.sp, color = TextMid)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text       = strings.resend,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TextDark,
                        modifier   = Modifier
                            .clickable {
                                timerTrigger++
                                digits.fill("")
                                focusedIndex = 0
                                focusRequesters[0].requestFocus()
                                onResendClick()
                            }
                            .padding(4.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                // language selector
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    AppLanguage.entries.forEachIndexed { index, lang ->
                        if (index > 0) Text("  •  ", fontSize = 11.sp, color = TextLight)
                        Text(
                            text       = lang.label,
                            fontSize   = 11.sp,
                            color      = if (selectedLang == lang) GreenAccent else TextLight,
                            fontWeight = if (selectedLang == lang) FontWeight.Bold
                            else FontWeight.Normal,
                            modifier   = Modifier.clickable { onLanguageChange(lang) }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // footer
        Text(strings.footerLine1, fontSize = 10.sp, color = TextLight, textAlign = TextAlign.Center)
        Spacer(Modifier.height(4.dp))
        Text(strings.footerLine2, fontSize = 10.sp, color = TextLight, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
    }

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }
}