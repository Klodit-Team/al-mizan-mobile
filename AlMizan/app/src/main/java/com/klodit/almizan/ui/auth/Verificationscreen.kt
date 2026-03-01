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
//  FAKE NOTIFICATIONS  (replace with real data later)
// ─────────────────────────────────────────────
private val sampleNotifications = listOf(
    "Nouvel appel d'offres publié",
    "Votre soumission a été reçue",
    "Résultat d'évaluation disponible"
)

// ─────────────────────────────────────────────
//  TOP NAV BAR  (extracted as its own composable
//               so it can be reused on every screen)
// ─────────────────────────────────────────────
@Composable
fun AlMizanTopBar(
    onLogoutClick : () -> Unit = {},
    onNotifClick  : () -> Unit = {}
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

        // ── LEFT: logo + title ───────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter            = painterResource(id = R.drawable.logo),
                contentDescription = "Al-Mizan Logo",
                modifier           = Modifier.size(44.dp)          // bigger logo
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text       = "AL-MIZAN",
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = White,
                letterSpacing = 1.sp
            )
        }

        // ── RIGHT: notification + account ────
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {

            // ── NOTIFICATION BUTTON + DROPDOWN ──
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
                    modifier         = Modifier
                        .width(260.dp)
                        .background(White)
                ) {
                    // header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F7F9))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text       = "Notifications",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 14.sp,
                            color      = TextDark
                        )
                    }

                    HorizontalDivider(color = BorderGrey)

                    if (sampleNotifications.isEmpty()) {
                        DropdownMenuItem(
                            text    = { Text("Aucune notification", color = TextLight, fontSize = 13.sp) },
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
                                        Text(
                                            text      = notif,
                                            fontSize  = 13.sp,
                                            color     = TextDark,
                                            lineHeight = 18.sp
                                        )
                                    }
                                },
                                onClick = {
                                    onNotifClick()
                                    showNotifMenu = false
                                }
                            )
                            HorizontalDivider(color = BorderGrey, thickness = 0.5.dp)
                        }
                    }
                }
            }

            // ── ACCOUNT BUTTON + DROPDOWN ───────
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
                    modifier         = Modifier
                        .width(180.dp)
                        .background(White)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text       = "Se déconnecter",
                                fontSize   = 14.sp,
                                color      = Color(0xFFE53935),   // red for logout
                                fontWeight = FontWeight.Medium
                            )
                        },
                        onClick = {
                            showAccountMenu = false
                            onLogoutClick()
                        }
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
    onVerifyClick  : (code: String) -> Unit = {},
    onResendClick  : () -> Unit = {},
    onLogoutClick  : () -> Unit = {},
    onNotifClick   : () -> Unit = {}
) {
    val digits           = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters  = remember { List(6) { FocusRequester() } }
    var focusedIndex     by remember { mutableIntStateOf(0) }

    var secondsLeft by remember { mutableIntStateOf(120) }
    LaunchedEffect(Unit) {
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

        // ── navbar ───────────────────────────
        AlMizanTopBar(
            onLogoutClick = onLogoutClick,
            onNotifClick  = onNotifClick
        )

        Spacer(Modifier.height(32.dp))

        // ── white card ───────────────────────
        Card(
            modifier  = Modifier.fillMaxWidth(0.90f),
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // top circle icon
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
                    text       = "Secure Verification",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextDark,
                    textAlign  = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text       = "Please enter the 6-digit TOTP code from your authentication app to access your dashboard.",
                    fontSize   = 13.sp,
                    color      = TextMid,
                    textAlign  = TextAlign.Center,
                    lineHeight = 19.sp
                )

                Spacer(Modifier.height(28.dp))

                // ── 6 digit boxes ────────────────────
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
                            modifier      = Modifier
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
                            textStyle     = TextStyle(
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
                    Text("Code expires in  ", fontSize = 12.sp, color = TextLight)
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape  = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor         = GreenAccent,
                        disabledContainerColor = Color(0xFFB0CDB9)
                    )
                ) {
                    Text(
                        text       = "Verify & Access",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color      = White
                    )
                }

                Spacer(Modifier.height(20.dp))

                // resend
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Didn't receive a code?", fontSize = 12.sp, color = TextMid)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text       = "Resend Code",
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TextDark,
                        modifier   = Modifier
                            .clickable { onResendClick() }
                            .padding(4.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // footer
        Text(
            text      = "© 2024 Al-Mizan B2B Procurement. All rights reserved.",
            fontSize  = 10.sp,
            color     = TextLight,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text      = "Secure Session ID: AMZ-882-X9",
            fontSize  = 10.sp,
            color     = TextLight,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
    }

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }
}