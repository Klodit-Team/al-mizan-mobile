package com.klodit.almizan.ui.main

import android.content.Context
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klodit.almizan.ui.components.AlMizanBottomBar
import com.klodit.almizan.ui.components.AlMizanTopBar
import com.klodit.almizan.ui.components.BottomNavDestination
import com.klodit.almizan.ui.home.HomeScreen
import com.klodit.almizan.ui.bids.MyBidsScreen
import com.klodit.almizan.ui.profile.ProfileScreen
import com.klodit.almizan.ui.tender.TenderListScreen
import com.klodit.almizan.viewmodel.MainViewModel

@Composable
fun MainScreen(
    viewModel            : MainViewModel = viewModel(),
    onNavigateToLogin    : () -> Unit    = {},
    onNavigateToFilter   : () -> Unit    = {}
) {
    val currentRoute by viewModel.currentRoute.collectAsState()
    val userName     by viewModel.userName.collectAsState()
    val language     by viewModel.language.collectAsState()

    val localizedContext = remember(language) {
        val locale = java.util.Locale(language.locale)
        val config = android.content.res.Configuration(
            viewModel.getApplication<android.app.Application>().resources.configuration
        )
        config.setLocale(locale)
        viewModel.getApplication<android.app.Application>().createConfigurationContext(config)
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            AlMizanTopBar(
                userName            = userName,
                language            = language,
                localizedContext    = localizedContext,
                onLanguageChange    = { viewModel.onLanguageChange(it) },
                onNotificationClick = { },
                onLogoutClick       = {
                    viewModel.onLogout()
                    onNavigateToLogin()
                }
            )
        },
        bottomBar = {
            AlMizanBottomBar(
                currentRoute          = currentRoute,
                localizedContext      = localizedContext,
                onDestinationSelected = { viewModel.onTabSelected(it) }
            )
        }
    ) { innerPadding ->
        // Each tab is its own self-contained screen file
        when (currentRoute) {
            BottomNavDestination.Home.route    -> HomeScreen(innerPadding)
            BottomNavDestination.Tenders.route -> TenderListScreen(
                innerPadding       = innerPadding,
                localizedContext   = localizedContext,
                onNavigateToFilter = onNavigateToFilter
            )
            BottomNavDestination.MyBids.route  -> MyBidsScreen(innerPadding)
            BottomNavDestination.Profile.route -> ProfileScreen(innerPadding)
            else                               -> HomeScreen(innerPadding)
        }
    }
}