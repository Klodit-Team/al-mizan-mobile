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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
private val DarkHeader = Color(0xFF364150)
private val GreenAccent = Color(0xFF4CAE4F)
private val White = Color.White
private val BorderGrey = Color(0xFFDDE3E8)
private val TextDark = Color(0xFF1A2B38)
private val TextMid = Color(0xFF4A6070)
private val TextLight = Color(0xFF8FA3B0)
private val FieldBg = Color(0xFFF8FAFB)
private val PageBg = Color(0xFFECEFF1)

// ─────────────────────────────────────────────
//  LANGUAGE  (defined here, shared across app)
// ─────────────────────────────────────────────
enum class AppLanguage(val label: String) {
    FRENCH("FRANÇAIS"), ARABIC("العربية"), ENGLISH("ENGLISH")
}

data class LoginStrings(
    val signIn: String,
    val welcome: String,
    val emailLabel: String,
    val passwordLabel: String,
    val rememberMe: String,
    val forgotPassword: String,
    val signInButton: String,
    val orDivider: String,
    val biometrics: String,
    val noAccount: String,
    val register: String
) {
    companion object {
        val french = LoginStrings(
            signIn = "Connexion",
            welcome = "Bienvenue ! Veuillez saisir vos identifiants pour accéder à votre tableau de bord.",
            emailLabel = "Adresse e-mail",
            passwordLabel = "Mot de passe",
            rememberMe = "Se souvenir de moi",
            forgotPassword = "Mot de passe oublié ?",
            signInButton = "Se connecter",
            orDivider = "OU",
            biometrics = "Se connecter avec la biométrie",
            noAccount = "Pas encore de compte ?",
            register = "S'inscrire ici"
        )
        val arabic = LoginStrings(
            signIn = "تسجيل الدخول",
            welcome = "مرحباً! يرجى إدخال بيانات الاعتماد للوصول إلى لوحة التحكم.",
            emailLabel = "البريد الإلكتروني",
            passwordLabel = "كلمة المرور",
            rememberMe = "تذكرني",
            forgotPassword = "نسيت كلمة المرور؟",
            signInButton = "دخول",
            orDivider = "أو",
            biometrics = "الدخول بالبصمة",
            noAccount = "ليس لديك حساب؟",
            register = "سجّل هنا"
        )
        val english = LoginStrings(
            signIn = "Sign In",
            welcome = "Welcome back! Please enter your credentials to access your dashboard.",
            emailLabel = "Email Address",
            passwordLabel = "Password",
            rememberMe = "Remember me",
            forgotPassword = "Forgot password?",
            signInButton = "Sign In",
            orDivider = "OR",
            biometrics = "Sign in with Biometrics",
            noAccount = "Don't have an account yet?",
            register = "Register here"
        )
    }
}

// ─────────────────────────────────────────────
//  LOGIN SCREEN
// ─────────────────────────────────────────────
@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String) -> Unit = { _, _ -> },
    onForgotPasswordClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onBiometricsClick: () -> Unit = {},
    selectedLang: AppLanguage = AppLanguage.FRENCH,
    onLanguageChange: (AppLanguage) -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    val strings = when (selectedLang) {
        AppLanguage.FRENCH -> LoginStrings.french
        AppLanguage.ARABIC -> LoginStrings.arabic
        AppLanguage.ENGLISH -> LoginStrings.english
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = if (screenWidth < 500.dp) screenWidth * 0.90f else 420.dp
    val overlapAmount = 32.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── dark header ──────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkHeader)
                .statusBarsPadding()
                .padding(
                    top = 24.dp,
                    bottom = overlapAmount + 24.dp,
                    start = 24.dp,
                    end = 24.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(52.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "AL-MIZAN",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = White,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "SOVEREIGN PROCUREMENT",
                        fontSize = 9.sp,
                        color = GreenAccent,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // ── white card (overlapping header) ──
        Card(
            modifier = Modifier
                .width(cardWidth)
                .offset(y = -overlapAmount)
                .zIndex(1f),
            shape = RoundedCornerShape(
                topStart = 20.dp, topEnd = 20.dp,
                bottomStart = 16.dp, bottomEnd = 16.dp
            ),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {

                Text(
                    strings.signIn,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(Modifier.height(6.dp))
                Text(strings.welcome, fontSize = 13.sp, color = TextMid, lineHeight = 19.sp)

                Spacer(Modifier.height(24.dp))

                // email
                FieldLabel(strings.emailLabel)
                Spacer(Modifier.height(6.dp))
                var emailTouched by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value         = email,
                    onValueChange = { email = it; emailTouched = true },
                    isError       = emailTouched && email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                    placeholder = {
                        Text(
                            "nom.prenom@domain.dz",
                            color = TextLight,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = fieldColors()
                )

                Spacer(Modifier.height(16.dp))

                // password
                FieldLabel(strings.passwordLabel)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("••••••••", color = TextLight, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(
                            onClick = { passwordVisible = !passwordVisible },
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Text(
                                text = if (passwordVisible) "Masquer" else "Afficher",
                                fontSize = 11.sp,
                                color = GreenAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    },
                    colors = fieldColors()
                )

                Spacer(Modifier.height(10.dp))

                // remember me + forgot password
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            modifier = Modifier.size(20.dp),
                            colors = CheckboxDefaults.colors(
                                checkedColor = GreenAccent,
                                uncheckedColor = BorderGrey
                            )
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(strings.rememberMe, fontSize = 12.sp, color = TextMid)
                    }
                    Text(
                        text = strings.forgotPassword,
                        fontSize = 12.sp,
                        color = TextDark,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onForgotPasswordClick() }
                    )
                }

                Spacer(Modifier.height(18.dp))

                // sign in button
                Button(
                    onClick = {
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            onLoginClick(email, password)
                        }
                    },
                    enabled = email.isNotBlank() && password.isNotBlank()
                            && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenAccent,
                        disabledContainerColor = Color(0xFFB0CDB9)
                    )
                ) {
                    Text(
                        strings.signInButton,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }

                Spacer(Modifier.height(20.dp))

                // OR divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = BorderGrey)
                    Text("  ${strings.orDivider}  ", fontSize = 11.sp, color = TextLight)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = BorderGrey)
                }

                Spacer(Modifier.height(16.dp))

                // biometrics
                OutlinedButton(
                    onClick = { onBiometricsClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark)
                ) {
                    Text(
                        strings.biometrics,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // register link
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(strings.noAccount, fontSize = 13.sp, color = TextMid)
            Spacer(Modifier.width(4.dp))
            Text(
                text = strings.register,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.clickable { onRegisterClick() }
            )
        }

        Spacer(Modifier.height(16.dp))

        // language selector
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppLanguage.entries.forEachIndexed { index, lang ->
                if (index > 0) Text("  •  ", fontSize = 11.sp, color = TextLight)
                Text(
                    text = lang.label,
                    fontSize = 11.sp,
                    color = if (selectedLang == lang) GreenAccent else TextLight,
                    fontWeight = if (selectedLang == lang) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { onLanguageChange(lang) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "MINISTÈRE DES FINANCES",
            fontSize = 9.sp,
            color = TextLight,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))
    }
}

// ─────────────────────────────────────────────
//  HELPERS
// ─────────────────────────────────────────────
@Composable
private fun FieldLabel(text: String) {
    Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = GreenAccent,
    unfocusedBorderColor = BorderGrey,
    focusedTextColor = TextDark,
    unfocusedTextColor = TextDark,
    cursorColor = GreenAccent,
    focusedContainerColor = FieldBg,
    unfocusedContainerColor = FieldBg
)

