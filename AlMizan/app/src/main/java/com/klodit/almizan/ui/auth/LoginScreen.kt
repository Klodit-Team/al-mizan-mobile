package com.klodit.almizan.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.klodit.almizan.R
import com.klodit.almizan.ui.theme.AppLanguage
import com.klodit.almizan.util.LocaleHelper

@Composable
fun LoginScreen(
    onLoginClick         : (email: String, password: String) -> Unit = { _, _ -> },
    onForgotPasswordClick: () -> Unit = {},
    onRegisterClick      : () -> Unit = {},
    onBiometricsClick    : () -> Unit = {},
    selectedLang         : AppLanguage = AppLanguage.FRENCH,
    onLanguageChange     : (AppLanguage) -> Unit = {}
) {
    var email          by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe     by remember { mutableStateOf(false) }
    var emailTouched   by remember { mutableStateOf(false) }

    val emailValid  = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val showEmailErr = emailTouched && email.isNotEmpty() && !emailValid
    val canLogin    = email.isNotBlank() && password.isNotBlank() && emailValid

    val cs = MaterialTheme.colorScheme
    val screenWidth   = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth     = if (screenWidth < 500.dp) screenWidth * 0.90f else 420.dp
    val overlapAmount = 32.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── header ──────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cs.primary)
                .statusBarsPadding()
                .padding(top = 24.dp, bottom = overlapAmount + 24.dp,
                    start = 24.dp, end = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                Image(painterResource(R.drawable.logo), contentDescription = "Logo",
                    modifier = Modifier.size(52.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("AL-MIZAN", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
                        color = cs.onPrimary, letterSpacing = 1.5.sp)
                    Text(stringResource(R.string.app_tagline), fontSize = 9.sp,
                        color = cs.secondary, letterSpacing = 1.sp)
                }
            }
        }

        // ── card ─────────────────────────────────
        Card(
            modifier  = Modifier.width(cardWidth).offset(y = -overlapAmount).zIndex(1f),
            shape     = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp,
                bottomStart = 16.dp, bottomEnd = 16.dp),
            colors    = CardDefaults.cardColors(containerColor = cs.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {

                Text(stringResource(R.string.login_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = cs.onSurface)
                Spacer(Modifier.height(6.dp))
                Text(stringResource(R.string.login_welcome),
                    style = MaterialTheme.typography.bodyMedium,
                    color = cs.onSurfaceVariant)

                Spacer(Modifier.height(24.dp))

                // email
                AuthFieldLabel(stringResource(R.string.login_email_label))
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value         = email,
                    onValueChange = { email = it; emailTouched = true },
                    isError       = showEmailErr,
                    placeholder   = { Text("nom.prenom@domain.dz",
                        color = cs.onSurfaceVariant, fontSize = 13.sp) },
                    supportingText = if (showEmailErr) {
                        { Text(stringResource(R.string.err_email_invalid),
                            color = cs.error, fontSize = 11.sp) }
                    } else null,
                    modifier   = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape      = RoundedCornerShape(8.dp),
                    colors     = authFieldColors()
                )

                Spacer(Modifier.height(16.dp))

                // password
                AuthFieldLabel(stringResource(R.string.login_password_label))
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value         = password,
                    onValueChange = { password = it },
                    placeholder   = { Text("••••••••", color = cs.onSurfaceVariant, fontSize = 13.sp) },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    shape         = RoundedCornerShape(8.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon  = {
                        TextButton(onClick = { passwordVisible = !passwordVisible },
                            contentPadding = PaddingValues(horizontal = 10.dp)) {
                            Text(if (passwordVisible) "Masquer" else "Afficher",
                                fontSize = 11.sp, color = cs.secondary,
                                fontWeight = FontWeight.SemiBold)
                        }
                    },
                    colors = authFieldColors()
                )

                Spacer(Modifier.height(10.dp))

                // remember me + forgot password
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it },
                            modifier = Modifier.size(20.dp),
                            colors = CheckboxDefaults.colors(
                                checkedColor   = cs.secondary,
                                uncheckedColor = cs.outline))
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.login_remember_me),
                            fontSize = 12.sp, color = cs.onSurfaceVariant)
                    }
                    Text(stringResource(R.string.login_forgot_password),
                        fontSize = 12.sp, color = cs.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onForgotPasswordClick() })
                }

                Spacer(Modifier.height(18.dp))

                // sign in button
                Button(
                    onClick  = { onLoginClick(email, password) },
                    enabled  = canLogin,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(8.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = cs.secondary,
                        disabledContainerColor = cs.secondaryContainer)
                ) {
                    Text(stringResource(R.string.login_sign_in_btn),
                        fontSize = 15.sp, fontWeight = FontWeight.Bold,
                        color = cs.onSecondary)
                }

                Spacer(Modifier.height(20.dp))

                // OR divider
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = cs.outline)
                    Text("  ${stringResource(R.string.login_or_divider)}  ",
                        fontSize = 11.sp, color = cs.onSurfaceVariant)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = cs.outline)
                }

                Spacer(Modifier.height(16.dp))

                // biometrics
                OutlinedButton(
                    onClick  = { onBiometricsClick() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(8.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = cs.onSurface)
                ) {
                    Text(stringResource(R.string.login_biometrics),
                        fontSize = 14.sp, fontWeight = FontWeight.Medium,
                        color = cs.onSurface)
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // register link
        Row(horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.login_no_account),
                fontSize = 13.sp, color = cs.onSurfaceVariant)
            Spacer(Modifier.width(4.dp))
            Text(stringResource(R.string.login_register),
                fontSize = 13.sp, fontWeight = FontWeight.Bold,
                color = cs.onSurface,
                modifier = Modifier.clickable { onRegisterClick() })
        }

        Spacer(Modifier.height(16.dp))

        // language switcher
        LanguageSwitcher(selectedLang, onLanguageChange)

        Spacer(Modifier.height(16.dp))

        Text(stringResource(R.string.footer_ministry),
            fontSize = 9.sp, color = cs.onSurfaceVariant,
            letterSpacing = 1.5.sp, textAlign = TextAlign.Center)

        Spacer(Modifier.height(24.dp))
    }
}

// ─────────────────────────────────────────────
//  SHARED COMPOSABLE HELPERS  (used by ALL auth screens)
// ─────────────────────────────────────────────

@Composable
fun AuthFieldLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface)
}

@Composable
fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = MaterialTheme.colorScheme.secondary,
    unfocusedBorderColor    = MaterialTheme.colorScheme.outline,
    focusedTextColor        = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor      = MaterialTheme.colorScheme.onSurface,
    cursorColor             = MaterialTheme.colorScheme.secondary,
    focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    errorBorderColor        = MaterialTheme.colorScheme.error,
    errorContainerColor     = MaterialTheme.colorScheme.surfaceVariant
)

@Composable
fun LanguageSwitcher(
    selectedLang    : AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Row(horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        AppLanguage.entries.forEachIndexed { index, lang ->
            if (index > 0) Text("  •  ", fontSize = 11.sp, color = cs.onSurfaceVariant)
            Text(
                text       = lang.label,
                fontSize   = 11.sp,
                color      = if (selectedLang == lang) cs.secondary else cs.onSurfaceVariant,
                fontWeight = if (selectedLang == lang) FontWeight.Bold else FontWeight.Normal,
                modifier   = Modifier.clickable { onLanguageChange(lang) }
            )
        }
    }
}