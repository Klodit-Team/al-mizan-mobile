package com.klodit.almizan.ui.main

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klodit.almizan.model.*
import com.klodit.almizan.ui.components.AlMizanBottomBar
import com.klodit.almizan.ui.components.AlMizanTopBar
import com.klodit.almizan.ui.components.BottomNavDestination
import com.klodit.almizan.ui.theme.*
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                BottomNavDestination.Home.route    -> PlaceholderTab("Home")
                BottomNavDestination.Search.route  -> SearchTabContent(
                    localizedContext   = localizedContext,
                    localeTag          = language.locale,
                    onNavigateToFilter = onNavigateToFilter
                )
                BottomNavDestination.MyBids.route  -> PlaceholderTab("My Bids")
                BottomNavDestination.Profile.route -> PlaceholderTab("Profile")
                else                               -> PlaceholderTab("Home")
            }
        }
    }
}

// Search tab
@Composable
private fun SearchTabContent(
    localizedContext   : Context,
    localeTag          : String,
    onNavigateToFilter : () -> Unit
) {
    val data       = remember { mockDashboardData() }
    val filterTabs = listOf("All", "Open", "In Progress", "Closed", "Urgent")
    var selectedTab by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val visibleCases = data.cases.filter { case ->
        val matchesTab    = selectedTab == "All" ||
                CaseStatus.from(case.status).label == selectedTab
        val matchesSearch = searchQuery.isBlank() ||
                case.title.contains(searchQuery, ignoreCase = true) ||
                case.caseNumber.contains(searchQuery, ignoreCase = true) ||
                case.category.contains(searchQuery, ignoreCase = true)
        matchesTab && matchesSearch
    }

    LazyColumn(
        modifier       = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            // Search bar + filter button
            Row(
                modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value         = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder   = { Text("Search cases…", color = Navy500, fontSize = 14.sp) },
                    leadingIcon   = { Icon(Icons.Default.Search, null, tint = Navy500) },
                    singleLine    = true,
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = NavyWhite,
                        focusedContainerColor   = NavyWhite,
                        unfocusedBorderColor    = Navy100,
                        focusedBorderColor      = Navy800,
                        cursorColor             = Navy800
                    ),
                    modifier      = Modifier.weight(1f).height(52.dp)
                )
                // Filter icon button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier
                        .size(52.dp)
                        .shadow(2.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(NavyWhite)
                        .border(1.dp, Navy100, RoundedCornerShape(12.dp))
                        .clickable { onNavigateToFilter() }
                ) {
                    Icon(Icons.Outlined.Tune, null, tint = Navy800, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        item {
            // Status filter chips
            LazyRow(
                contentPadding        = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterTabs) { tab ->
                    FilterChipItem(
                        label    = tab,
                        selected = selectedTab == tab,
                        onClick  = { selectedTab = tab }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        items(visibleCases, key = { it.id }) { case ->
            CaseCardItem(
                case     = case,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}

// Filter chip
@Composable
private fun FilterChipItem(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier         = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(if (selected) Navy800 else NavyWhite)
            .border(1.dp, if (selected) Navy800 else Navy100, RoundedCornerShape(50.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            text       = label,
            color      = if (selected) NavyWhite else Navy700,
            fontSize   = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// Case card
@Composable
private fun CaseCardItem(case: CaseData, modifier: Modifier = Modifier) {
    val status      = CaseStatus.from(case.status)
    val buttonColor = when (status) {
        CaseStatus.CLOSED -> Navy500
        CaseStatus.URGENT -> Red600
        else              -> Green500
    }
    Card(
        modifier = modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = NavyWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                // Category icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Navy50)
                ) {
                    Icon(Icons.Outlined.AccountBalance, null, tint = Navy800, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text       = case.category,
                    color      = Navy700,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier   = Modifier.weight(1f),
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                // Status badge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(status.color.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(status.label, color = status.color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text       = case.title,
                color      = Navy900,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp,
                maxLines   = 2,
                overflow   = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(10.dp))
            // Meta row
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, null, tint = Navy500, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(case.caseNumber, color = Navy500, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Schedule, null, tint = Navy500, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(case.date, color = Navy500, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Grey100)
            Spacer(Modifier.height(12.dp))
            Button(
                onClick        = { },
                colors         = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape          = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(vertical = 10.dp),
                modifier       = Modifier.fillMaxWidth().height(40.dp)
            ) {
                Icon(Icons.Outlined.Visibility, null, tint = NavyWhite, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("View Details", color = NavyWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// Placeholder tab
@Composable
private fun PlaceholderTab(name: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(name, color = Navy700, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
    }
}