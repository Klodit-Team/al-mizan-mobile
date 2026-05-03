package com.klodit.almizan.navigation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.klodit.almizan.ui.auth.*
import com.klodit.almizan.ui.Registration.RegistrationStep1Screen
import com.klodit.almizan.ui.Registration.RegistrationStep2Screen
import com.klodit.almizan.ui.Registration.RegistrationStep3Screen
import com.klodit.almizan.ui.main.MainScreen
import com.klodit.almizan.ui.search.DetailedFilterScreen
import com.klodit.almizan.ui.theme.AppLanguage
import com.klodit.almizan.util.LocaleHelper
import com.klodit.almizan.viewmodel.auth.AuthState
import com.klodit.almizan.viewmodel.auth.AuthViewModel

private object Routes {
    const val LOGIN              = "login"
    const val VERIFICATION       = "verification/{email}"  // email arg for resend
    const val FORGOT_PASSWORD    = "forgot_password"
    const val SET_NEW_PASSWORD   = "set_new_password"
    const val ACCOUNT_LOCKED     = "account_locked"
    const val REGISTRATION_STEP1 = "registration_step1"
    const val REGISTRATION_STEP2 = "registration_step2"
    const val REGISTRATION_STEP3 = "registration_step3"
    const val TERMS              = "terms"
    const val PRIVACY            = "privacy"
    const val MAIN               = "main"
    const val FILTER             = "filter"
}

@Composable
fun NavGraph(onAuthSuccess: () -> Unit = {}) {
    val navController                = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val baseContext                  = LocalContext.current

    var selectedLang by remember {
        mutableStateOf(LocaleHelper.currentLanguage(baseContext))
    }

    val localizedContext = remember(selectedLang) {
        LocaleHelper.applyLocale(baseContext, selectedLang)
    }

    val layoutDirection = if (selectedLang == AppLanguage.ARABIC)
        LayoutDirection.Rtl else LayoutDirection.Ltr

    val onLanguageChange: (AppLanguage) -> Unit = { lang ->
        LocaleHelper.setLocale(baseContext, lang)
        selectedLang = lang
    }

    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        pickedUri = uri
        if (uri != null) authViewModel.uploadDocument(baseContext, uri) {}
    }

    CompositionLocalProvider(
        LocalContext        provides localizedContext,
        LocalLayoutDirection provides layoutDirection
    ) {
        NavHost(navController = navController, startDestination = Routes.LOGIN) {

            // ── Login ─────────────────────────────────────────────────────────
            composable(Routes.LOGIN) {
                LoginScreen(
                    selectedLang          = selectedLang,
                    onLanguageChange      = onLanguageChange,
                    authState             = authViewModel.authState,
                    onClearError          = { authViewModel.clearError() },
                    onLoginClick          = { email, password ->
                        authViewModel.login(
                            email     = email,
                            password  = password,
                            onSuccess = {
                                navController.navigate(Routes.MAIN) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            },
                            onLocked  = { navController.navigate(Routes.ACCOUNT_LOCKED) }
                        )
                    },
                    onForgotPasswordClick = { navController.navigate(Routes.FORGOT_PASSWORD) },
                    onRegisterClick       = { navController.navigate(Routes.REGISTRATION_STEP1) },
                    onBiometricsClick     = {}
                )
            }

            // ── Forgot password ───────────────────────────────────────────────
            composable(Routes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    selectedLang     = selectedLang,
                    onLanguageChange = onLanguageChange,
                    authState        = authViewModel.authState,
                    onClearError     = { authViewModel.clearError() },
                    onSendClick      = { email ->
                        authViewModel.forgotPassword(email) {
                            // Navigate to verification, passing the email so the
                            // resend button can call forgotPassword again if needed
                            navController.navigate("verification/${Uri.encode(email)}")
                        }
                    },
                    onBackClick      = { navController.popBackStack() },
                    onSignInClick    = { navController.popBackStack(Routes.LOGIN, false) }
                )
            }

            // ── OTP Verification ──────────────────────────────────────────────
            composable("verification/{email}") { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""
                VerificationScreen(
                    selectedLang     = selectedLang,
                    onLanguageChange = onLanguageChange,
                    authState        = authViewModel.authState,
                    onClearError     = { authViewModel.clearError() },
                    onVerifyClick    = { code ->
                        authViewModel.verifyToken(code) {
                            navController.navigate(Routes.SET_NEW_PASSWORD) {
                                // Remove forgot_password + verification from back stack
                                // so Back doesn't return there after resetting password
                                popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                            }
                        }
                    },
                    onResendClick    = {
                        // Re-call forgotPassword with the original email
                        authViewModel.forgotPassword(email) {}
                    },
                    onLogoutClick    = { navController.popBackStack(Routes.LOGIN, false) }
                )
            }

            // ── Set new password ──────────────────────────────────────────────
            composable(Routes.SET_NEW_PASSWORD) {
                SetNewPasswordScreen(
                    selectedLang     = selectedLang,
                    onLanguageChange = onLanguageChange,
                    authState        = authViewModel.authState,
                    onClearError     = { authViewModel.clearError() },
                    onSaveClick      = { code, newPassword ->
                        authViewModel.resetPassword(code, newPassword) {
                            navController.popBackStack(Routes.LOGIN, false)
                        }
                    },
                    onBackClick      = { navController.popBackStack() }
                )
            }

            // ── Account locked ────────────────────────────────────────────────
            composable(Routes.ACCOUNT_LOCKED) {
                AccountLockedScreen(
                    lockDurationSeconds  = 300 + (authViewModel.failedLoginAttempts - 5) * 60,
                    selectedLang         = selectedLang,
                    onLanguageChange     = onLanguageChange,
                    onResetPasswordClick = {
                        authViewModel.resetFailedAttempts()
                        navController.navigate(Routes.FORGOT_PASSWORD)
                    },
                    onTimerExpired       = {
                        authViewModel.resetFailedAttempts()
                        navController.popBackStack(Routes.LOGIN, false)
                    },
                    onContactSupport     = {}
                )
            }

            // ── Registration step 1 ───────────────────────────────────────────
            composable(Routes.REGISTRATION_STEP1) {
                RegistrationStep1Screen(
                    selectedLang     = selectedLang,
                    onLanguageChange = onLanguageChange,
                    onBackClick      = { navController.popBackStack() },
                    onTermsClick     = { navController.navigate(Routes.TERMS) },
                    onPrivacyClick   = { navController.navigate(Routes.PRIVACY) },
                    onContinueClick  = { orgName, nif, nis, rc ->
                        authViewModel.saveStep1(orgName, nif, nis, rc)
                        navController.navigate(Routes.REGISTRATION_STEP2)
                    }
                )
            }

            // ── Registration step 2 ───────────────────────────────────────────
            composable(Routes.REGISTRATION_STEP2) {
                RegistrationStep2Screen(
                    selectedLang     = selectedLang,
                    onLanguageChange = onLanguageChange,
                    onBackClick      = { navController.popBackStack() },
                    onContinueClick  = { phone, email, password, nom, prenom ->
                        authViewModel.saveStep2(phone, email, password, nom, prenom)
                        navController.navigate(Routes.REGISTRATION_STEP3)
                    }
                )
            }

            // ── Registration step 3 ───────────────────────────────────────────
            composable(Routes.REGISTRATION_STEP3) {
                RegistrationStep3Screen(
                    selectedLang       = selectedLang,
                    onLanguageChange   = onLanguageChange,
                    onBackClick        = { authViewModel.clearError(); navController.popBackStack() },
                    authState          = authViewModel.authState,
                    uploadState        = authViewModel.uploadState,
                    onPickFile         = { /* filePickerLauncher.launch("application/pdf") */ },
                    onClearError       = { authViewModel.clearError() },
                    onClearUploadError = { authViewModel.clearUploadError() },
                    onSubmitClick      = {
                        if (authViewModel.authState is AuthState.Success) {
                            authViewModel.resetState()
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.REGISTRATION_STEP1) { inclusive = true }
                            }
                        } else {
                            authViewModel.register(selectedLang) { _ -> }
                        }
                    }
                )
            }

            // ── Main shell ────────────────────────────────────────────────────
            composable(Routes.MAIN) {
                MainScreen(
                    onNavigateToLogin  = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.MAIN) { inclusive = true }
                        }
                    },
                    onNavigateToFilter = { navController.navigate(Routes.FILTER) }
                )
            }

            // ── Filter ────────────────────────────────────────────────────────
            composable(Routes.FILTER) {
                DetailedFilterScreen(
                    localizedContext = localizedContext,
                    onApply          = { navController.popBackStack() },
                    onDismiss        = { navController.popBackStack() }
                )
            }

            composable(Routes.TERMS)   { }
            composable(Routes.PRIVACY) { }
        }
    }
}