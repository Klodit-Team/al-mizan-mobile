package com.klodit.almizan.navigation

import androidx.compose.runtime.*
import .navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.klodit.almizan.ui.auth.*

// ─────────────────────────────────────────────
//  ROUTES  (all screen names in one place)
// ─────────────────────────────────────────────
object Routes {
    const val LOGIN               = "login"
    const val VERIFICATION        = "verification"
    const val REGISTRATION_STEP1  = "registration_step1"
    const val REGISTRATION_STEP2  = "registration_step2"
    const val REGISTRATION_STEP3  = "registration_step3"
}

// ─────────────────────────────────────────────
//  NAV GRAPH
// ─────────────────────────────────────────────
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    // shared language state — changing it on any screen
    // affects all other screens automatically
    var selectedLang by remember { mutableStateOf(AppLanguage.FRENCH) }

    NavHost(
        navController    = navController,
        startDestination = Routes.LOGIN
    ) {

        // ── LOGIN ────────────────────────────
        composable(Routes.LOGIN) {
            LoginScreen(
                selectedLang     = selectedLang,
                onLanguageChange = { selectedLang = it },
                onLoginClick     = { _, _ ->
                    navController.navigate(Routes.VERIFICATION)
                },
                onForgotPasswordClick = {
                    // TODO: navigate to forgot password screen
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTRATION_STEP1)
                },
                onBiometricsClick = {
                    // TODO: handle biometrics
                }
            )
        }

        // ── VERIFICATION ─────────────────────
        composable(Routes.VERIFICATION) {
            VerificationScreen(
                onVerifyClick = { _ ->
                    // TODO: navigate to home dashboard after verify
                },
                onResendClick = {
                    // TODO: call resend API
                },
                onLogoutClick = {
                    // go back to login and clear backstack
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNotifClick = {
                    // TODO: handle notification tap
                }
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
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // ── REGISTRATION STEP 2 ──────────────
        composable(Routes.REGISTRATION_STEP2) {
            RegistrationStep2Screen(
                onContinueClick = {
                    navController.navigate(Routes.REGISTRATION_STEP3)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // ── REGISTRATION STEP 3 ──────────────
        composable(Routes.REGISTRATION_STEP3) {
            RegistrationStep3Screen(
                onSubmitClick = {
                    // TODO: navigate to success or login after submit
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}