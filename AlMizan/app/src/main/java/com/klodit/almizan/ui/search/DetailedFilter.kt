package com.klodit.almizan.ui.search

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klodit.almizan.model.tender.Tender

// ─── Colors ───────────────────────────────────────────────────────────────────
private val NavyDark    = Color(0xFF364150)
private val SlateGrey   = Color(0xFF475569)
private val Green500    = Color(0xFF4CAF50)
private val Grey50      = Color(0xFFF8F6F6)
private val GreyBg      = Color(0xFFF8FAFC)
private val BorderGrey  = Color(0xFFE2E8F0)
private val DividerGrey = Color(0xFFF1F5F9)
private val White       = Color(0xFFFFFFFF)

// ─── FilterState ──────────────────────────────────────────────────────────────
data class FilterState(
    val selectedSectors   : Set<String> = emptySet(),
    val selectedStatuses  : Set<String> = emptySet(),
    val selectedWilayas   : Set<String> = emptySet(),
    val selectedProcedures: Set<String> = emptySet(),
    val dateFrom          : String?     = null,
    val dateTo            : String?     = null
)

fun <T> Set<T>.toggle(item: T): Set<T> =
    if (contains(item)) this - item else this + item

// ─── Derive distinct filter options from live tender data ─────────────────────
fun deriveFilterOptions(tenders: List<Tender>) = Triple(
    tenders.map { it.secteurActivite }.distinct().sorted(),
    tenders.map { it.statut          }.distinct().sorted(),
    tenders.map { it.wilaya          }.distinct().sorted()
)

// ─── Main composable ──────────────────────────────────────────────────────────
@Composable
fun DetailedFilterScreen(
    localizedContext : Context,
    tenders          : List<Tender> = emptyList(),
    filterState      : FilterState  = FilterState(),
    localeTag        : String       = "",
    onApply          : (FilterState) -> Unit = {},
    onDismiss        : () -> Unit            = {}
) {
    var state by remember { mutableStateOf(filterState) }

    val (dynamicSectors, dynamicStatuses, dynamicWilayas) = remember(tenders) {
        deriveFilterOptions(tenders)
    }

    // Fallback static lists used while tenders are loading
    val fallbackSectors  = listOf("Construction", "IT & Digital", "Health", "Education", "Energy", "Transport")
    val fallbackStatuses = listOf("PUBLIE", "ANNULE", "EN_EVALUATION", "ATTRIBUE")
    val fallbackWilayas  = listOf(
        "Alger", "Oran", "Constantine", "Annaba", "Blida", "Batna",
        "Sétif", "Sidi Bel Abbès", "Biskra", "Tébessa", "Tlemcen",
        "Béjaïa", "Médéa", "Mostaganem", "Ouargla", "Tizi Ouzou"
    )

    val sectors  = if (dynamicSectors.isNotEmpty())  dynamicSectors  else fallbackSectors
    val statuses = if (dynamicStatuses.isNotEmpty()) dynamicStatuses else fallbackStatuses
    val wilayas  = if (dynamicWilayas.isNotEmpty())  dynamicWilayas  else fallbackWilayas

    var wilayaQuery by remember { mutableStateOf("") }
    val filteredWilayas = remember(wilayaQuery, wilayas) {
        if (wilayaQuery.isBlank()) wilayas
        else wilayas.filter { it.contains(wilayaQuery, ignoreCase = true) }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Grey50)
    ) {

        // ── HEADER ────────────────────────────────────────────────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .background(White)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector        = Icons.Outlined.Category,
                contentDescription = null,
                tint               = NavyDark,
                modifier           = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text       = "Filters",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp,
                color      = NavyDark,
                modifier   = Modifier.weight(1f)
            )
            Text(
                text     = "Reset",
                color    = Green500,
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable { state = FilterState() }
                    .padding(end = 12.dp)
            )
            Icon(
                imageVector        = Icons.Default.Close,
                contentDescription = "Close",
                tint               = NavyDark,
                modifier           = Modifier
                    .size(22.dp)
                    .clickable { onDismiss() }
            )
        }

        // ── BODY ──────────────────────────────────────────────────────────────
        LazyColumn(
            modifier       = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {

            item {
                FilterSection(title = "Sectors") {
                    ChipGroup(
                        items    = sectors,
                        selected = state.selectedSectors,
                        onToggle = { state = state.copy(selectedSectors = state.selectedSectors.toggle(it)) }
                    )
                }
                HorizontalDivider(color = DividerGrey)
            }

            item {
                FilterSection(title = "Status") {
                    ChipGroup(
                        items    = statuses,
                        selected = state.selectedStatuses,
                        onToggle = { state = state.copy(selectedStatuses = state.selectedStatuses.toggle(it)) }
                    )
                }
                HorizontalDivider(color = DividerGrey)
            }

            item {
                FilterSection(title = "Wilaya") {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(GreyBg)
                            .border(1.dp, BorderGrey, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, null, tint = SlateGrey, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        BasicTextField(
                            value         = wilayaQuery,
                            onValueChange = { wilayaQuery = it },
                            singleLine    = true,
                            cursorBrush   = SolidColor(Green500),
                            textStyle     = TextStyle(color = NavyDark, fontSize = 14.sp),
                            modifier      = Modifier.fillMaxWidth(),
                            decorationBox = { inner ->
                                if (wilayaQuery.isEmpty()) {
                                    Text("Search wilaya…", color = SlateGrey, fontSize = 14.sp)
                                }
                                inner()
                            }
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    ChipGroup(
                        items    = filteredWilayas,
                        selected = state.selectedWilayas,
                        onToggle = { state = state.copy(selectedWilayas = state.selectedWilayas.toggle(it)) }
                    )
                }
                HorizontalDivider(color = DividerGrey)
            }

            item {
                FilterSection(title = "Date Range") {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DateChipField(
                            label          = "From",
                            value          = state.dateFrom,
                            modifier       = Modifier.weight(1f),
                            onDateSelected = { state = state.copy(dateFrom = it) }
                        )
                        DateChipField(
                            label          = "To",
                            value          = state.dateTo,
                            modifier       = Modifier.weight(1f),
                            onDateSelected = { state = state.copy(dateTo = it) }
                        )
                    }
                }
            }
        }

        // ── APPLY ─────────────────────────────────────────────────────────────
        Surface(shadowElevation = 8.dp, color = White) {
            Button(
                onClick  = { onApply(state) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(52.dp),
                shape  = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green500)
            ) {
                Text("Apply Filters", color = White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }
    }
}

// ─── Section wrapper ──────────────────────────────────────────────────────────
@Composable
private fun FilterSection(
    title   : String,
    content : @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text          = title.uppercase(),
            fontWeight    = FontWeight.SemiBold,
            fontSize      = 11.sp,
            color         = SlateGrey,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(12.dp))
        content()
    }
}

// ─── Chip group ───────────────────────────────────────────────────────────────
@Composable
private fun ChipGroup(
    items    : List<String>,
    selected : Set<String>,
    onToggle : (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(3).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { item ->
                    FilterChip(
                        label    = item,
                        selected = item in selected,
                        onClick  = { onToggle(item) }
                    )
                }
            }
        }
    }
}

// ─── Single chip ──────────────────────────────────────────────────────────────
@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Green500 else GreyBg)
            .border(1.dp, if (selected) Green500 else BorderGrey, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Text(
            text       = label,
            color      = if (selected) White else SlateGrey,
            fontSize   = 13.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            maxLines   = 1
        )
    }
}

// ─── Date field ───────────────────────────────────────────────────────────────
@Composable
private fun DateChipField(
    label          : String,
    value          : String?,
    modifier       : Modifier = Modifier,
    onDateSelected : (String) -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (value != null) Green500 else GreyBg)
            .border(1.dp, if (value != null) Green500 else BorderGrey, RoundedCornerShape(8.dp))
            .clickable { /* TODO: show DatePickerDialog, then call onDateSelected("YYYY-MM-DD") */ }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text     = value ?: label,
            color    = if (value != null) White else SlateGrey,
            fontSize = 13.sp
        )
    }
}