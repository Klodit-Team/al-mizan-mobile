package com.klodit.almizan.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.klodit.almizan.ui.Registration.RegistrationStep1Screen
import com.klodit.almizan.ui.Registration.RegistrationStep2Screen
import com.klodit.almizan.ui.Registration.RegistrationStep3Screen
import com.klodit.almizan.ui.auth.*

// ─────────────────────────────────────────────
//  ROUTES
// ─────────────────────────────────────────────
object Routes {
    const val LOGIN              = "login"
    const val VERIFICATION       = "verification"
    const val FORGOT_PASSWORD    = "forgot_password"
    const val SET_NEW_PASSWORD   = "set_new_password"
    const val ACCOUNT_LOCKED     = "account_locked"
    const val REGISTRATION_STEP1 = "registration_step1"
    const val REGISTRATION_STEP2 = "registration_step2"
    const val REGISTRATION_STEP3 = "registration_step3"
}

// ─────────────────────────────────────────────
//  NAV GRAPH
// ─────────────────────────────────────────────
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    var selectedLang   by remember { mutableStateOf(AppLanguage.FRENCH) }
    var failedAttempts by remember { mutableIntStateOf(0) }

    NavHost(
        navController    = navController,
        startDestination = Routes.LOGIN
    ) {

        // ── LOGIN ────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(
                selectedLang          = selectedLang,
                onLanguageChange      = { selectedLang = it },
                /*onLoginClick          = { _, _ ->
                    failedAttempts++
                    if (failedAttempts >= 5) {
                        failedAttempts = 0
                        navController.navigate(Routes.ACCOUNT_LOCKED)
                    } else {
                        navController.navigate(Routes.VERIFICATION)
                    }
                },*/
                onLoginClick = { email, password ->
                    if (password == "admin123") {
                        // correct password → go to verification
                        failedAttempts = 0
                        navController.navigate(Routes.VERIFICATION)
                    } else {
                        // wrong password → count attempt
                        failedAttempts++
                        if (failedAttempts >= 5) {
                            failedAttempts = 0
                            navController.navigate(Routes.ACCOUNT_LOCKED)
                        }
                        // else stay on login screen and show nothing (real app shows error toast)
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                },
                onRegisterClick       = {
                    navController.navigate(Routes.REGISTRATION_STEP1)
                },
                onBiometricsClick     = { }
            )
        }

        // ── VERIFICATION ─────────────────────
        composable(Routes.VERIFICATION) {
            VerificationScreen(
                onVerifyClick    = { code ->
                    if (code == "123456") {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
                onResendClick    = { },
                onLogoutClick    = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNotifClick     = { },
                selectedLang     = selectedLang,
                onLanguageChange = { selectedLang = it }
            )
        }

        // ── FORGOT PASSWORD ──────────────────
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                selectedLang     = selectedLang,
                onLanguageChange = { selectedLang = it },
                onSendClick      = { _ ->
                    navController.navigate(Routes.SET_NEW_PASSWORD)
                },
                onBackClick      = { navController.popBackStack() },
                onSignInClick    = { navController.popBackStack() }
            )
        }

        // ── SET NEW PASSWORD ─────────────────
        composable(Routes.SET_NEW_PASSWORD) {
            SetNewPasswordScreen(
                selectedLang     = selectedLang,
                onLanguageChange = { selectedLang = it },
                onSaveClick      = { _, _ ->
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBackClick      = { navController.popBackStack() }
            )
        }

        // ── ACCOUNT LOCKED ───────────────────
        composable(Routes.ACCOUNT_LOCKED) {
            AccountLockedScreen(
                selectedLang         = selectedLang,
                onLanguageChange     = { selectedLang = it },
                onResetPasswordClick = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                },
                onContactSupport     = { }
            )
        }

        // ── REGISTRATION STEP 1 ──────────────
        composable(Routes.REGISTRATION_STEP1) {
            RegistrationStep1Screen(
                selectedLang     = selectedLang,
                onLanguageChange = { selectedLang = it },
                onContinueClick  = { _, _, _, _ ->
                    navController.navigate(Routes.REGISTRATION_STEP2)
                },
                onBackClick      = { navController.popBackStack() }
            )
        }

        // ── REGISTRATION STEP 2 ──────────────
        composable(Routes.REGISTRATION_STEP2) {
            RegistrationStep2Screen(
                selectedLang     = selectedLang,
                onLanguageChange = { selectedLang = it },
                onContinueClick  = { _, _, _, _, _ ->
                    navController.navigate(Routes.REGISTRATION_STEP3)
                },
                onBackClick      = { navController.popBackStack() }
            )
        }

        // ── REGISTRATION STEP 3 ──────────────
        composable(Routes.REGISTRATION_STEP3) {
            RegistrationStep3Screen(
                selectedLang     = selectedLang,
                onLanguageChange = { selectedLang = it },
                onSubmitClick    = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBackClick      = { navController.popBackStack() }
            )
        }
    }
}