package com.klodit.almizan.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.klodit.almizan.ui.auth.*
import com.klodit.almizan.ui.Registration.RegistrationStep1Screen
import com.klodit.almizan.ui.Registration.RegistrationStep2Screen
import com.klodit.almizan.ui.Registration.RegistrationStep3Screen
import com.klodit.almizan.ui.main.MainScreen
import com.klodit.almizan.ui.search.AdvancedFilterScreen
import com.klodit.almizan.ui.theme.AppLanguage
import com.klodit.almizan.util.LocaleHelper

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
    val navController  = rememberNavController()
    var selectedLang   by remember { mutableStateOf(AppLanguage.FRENCH) }
    var failedAttempts by remember { mutableIntStateOf(0) }

    fun changeLanguage(lang: AppLanguage) {
        selectedLang = lang
        LocaleHelper.setLocale(lang)
    }

    NavHost(navController = navController, startDestination = Routes.MAIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                selectedLang          = selectedLang,
                onLanguageChange      = { changeLanguage(it) },
                onLoginClick          = { _, _ ->
                    failedAttempts++
                    if (failedAttempts >= 5) navController.navigate(Routes.ACCOUNT_LOCKED)
                    else navController.navigate(Routes.VERIFICATION)
                },
                onForgotPasswordClick = { navController.navigate(Routes.FORGOT_PASSWORD) },
                onRegisterClick       = { navController.navigate(Routes.REGISTRATION_STEP1) }
            )
        }

        composable(Routes.VERIFICATION) {
            VerificationScreen(
                selectedLang     = selectedLang,
                onLanguageChange = { changeLanguage(it) },
                onVerifyClick    = { code ->
                    if (code == "123456") {
                        failedAttempts = 0
                        navController.navigate(Routes.MAIN) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
                onResendClick = {},
                onLogoutClick = { navController.popBackStack(Routes.LOGIN, false) }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                selectedLang     = selectedLang,
                onLanguageChange = { changeLanguage(it) },
                onSendClick      = { navController.navigate(Routes.SET_NEW_PASSWORD) },
                onBackClick      = { navController.popBackStack() },
                onSignInClick    = { navController.popBackStack(Routes.LOGIN, false) }
            )
        }

        composable(Routes.SET_NEW_PASSWORD) {
            SetNewPasswordScreen(
                selectedLang     = selectedLang,
                onLanguageChange = { changeLanguage(it) },
                onSaveClick      = { _, _ -> navController.popBackStack(Routes.LOGIN, false) },
                onBackClick      = { navController.popBackStack() }
            )
        }

        composable(Routes.ACCOUNT_LOCKED) {
            AccountLockedScreen(
                selectedLang         = selectedLang,
                onLanguageChange     = { changeLanguage(it) },
                onResetPasswordClick = { navController.navigate(Routes.FORGOT_PASSWORD) },
                onContactSupport     = {}
            )
        }

        composable(Routes.REGISTRATION_STEP1) {
            RegistrationStep1Screen(
                selectedLang     = selectedLang,
                onLanguageChange = { changeLanguage(it) },
                onContinueClick  = { _, _, _, _ -> navController.navigate(Routes.REGISTRATION_STEP2) },
                onBackClick      = { navController.popBackStack() },
                onTermsClick     = { navController.navigate(Routes.TERMS) },
                onPrivacyClick   = { navController.navigate(Routes.PRIVACY) }
            )
        }

        composable(Routes.REGISTRATION_STEP2) {
            RegistrationStep2Screen(
                selectedLang     = selectedLang,
                onLanguageChange = { changeLanguage(it) },
                onContinueClick  = { _, _, _ -> navController.navigate(Routes.REGISTRATION_STEP3) },
                onBackClick      = { navController.popBackStack() }
            )
        }

        composable(Routes.REGISTRATION_STEP3) {
            RegistrationStep3Screen(
                selectedLang     = selectedLang,
                onLanguageChange = { changeLanguage(it) },
                onSubmitClick    = { navController.popBackStack(Routes.LOGIN, false) },
                onBackClick      = { navController.popBackStack() }
            )
        }

        // Main shell
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

        // Standalone filter screen
        composable(Routes.FILTER) {
            val lang = LocaleHelper.currentLanguage()
            val localizedContext = remember(lang) {
                val locale = java.util.Locale(lang.locale)
                val config = android.content.res.Configuration()
                config.setLocale(locale)
                navController.context.createConfigurationContext(config)
            }
            AdvancedFilterScreen(
                localizedContext = localizedContext,
                localeTag        = lang.locale,
                onApply          = { navController.popBackStack() },
                onDismiss        = { navController.popBackStack() }
            )
        }

        composable(Routes.TERMS) {
            // TODO: TermsScreen()
        }

        composable(Routes.PRIVACY) {
            // TODO: PrivacyScreen()
        }
    }
}