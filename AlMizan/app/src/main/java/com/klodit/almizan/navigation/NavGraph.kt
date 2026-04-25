package com.klodit.almizan.navigation

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
import com.klodit.almizan.viewmodel.auth.AuthViewModel

private object Routes {
    const val LOGIN              = "login"
    const val VERIFICATION       = "verification"
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
    val navController            = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val baseContext              = LocalContext.current

    // Read persisted language on first composition
    var selectedLang by remember {
        mutableStateOf(LocaleHelper.currentLanguage(baseContext))
    }

    // Build a localized context whenever selectedLang changes
    val localizedContext = remember(selectedLang) {
        LocaleHelper.applyLocale(baseContext, selectedLang)
    }

    // Derive layout direction from selected language
    val layoutDirection = if (selectedLang == AppLanguage.ARABIC) {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    // Shared lambda — persists choice + triggers instant recomposition
    val onLanguageChange: (AppLanguage) -> Unit = { lang ->
        LocaleHelper.setLocale(baseContext, lang)
        selectedLang = lang
    }

    // Provide BOTH localized context AND layout direction to the entire tree
    CompositionLocalProvider(
        LocalContext       provides localizedContext,
        LocalLayoutDirection provides layoutDirection
    ) {
        var failedAttempts by remember { mutableIntStateOf(0) }

        NavHost(navController = navController, startDestination = Routes.LOGIN) {

            // ── Login ─────────────────────────────────────────────────────────
            composable(Routes.LOGIN) {
                LoginScreen(
                    selectedLang          = selectedLang,
                    onLanguageChange      = onLanguageChange,
                    authState             = authViewModel.authState,
                    onClearError          = { authViewModel.clearError() },
                    onLoginClick          = { email, password ->
                        authViewModel.login(email, password) {
                            failedAttempts = 0
                            navController.navigate(Routes.VERIFICATION)
                        }
                    },
                    onForgotPasswordClick = { navController.navigate(Routes.FORGOT_PASSWORD) },
                    onRegisterClick       = { navController.navigate(Routes.REGISTRATION_STEP1) },
                    onBiometricsClick     = { }
                )
            }

            // ── OTP Verification ──────────────────────────────────────────────
            composable(Routes.VERIFICATION) {
                VerificationScreen(
                    selectedLang     = selectedLang,
                    onLanguageChange = onLanguageChange,
                    onVerifyClick    = { code ->
                        if (code == "123456") {
                            navController.navigate(Routes.MAIN) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                    },
                    onResendClick = {},
                    onLogoutClick = { navController.popBackStack(Routes.LOGIN, false) }
                )
            }

            // ── Forgot password ───────────────────────────────────────────────
            composable(Routes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    selectedLang     = selectedLang,
                    onLanguageChange = onLanguageChange,
                    onSendClick      = { navController.navigate(Routes.SET_NEW_PASSWORD) },
                    onBackClick      = { navController.popBackStack() },
                    onSignInClick    = { navController.popBackStack(Routes.LOGIN, false) }
                )
            }

            // ── Set new password ──────────────────────────────────────────────
            composable(Routes.SET_NEW_PASSWORD) {
                SetNewPasswordScreen(
                    selectedLang     = selectedLang,
                    onLanguageChange = onLanguageChange,
                    onSaveClick      = { _, _ -> navController.popBackStack(Routes.LOGIN, false) },
                    onBackClick      = { navController.popBackStack() }
                )
            }

            // ── Account locked ────────────────────────────────────────────────
            composable(Routes.ACCOUNT_LOCKED) {
                AccountLockedScreen(
                    selectedLang         = selectedLang,
                    onLanguageChange     = onLanguageChange,
                    onResetPasswordClick = { navController.navigate(Routes.FORGOT_PASSWORD) },
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
                    onContinueClick  = { phone, email, password ->
                        authViewModel.saveStep2(phone, email, password)
                        navController.navigate(Routes.REGISTRATION_STEP3)
                    }
                )
            }

            // ── Registration step 3 ───────────────────────────────────────────
            composable(Routes.REGISTRATION_STEP3) {
                RegistrationStep3Screen(
                    selectedLang     = selectedLang,
                    onLanguageChange = onLanguageChange,
                    onBackClick      = { authViewModel.clearError(); navController.popBackStack() },
                    authState        = authViewModel.authState,
                    onClearError     = { authViewModel.clearError() },
                    onSubmitClick    = {
                        authViewModel.register(selectedLang) { _ ->  // pass selectedLang here
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.REGISTRATION_STEP1) { inclusive = true }
                            }
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

            // ── Forgot password ───────────────────────────────────────────────────────────
            composable(Routes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    selectedLang     = selectedLang,
                    onLanguageChange = onLanguageChange,
                    onSendClick      = { email ->
                        authViewModel.forgotPassword(email) {
                            navController.navigate(Routes.SET_NEW_PASSWORD)
                        }
                    },
                    onBackClick   = { navController.popBackStack() },
                    onSignInClick = { navController.popBackStack(Routes.LOGIN, false) }
                )
            }

// ── Set new password ──────────────────────────────────────────────────────────
            composable(Routes.SET_NEW_PASSWORD) {
                SetNewPasswordScreen(
                    selectedLang     = selectedLang,
                    onLanguageChange = onLanguageChange,
                    onSaveClick      = { code, newPassword ->
                        authViewModel.resetPassword(code, newPassword) {
                            navController.popBackStack(Routes.LOGIN, false)
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

// ── Verification ──────────────────────────────────────────────────────────────

            composable(Routes.VERIFICATION) {
                VerificationScreen(
                    selectedLang     = selectedLang,
                    onLanguageChange = onLanguageChange,
                    onVerifyClick    = { code ->
                        // TODO: your backend has no post-login OTP endpoint yet
                        // Either skip this screen after login, or ask your backend team
                        // to add POST /auth/verify-otp
                        if (code == "123456") {
                            navController.navigate(Routes.MAIN) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                    },
                    onResendClick = {},
                    onLogoutClick = { navController.popBackStack(Routes.LOGIN, false) }
                )
            }
        }
    }
}