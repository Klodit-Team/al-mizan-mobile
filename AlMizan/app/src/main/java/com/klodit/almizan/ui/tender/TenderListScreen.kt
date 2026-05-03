package com.klodit.almizan.ui.tender

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klodit.almizan.model.tender.Tender
import com.klodit.almizan.ui.theme.*
import com.klodit.almizan.viewmodel.TenderViewModel

@Composable
fun TenderListScreen(
    innerPadding       : PaddingValues,
    localizedContext   : Context,
    onNavigateToFilter : () -> Unit,
    viewModel          : TenderViewModel = viewModel()
) {
    val tenders   by viewModel.tenders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error     by viewModel.error.collectAsState()

    val filterTabs = listOf("All", "PUBLIE", "ANNULE")
    var selectedTab  by remember { mutableStateOf("All") }
    var searchQuery  by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.fetchTenders() }

    val visible = tenders.filter { tender ->
        val matchesTab    = selectedTab == "All" || tender.statut == selectedTab
        val matchesSearch = searchQuery.isBlank() ||
                tender.objet.contains(searchQuery, ignoreCase = true) ||
                tender.reference.contains(searchQuery, ignoreCase = true) ||
                tender.wilaya.contains(searchQuery, ignoreCase = true) ||
                tender.secteurActivite.contains(searchQuery, ignoreCase = true)
        matchesTab && matchesSearch
    }

    LazyColumn(
        modifier       = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // ── Search bar + filter button ────────────────────────────────────
        item {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value         = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder   = { Text("Search tenders…", color = Navy500, fontSize = 14.sp) },
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
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                )
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

        // ── Status filter chips ───────────────────────────────────────────
        item {
            LazyRow(
                contentPadding        = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterTabs) { tab ->
                    TenderFilterChip(
                        label    = tab,
                        selected = selectedTab == tab,
                        onClick  = { selectedTab = tab }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // ── Loading ───────────────────────────────────────────────────────
        if (isLoading) {
            item {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Navy800)
                }
            }
        }

        // ── Error ─────────────────────────────────────────────────────────
        error?.let { msg ->
            item {
                Text(
                    text     = "Failed to load tenders: $msg",
                    color    = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // ── Empty state ───────────────────────────────────────────────────
        if (!isLoading && error == null && visible.isEmpty()) {
            item {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .padding(top = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tenders found.", color = Navy500, fontSize = 14.sp)
                }
            }
        }

        // ── Tender cards ──────────────────────────────────────────────────
        items(visible, key = { it.id }) { tender ->
            TenderCard(
                tender   = tender,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}

// ── Filter chip ───────────────────────────────────────────────────────────────

@Composable
private fun TenderFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
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

// ── Tender card ───────────────────────────────────────────────────────────────

@Composable
fun TenderCard(tender: Tender, modifier: Modifier = Modifier) {
    val statusColor = when (tender.statut) {
        "PUBLIE" -> Green500
        "ANNULE" -> Navy500
        else     -> Navy500
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Header row: sector icon + status badge ────────────────────
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Navy50)
                ) {
                    Icon(
                        Icons.Outlined.AccountBalance,
                        contentDescription = null,
                        tint               = Navy800,
                        modifier           = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text       = tender.secteurActivite,
                    color      = Navy700,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier   = Modifier.weight(1f),
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text       = tender.statut,
                        color      = statusColor,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // ── Tender title ──────────────────────────────────────────────
            Text(
                text       = tender.objet,
                color      = Navy900,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp,
                maxLines   = 2,
                overflow   = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            // ── Reference ─────────────────────────────────────────────────
            Text(
                text     = tender.reference,
                color    = Navy500,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(10.dp))

            // ── Meta row: wilaya + deadline ───────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint               = Navy500,
                        modifier           = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(tender.wilaya, color = Navy500, fontSize = 12.sp)
                }
                tender.dateLimiteSoumission?.let { date ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint               = Navy500,
                            modifier           = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(date.take(10), color = Navy500, fontSize = 12.sp)
                    }
                }
            }

            // ── Lots badge ────────────────────────────────────────────────
            if (tender.lots.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text     = "${tender.lots.size} lot(s)",
                    color    = Navy500,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = Grey100)
            Spacer(Modifier.height(12.dp))

            // ── Action button ─────────────────────────────────────────────
            Button(
                onClick        = { /* TODO: navigate to tender detail */ },
                colors         = ButtonDefaults.buttonColors(containerColor = statusColor),
                shape          = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(vertical = 10.dp),
                modifier       = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                Icon(
                    Icons.Outlined.Visibility,
                    contentDescription = null,
                    tint               = NavyWhite,
                    modifier           = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "View Details",
                    color      = NavyWhite,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}