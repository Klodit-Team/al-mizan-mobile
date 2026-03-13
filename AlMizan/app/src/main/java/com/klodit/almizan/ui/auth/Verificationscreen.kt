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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.klodit.almizan.R
import com.klodit.almizan.ui.theme.AppLanguage
import com.klodit.almizan.ui.theme.Grey50

private val sampleNotifications = listOf(
    "Nouvel appel d'offres publié",
    "Votre soumission a été reçue",
    "Résultat d'évaluation disponible"
)

// ─────────────────────────────────────────────
//  TOP NAV BAR  (shared across main screens)
// ─────────────────────────────────────────────
@Composable
fun AlMizanTopBar(
    onLogoutClick : () -> Unit = {},
    onNotifClick  : () -> Unit = {},
    logoutLabel   : String = stringResource(R.string.verif_logout),
    notifTitle    : String = stringResource(R.string.verif_notifications),
    noNotifsLabel : String = stringResource(R.string.verif_no_notifs)
) {
    val cs = MaterialTheme.colorScheme
    var showAccountMenu by remember { mutableStateOf(false) }
    var showNotifMenu   by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(cs.primary)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.logo), "Al-Mizan Logo",
                modifier = Modifier.size(44.dp))
            Spacer(Modifier.width(10.dp))
            Text("AL-MIZAN", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                color = cs.onPrimary, letterSpacing = 1.sp)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically) {

            // notifications
            Box {
                IconButton(onClick = { showNotifMenu = !showNotifMenu }) {
                    Icon(Icons.Outlined.Notifications, "Notifications",
                        tint = cs.onPrimary, modifier = Modifier.size(26.dp))
                }
                DropdownMenu(expanded = showNotifMenu,
                    onDismissRequest = { showNotifMenu = false },
                    modifier = Modifier.width(260.dp).background(cs.surface)) {
                    Box(modifier = Modifier.fillMaxWidth().background(Grey50)
                        .padding(horizontal = 16.dp, vertical = 10.dp)) {
                        Text(notifTitle, fontWeight = FontWeight.Bold,
                            fontSize = 14.sp, color = cs.onSurface)
                    }
                    HorizontalDivider(color = cs.outline)
                    if (sampleNotifications.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text(noNotifsLabel, color = cs.onSurfaceVariant, fontSize = 13.sp) },
                            onClick = { showNotifMenu = false })
                    } else {
                        sampleNotifications.forEach { notif ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(7.dp).clip(CircleShape)
                                            .background(cs.secondary))
                                        Spacer(Modifier.width(10.dp))
                                        Text(notif, fontSize = 13.sp, color = cs.onSurface,
                                            lineHeight = 18.sp)
                                    }
                                },
                                onClick = { onNotifClick(); showNotifMenu = false })
                            HorizontalDivider(color = cs.outline, thickness = 0.5.dp)
                        }
                    }
                }
            }

            // account
            Box {
                IconButton(onClick = { showAccountMenu = !showAccountMenu }) {
                    Icon(Icons.Outlined.AccountCircle, "Account",
                        tint = cs.onPrimary, modifier = Modifier.size(26.dp))
                }
                DropdownMenu(expanded = showAccountMenu,
                    onDismissRequest = { showAccountMenu = false },
                    modifier = Modifier.width(180.dp).background(cs.surface)) {
                    DropdownMenuItem(
                        text = { Text(logoutLabel, fontSize = 14.sp,
                            color = cs.error, fontWeight = FontWeight.Medium) },
                        onClick = { showAccountMenu = false; onLogoutClick() })
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
    onVerifyClick   : (code: String) -> Unit = {},
    onResendClick   : () -> Unit = {},
    onLogoutClick   : () -> Unit = {},
    onNotifClick    : () -> Unit = {},
    selectedLang    : AppLanguage = AppLanguage.FRENCH,
    onLanguageChange: (AppLanguage) -> Unit = {}
) {
    val cs = MaterialTheme.colorScheme

    val digits          = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    var focusedIndex    by remember { mutableIntStateOf(0) }
    var secondsLeft     by remember { mutableIntStateOf(120) }
    var timerTrigger    by remember { mutableIntStateOf(0) }

    LaunchedEffect(timerTrigger) {
        secondsLeft = 120
        while (secondsLeft > 0) { delay(1000); secondsLeft-- }
    }

    val timerText    = "%02d:%02d".format(secondsLeft / 60, secondsLeft % 60)
    val timerExpired = secondsLeft == 0
    val fullCode     = digits.joinToString("")
    val codeComplete = digits.all { it.isNotEmpty() }

    Column(
        modifier = Modifier.fillMaxSize().background(cs.background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlMizanTopBar(
            onLogoutClick = onLogoutClick,
            onNotifClick  = onNotifClick,
            logoutLabel   = stringResource(R.string.verif_logout),
            notifTitle    = stringResource(R.string.verif_notifications),
            noNotifsLabel = stringResource(R.string.verif_no_notifs)
        )

        Spacer(Modifier.height(32.dp))

        Card(
            modifier  = Modifier.fillMaxWidth(0.90f),
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = cs.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {

                Box(modifier = Modifier.size(64.dp).clip(CircleShape)
                    .background(cs.surfaceVariant),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.AccountCircle, null,
                        tint = cs.onSurfaceVariant, modifier = Modifier.size(34.dp))
                }

                Spacer(Modifier.height(20.dp))
                Text(stringResource(R.string.verif_title),
                    fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = cs.onSurface, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.verif_subtitle),
                    fontSize = 13.sp, color = cs.onSurfaceVariant,
                    textAlign = TextAlign.Center, lineHeight = 19.sp)

                Spacer(Modifier.height(28.dp))

                // 6 digit boxes
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            modifier = Modifier
                                .weight(1f).aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isFocused) cs.surface else cs.surfaceVariant)
                                .border(
                                    width = if (isFocused) 1.5.dp else 1.dp,
                                    color = if (isFocused) cs.secondary else cs.outline,
                                    shape = RoundedCornerShape(8.dp))
                                .focusRequester(focusRequesters[index])
                                .onFocusChanged { if (it.isFocused) focusedIndex = index },
                            textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold,
                                color = cs.onSurface, textAlign = TextAlign.Center),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            cursorBrush     = SolidColor(cs.secondary),
                            singleLine      = true,
                            decorationBox   = { inner ->
                                Box(contentAlignment = Alignment.Center) { inner() }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Outlined.Notifications, null,
                        tint = if (timerExpired) cs.error else cs.onSurfaceVariant,
                        modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(5.dp))
                    Text(stringResource(R.string.verif_expires_label),
                        fontSize = 12.sp, color = cs.onSurfaceVariant)
                    Text(timerText, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        color = if (timerExpired) cs.error else cs.onSurface)
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick  = { onVerifyClick(fullCode) },
                    enabled  = codeComplete && !timerExpired,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(8.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = cs.secondary,
                        disabledContainerColor = cs.secondaryContainer)
                ) {
                    Text(stringResource(R.string.verif_verify_btn),
                        fontSize = 15.sp, fontWeight = FontWeight.Bold,
                        color = cs.onSecondary)
                }

                Spacer(Modifier.height(20.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.verif_no_code),
                        fontSize = 12.sp, color = cs.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(R.string.verif_resend),
                        fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        color = cs.onSurface,
                        modifier = Modifier.clickable {
                            timerTrigger++
                            digits.fill("")
                            focusedIndex = 0
                            focusRequesters[0].requestFocus()
                            onResendClick()
                        }.padding(4.dp))
                }

                Spacer(Modifier.height(24.dp))

                LanguageSwitcher(selectedLang, onLanguageChange)
            }
        }

        Spacer(Modifier.height(32.dp))
        Text(stringResource(R.string.verif_footer_1),
            fontSize = 10.sp, color = cs.onSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(Modifier.height(4.dp))
        Text(stringResource(R.string.verif_footer_2),
            fontSize = 10.sp, color = cs.onSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
    }

    LaunchedEffect(Unit) { focusRequesters[0].requestFocus() }
}