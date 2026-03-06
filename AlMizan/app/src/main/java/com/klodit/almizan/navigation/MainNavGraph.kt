package com.klodit.almizan.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.klodit.almizan.ui.main.HomeScreen

// ─────────────────────────────────────────────
//  MAIN ROUTES
//
// ─────────────────────────────────────────────
object MainRoutes {
    const val HOME    = "home"
    const val SEARCH  = "search"
    const val ALERTS  = "alerts"
    const val PROFILE = "profile"
    const val TENDER_DETAIL = "tender_detail/{tenderId}"

    fun tenderDetail(id: String) = "tender_detail/$id"
}

// ─────────────────────────────────────────────
//  MAIN NAV GRAPH
// ─────────────────────────────────────────────
@Composable
fun MainNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController    = navController,
        startDestination = MainRoutes.HOME
    ) {

        // ── HOME ─────────────────────────────
        composable(MainRoutes.HOME) {
            HomeScreen(
                onSearchClick  = { navController.navigate(MainRoutes.SEARCH) },
                onViewAllClick = { navController.navigate(MainRoutes.SEARCH) },
                onTenderClick  = { id ->
                    navController.navigate(MainRoutes.tenderDetail(id))
                },
                onSignInClick  = { /* TODO: navigate back to auth */ },
                onMarketStats  = { /* TODO */ },
                onLegalInfo    = { /* TODO */ },
                onHelpCenter   = { /* TODO */ }
            )
        }

        // ── SEARCH ───────────────────────────
        composable(MainRoutes.SEARCH) {
            // TODO: build SearchScreen
        }

        // ── ALERTS ───────────────────────────
        composable(MainRoutes.ALERTS) {
            // TODO: build AlertsScreen
        }

        // ── PROFILE ──────────────────────────
        composable(MainRoutes.PROFILE) {
            // TODO: build ProfileScreen
        }

        // ── TENDER DETAIL ────────────────────
        composable(MainRoutes.TENDER_DETAIL) { backStackEntry ->
            val tenderId = backStackEntry.arguments?.getString("tenderId") ?: ""
            // TODO: build TenderDetailScreen(tenderId)
        }
    }
}