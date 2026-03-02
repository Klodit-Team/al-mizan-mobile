package com.klodit.almizan.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.klodit.almizan.ui.auth.*

// ─────────────────────────────────────────────
//  ROUTES
// ─────────────────────────────────────────────
object Routes {
    const val LOGIN              = "login"
    const val VERIFICATION       = "verification"
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
    // language lives here — shared across ALL screens
    var selectedLang by remember { mutableStateOf(AppLanguage.FRENCH) }

    NavHost(
        navController    = navController,
        startDestination = Routes.LOGIN
    ) {

        composable(Routes.LOGIN) {
            LoginScreen(
                selectedLang          = selectedLang,
                onLanguageChange      = { selectedLang = it },
                onLoginClick          = { _, _ ->
                    navController.navigate(Routes.VERIFICATION)
                },
                onForgotPasswordClick = { /* TODO */ },
                onRegisterClick       = {
                    navController.navigate(Routes.REGISTRATION_STEP1)
                },
                onBiometricsClick     = { /* TODO */ }
            )
        }

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

        composable(Routes.REGISTRATION_STEP2) {
            RegistrationStep2Screen(
                selectedLang     = selectedLang,
                onLanguageChange = { selectedLang = it },
                onContinueClick  = { _, _, _, _, _ ->
                    navController.navigate(Routes.REGISTRATION_STEP3)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.REGISTRATION_STEP3) {
            RegistrationStep3Screen(
                selectedLang     = selectedLang,
                onLanguageChange = { selectedLang = it },
                onSubmitClick    = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}