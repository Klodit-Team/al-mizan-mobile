package com.klodit.almizan.ui.main

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klodit.almizan.ui.components.AlMizanBottomBar
import com.klodit.almizan.ui.components.TopBar
import com.klodit.almizan.ui.components.BottomNavDestination
import com.klodit.almizan.ui.home.HomeScreen
import com.klodit.almizan.ui.bids.MyBidsScreen
import com.klodit.almizan.ui.profile.ProfileScreen
import com.klodit.almizan.ui.search.FilterState
import com.klodit.almizan.ui.tender.TenderListScreen
import com.klodit.almizan.viewmodel.MainViewModel
import com.klodit.almizan.viewmodel.profile.ProfileViewModel

@Composable
fun MainScreen(
    viewModel                  : MainViewModel    = viewModel(),
    profileViewModel           : ProfileViewModel = viewModel(),
    activeFilter               : FilterState      = FilterState(),
    userId                     : String           = "",
    token                      : String           = "",
    onNavigateToLogin          : () -> Unit       = {},
    onNavigateToFilter         : () -> Unit       = {},
    onNavigateToTenderDetail   : (String) -> Unit = {},
    onNavigateToEditProfile    : (String) -> Unit = {},
    onNavigateToChangePassword : () -> Unit       = {},
    onNavigateToDeleteAccount  : (String) -> Unit = {}
) {
    val currentRoute  by viewModel.currentRoute.collectAsState()
    val userFirstName by viewModel.userFirstName.collectAsState()
    val userLastName  by viewModel.userLastName.collectAsState()
    val isVerified    by viewModel.isVerified.collectAsState()
    val tier          by viewModel.tier.collectAsState()
    val unreadCount   by viewModel.unreadCount.collectAsState()
    val language      by viewModel.language.collectAsState()

    val localizedContext = remember(language) {
        val locale = java.util.Locale(language.locale)
        val config = android.content.res.Configuration(
            viewModel.getApplication<android.app.Application>().resources.configuration
        )
        config.setLocale(locale)
        viewModel.getApplication<android.app.Application>().createConfigurationContext(config)
    }

    val profileState by profileViewModel.profileUiState.collectAsState()
    LaunchedEffect(profileState) {
        if (profileState is com.klodit.almizan.data.profile.ProfileUiState.Success) {
            val p = (profileState as com.klodit.almizan.data.profile.ProfileUiState.Success).profile
            viewModel.onProfileLoaded(
                userId     = p.userId,
                profileId  = p.id,
                firstName  = p.firstName,
                lastName   = p.lastName,
                isVerified = p.isVerified,
                tier       = p.tier
            )
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            TopBar(
                userFirstName        = userFirstName,
                userLastName         = userLastName,
                isVerified           = isVerified,
                tier                 = tier,
                unreadCount          = unreadCount,
                onNotificationsClick = {},
                onLogoutClick        = {
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
        when (currentRoute) {
            BottomNavDestination.Home.route -> HomeScreen(innerPadding)

            BottomNavDestination.Tenders.route -> TenderListScreen(
                innerPadding           = innerPadding,
                localizedContext       = localizedContext,
                activeFilter           = activeFilter,
                onNavigateToFilter     = onNavigateToFilter,
                onNavigateToDetail     = onNavigateToTenderDetail
            )

            BottomNavDestination.MyBids.route -> MyBidsScreen(innerPadding)

            BottomNavDestination.Profile.route -> ProfileScreen(
                userId                     = userId,
                token                      = token,
                viewModel                  = profileViewModel,
                innerPadding               = innerPadding,
                onNavigateToEdit           = onNavigateToEditProfile,
                onNavigateToChangePassword = onNavigateToChangePassword,
                onNavigateToDeleteAccount  = onNavigateToDeleteAccount,
                onLogout                   = {
                    viewModel.onLogout()
                    onNavigateToLogin()
                }
            )

            else -> HomeScreen(innerPadding)
        }
    }
}