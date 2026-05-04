package com.klodit.almizan.ui.main

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
import android.content.res.Configuration
import com.klodit.almizan.model.*
import com.klodit.almizan.ui.bidwizard.BidStatusScreen
import com.klodit.almizan.ui.bidwizard.BidWizardScreen
import com.klodit.almizan.ui.bidwizard.EvaluationResultsScreen
import com.klodit.almizan.ui.bidwizard.FileAppealScreen
import com.klodit.almizan.ui.components.AlMizanBottomBar
import com.klodit.almizan.ui.components.AlMizanTopBar
import com.klodit.almizan.ui.components.BottomNavDestination
import com.klodit.almizan.ui.profile.DocumentsScreen
import com.klodit.almizan.ui.profile.ProfileScreen
import com.klodit.almizan.ui.profile.security.SecurityScreen
import com.klodit.almizan.ui.profile.settings.SettingsScreen
import com.klodit.almizan.ui.soumissions.MyBidsScreen
import com.klodit.almizan.ui.theme.*
import com.klodit.almizan.viewmodel.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {}
) {
    val currentRoute     by viewModel.currentRoute.collectAsState()
    val userName         by viewModel.userName.collectAsState()
    val language         by viewModel.language.collectAsState()
    val showBidWizard    by viewModel.showBidWizard.collectAsState()
    val currentBidAppelOffreId by viewModel.currentBidAppelOffreId.collectAsState()
    val showBidStatus    by viewModel.showBidStatus.collectAsState()
    val currentStatusSubmissionId by viewModel.currentStatusSubmissionId.collectAsState()
    val showEvaluationResults by viewModel.showEvaluationResults.collectAsState()
    val currentEvalSubmissionId by viewModel.currentEvalSubmissionId.collectAsState()
    val showFileAppeal by viewModel.showFileAppeal.collectAsState()
    val currentAppealSubmissionId by viewModel.currentAppealSubmissionId.collectAsState()
    val showDocuments by viewModel.showDocuments.collectAsState()
    val showSecurity by viewModel.showSecurity.collectAsState()
    val showSettings by viewModel.showSettings.collectAsState()

    // key(language) forces this to recompute every time language changes
    val localizedContext = remember(language) {
        val locale = java.util.Locale(language.locale)
        val config = android.content.res.Configuration(
            viewModel.getApplication<android.app.Application>().resources.configuration
        )
        config.setLocale(locale)
        viewModel.getApplication<android.app.Application>().createConfigurationContext(config)
    }

    // Show Bid Wizard as full-screen overlay (no top/bottom bars)
    if (showBidWizard && currentBidAppelOffreId != null) {
        BidWizardScreen(
            localizedContext = localizedContext,
            appelOffreId = currentBidAppelOffreId!!,
            onExit = { viewModel.closeBidWizard() },
            onSubmitBid = { state ->
                // TODO: Submit bid to API
                // Don't close wizard here - let BidSubmittedScreen handle it via onReturnToDashboard
            }
        )
        return
    }

    // Show Bid Status as full-screen overlay (no top/bottom bars)
    if (showBidStatus && currentStatusSubmissionId != null) {
        BidStatusScreen(
            submissionId = currentStatusSubmissionId!!,
            localizedContext = localizedContext,
            onBackClick = { viewModel.closeBidStatus() },
            onContactSupport = { /* TODO: Open support */ }
        )
        return
    }

    // Show Evaluation Results as full-screen overlay
    if (showEvaluationResults && currentEvalSubmissionId != null) {
        EvaluationResultsScreen(
            submissionId = currentEvalSubmissionId!!,
            localizedContext = localizedContext,
            onBackClick = { viewModel.closeEvaluationResults() },
            onFileAppeal = {
                viewModel.closeEvaluationResults()
                viewModel.openFileAppeal(currentEvalSubmissionId!!)
            }
        )
        return
    }

    // Show File Appeal as full-screen overlay
    if (showFileAppeal && currentAppealSubmissionId != null) {
        FileAppealScreen(
            submissionId = currentAppealSubmissionId!!,
            localizedContext = localizedContext,
            onBackClick = { viewModel.closeFileAppeal() },
            onSubmitAppeal = { appealData ->
                // TODO: Submit appeal to API
                viewModel.closeFileAppeal()
            }
        )
        return
    }

    // Show Documents as full-screen overlay
    if (showDocuments) {
        DocumentsScreen(
            localizedContext = localizedContext,
            onBackClick = { viewModel.closeDocuments() },
            onUploadClick = { /* TODO: Implement upload */ },
            onUpdateDocument = { /* TODO: Implement update */ }
        )
        return
    }

    // Show Security as full-screen overlay
    if (showSecurity) {
        SecurityScreen(
            localizedContext = localizedContext,
            onBackClick = { viewModel.closeSecurity() }
        )
        return
    }

    // Show Settings as full-screen overlay
    if (showSettings) {
        SettingsScreen(
            localizedContext = localizedContext,
            onBackClick = { viewModel.closeSettings() }
        )
        return
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
                BottomNavDestination.Search.route  -> SearchTabContent()
                BottomNavDestination.MyBids.route  -> MyBidsScreen(
                    localizedContext = localizedContext,
                    onStartBidWizard = { appelOffreId -> viewModel.openBidWizard(appelOffreId) },
                    onTrackStatus = { submissionId -> viewModel.openBidStatus(submissionId) },
                    onViewResults = { submissionId -> viewModel.openEvaluationResults(submissionId) },
                    onFileAppeal = { submissionId -> viewModel.openFileAppeal(submissionId) }
                )
                BottomNavDestination.Profile.route -> ProfileScreen(
                    localizedContext = localizedContext,
                    onEditPersonalInfo = { /* TODO: Navigate to edit profile */ },
                    onRequestOrganisationUpdate = { /* TODO: Request update */ },
                    onNavigateToDocuments = { viewModel.openDocuments() },
                    onNavigateToSecurity = { viewModel.openSecurity() },
                    onNavigateToSettings = { viewModel.openSettings() }
                )
                else                               -> PlaceholderTab("Home")
            }
        }
    }
}

@Composable
private fun SearchTabContent() {
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
                        unfocusedBorderColor    = Color(0xFFE5E7EB),
                        focusedBorderColor      = Navy800,
                        cursorColor             = Navy800
                    ),
                    modifier      = Modifier.weight(1f).height(52.dp)
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier
                        .size(52.dp)
                        .shadow(2.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(NavyWhite)
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                        .clickable { }
                ) {
                    Icon(Icons.Outlined.Tune, null, tint = Navy800, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        item {
            LazyRow(
                contentPadding        = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterTabs) { tab ->
                    FilterChipItem(label = tab, selected = selectedTab == tab, onClick = { selectedTab = tab })
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        items(visibleCases, key = { it.id }) { case ->
            CaseCardItem(case = case, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
        }
    }
}

@Composable
private fun FilterChipItem(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier         = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(if (selected) Navy800 else NavyWhite)
            .border(1.dp, if (selected) Navy800 else Color(0xFFE5E7EB), RoundedCornerShape(50.dp))
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

@Composable
private fun CaseCardItem(case: CaseData, modifier: Modifier = Modifier) {
    val status      = CaseStatus.from(case.status)
    val buttonColor = when (status) {
        CaseStatus.CLOSED -> Navy500
        CaseStatus.URGENT -> Color(0xFFE53935)
        else              -> Green500
    }
    Card(
        modifier = modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = NavyWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(Navy50)
                ) {
                    Icon(Icons.Outlined.AccountBalance, null, tint = Navy800, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text(case.category, color = Navy700, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
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
            Text(case.title, color = Navy900, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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
            HorizontalDivider(color = Color(0xFFF3F4F6))
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

@Composable
private fun PlaceholderTab(name: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(name, color = Navy700, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
    }
}