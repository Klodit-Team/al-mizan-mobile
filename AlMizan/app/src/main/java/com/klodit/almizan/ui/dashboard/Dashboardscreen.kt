package com.klodit.almizan.ui.dashboard

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klodit.almizan.model.Dashboard.CaseData
import com.klodit.almizan.model.Dashboard.CaseStatus
import com.klodit.almizan.model.Dashboard.mockDashboardData
import com.klodit.almizan.ui.search.DetailedFilterScreen
import com.klodit.almizan.ui.search.FilterState
import com.klodit.almizan.ui.theme.*

@Composable
fun DashboardScreen(
    localizedContext : Context,
    onCaseClick      : (String) -> Unit = {}
) {
    val data = remember { mockDashboardData() }

    val filterTabs = listOf("All", "Open", "In Progress", "Closed", "Urgent")
    var selectedTab   by remember { mutableStateOf("All") }
    var searchQuery   by remember { mutableStateOf("") }
    var showFilter    by remember { mutableStateOf(false) }
    var filterState   by remember { mutableStateOf(FilterState()) }

    if (showFilter) {
        DetailedFilterScreen(
            localizedContext = localizedContext,
            onApply = {
                filterState = it
                showFilter = false
            },
            onDismiss = { showFilter = false }
        )
        return
    }

    val visibleCases = data.cases.filter { case ->
        val matchesTab = selectedTab == "All" ||
                CaseStatus.from(case.status).label == selectedTab

        val matchesSearch = searchQuery.isBlank() ||
                case.title.contains(searchQuery, true) ||
                case.caseNumber.contains(searchQuery, true) ||
                case.category.contains(searchQuery, true)

        val matchesSector = filterState.selectedSectors.isEmpty() ||
                filterState.selectedSectors.any { it.equals(case.category, true) }

        val matchesStatus = filterState.selectedStatuses.isEmpty() ||
                filterState.selectedStatuses.any { it.equals(case.status, true) }

        val matchesWilaya = filterState.selectedWilayas.isEmpty() ||
                filterState.selectedWilayas.any { it.equals(case.wilaya, true) }

        matchesTab && matchesSearch && matchesSector && matchesStatus && matchesWilaya
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy50)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {

            item {
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text("Search tenders…", color = Navy500, fontSize = 14.sp)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, null, tint = Navy500)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = NavyWhite,
                            focusedContainerColor = NavyWhite,
                            unfocusedBorderColor = Navy100,
                            focusedBorderColor = Navy800,
                            cursorColor = Navy800
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    )

                    val hasFilters = filterState != FilterState()

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (hasFilters) Green500 else NavyWhite)
                            .border(
                                1.dp,
                                if (hasFilters) Green500 else Navy100,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { showFilter = true }
                    ) {
                        Icon(
                            Icons.Outlined.Tune,
                            null,
                            tint = if (hasFilters) NavyWhite else Navy800
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterTabs) { tab ->
                        DashboardFilterChip(
                            label = tab,
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            if (visibleCases.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No tenders match your filters", color = Navy500)
                    }
                }
            } else {
                items(visibleCases) { case ->
                    DashboardCaseCard(
                        case = case,
                        onClick = { onCaseClick(case.id) },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) Navy800 else NavyWhite)
            .border(1.dp, if (selected) Navy800 else Navy100, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            label,
            color = if (selected) NavyWhite else Navy700,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun DashboardCaseCard(
    case: CaseData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = CaseStatus.from(case.status)

    val buttonColor = when (status) {
        CaseStatus.CLOSED -> Navy500
        CaseStatus.URGENT -> Red600
        else -> Green500
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyWhite)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AccountBalance, null, tint = Navy800)
                Spacer(Modifier.width(8.dp))

                Text(
                    case.category,
                    modifier = Modifier.weight(1f),
                    color = Navy700,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(status.label, color = status.color)
            }

            Spacer(Modifier.height(10.dp))

            Text(case.title, color = Navy900, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(12.dp))

            // Case number and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.LocationOn, null, tint = Navy500, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(case.wilaya, color = Navy500, fontSize = 12.sp, maxLines = 1)
                }

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Schedule, null, tint = Navy500, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(case.date, color = Navy500, fontSize = 12.sp, maxLines = 1)
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Details", color = NavyWhite)
            }

        }
    }
}