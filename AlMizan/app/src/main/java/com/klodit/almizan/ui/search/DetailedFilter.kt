package com.klodit.almizan.ui.search

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klodit.almizan.R
import com.klodit.almizan.ui.theme.*

@Composable
fun DetailedFilterScreen(
    localizedContext : Context,
    localeTag        : String  = "",
    resultCount      : Int     = 0,
    onApply          : (FilterState) -> Unit = {},
    onDismiss        : () -> Unit            = {}
) {
    val c = localizedContext

    val sectors = remember(localeTag) {
        listOf(
            c.getString(R.string.sector_construction),
            c.getString(R.string.sector_it),
            c.getString(R.string.sector_health),
            c.getString(R.string.sector_education),
            c.getString(R.string.sector_energy),
            c.getString(R.string.sector_transport)
        )
    }
    val procedureTypes = remember(localeTag) {
        listOf(
            c.getString(R.string.proc_open),
            c.getString(R.string.proc_restricted),
            c.getString(R.string.proc_negotiated),
            c.getString(R.string.proc_direct),
            c.getString(R.string.proc_competitive)
        )
    }
    val statuses = remember(localeTag) {
        listOf(
            c.getString(R.string.status_open),
            c.getString(R.string.status_closed),
            c.getString(R.string.status_evaluation),
            c.getString(R.string.status_awarded)
        )
    }
    val wilayas = listOf(
        "Alger", "Oran", "Constantine", "Annaba", "Blida", "Batna",
        "Setif", "Sidi Bel Abbes", "Biskra", "Tebessa", "Tlemcen",
        "Bejaia", "Medea", "Mostaganem", "Ouargla", "Tizi Ouzou",
        "Jijel", "Skikda", "Souk Ahras", "El Oued", "Ghardaia",
        "Bechar", "Adrar", "Tamanrasset", "Illizi"
    )

    var filterState       by remember { mutableStateOf(FilterState()) }
    var procedureExpanded by remember { mutableStateOf(false) }
    var wilayaExpanded    by remember { mutableStateOf(false) }
    var wilayaQuery       by remember { mutableStateOf("") }

    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker   by remember { mutableStateOf(false) }

    val filteredWilayas = remember(wilayaQuery) {
        if (wilayaQuery.isBlank()) wilayas
        else wilayas.filter { it.contains(wilayaQuery, ignoreCase = true) }
    }

    if (showFromPicker) {
        DatePickerModal(
            onDateSelected = { date ->
                filterState   = filterState.copy(deadlineFrom = date)
                showFromPicker = false
            },
            onDismiss = { showFromPicker = false }
        )
    }

    if (showToPicker) {
        DatePickerModal(
            onDateSelected = { date ->
                filterState = filterState.copy(deadlineTo = date)
                showToPicker = false
            },
            onDismiss = { showToPicker = false }
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Grey50)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyWhite)
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = null,
                tint     = Navy800,
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onDismiss() }
            )
            Spacer(Modifier.width(12.dp))
            Icon(
                Icons.Default.Category,
                contentDescription = null,
                tint     = Navy800,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(6.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text       = c.getString(R.string.filter_title),
                    color      = Navy800,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Text(
                    text     = c.getString(R.string.filter_market_sector),
                    color    = Navy500,
                    fontSize = 12.sp
                )
            }
            Text(
                text       = c.getString(R.string.filter_reset),
                color      = Green500,
                fontSize   = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier   = Modifier
                    .clickable { filterState = FilterState() }
                    .padding(horizontal = 4.dp)
            )
        }
        HorizontalDivider(color = Navy100, thickness = 1.dp)

        LazyColumn(
            modifier       = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            // 1. Market Sector
            item {
                DetailSection(label = c.getString(R.string.filter_market_sector)) {
                    SectorChipGrid(
                        options  = sectors,
                        selected = filterState.selectedSectors,
                        onToggle = {
                            filterState = filterState.copy(
                                selectedSectors = filterState.selectedSectors.toggle(it)
                            )
                        }
                    )
                }
                DetailSectionDivider()
            }

            // 2. Procedure Type
            item {
                DetailSection(label = c.getString(R.string.filter_procedure_type)) {
                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (filterState.procedureType != null) Green50 else Navy30)
                                .border(
                                    width = 1.dp,
                                    color = if (filterState.procedureType != null) Green500 else Navy100,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { procedureExpanded = !procedureExpanded }
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text     = filterState.procedureType ?: c.getString(R.string.filter_procedure_type),
                                color    = if (filterState.procedureType != null) Green500 else Navy300,
                                fontSize = 14.sp
                            )
                            Icon(
                                imageVector        = if (procedureExpanded) Icons.Default.KeyboardArrowUp
                                else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint               = if (filterState.procedureType != null) Green500 else Navy300,
                                modifier           = Modifier.size(20.dp)
                            )
                        }
                        DropdownMenu(
                            expanded         = procedureExpanded,
                            onDismissRequest = { procedureExpanded = false },
                            containerColor   = NavyWhite
                        ) {
                            procedureTypes.forEach { type ->
                                val sel = type == filterState.procedureType
                                DropdownMenuItem(
                                    text    = {
                                        Text(
                                            text       = type,
                                            fontSize   = 14.sp,
                                            color      = if (sel) Green500 else Navy800,
                                            fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        filterState       = filterState.copy(procedureType = type)
                                        procedureExpanded = false
                                    },
                                    trailingIcon = if (sel) ({
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint     = Green500,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }) else null,
                                    colors = MenuItemColors(
                                        textColor         = Navy800,
                                        leadingIconColor  = Navy800,
                                        trailingIconColor = Green500,
                                        disabledTextColor         = Navy300,
                                        disabledLeadingIconColor  = Navy300,
                                        disabledTrailingIconColor = Navy300
                                    )
                                )
                            }
                        }
                    }
                }
                DetailSectionDivider()
            }

            // 3. Wilaya
            item {
                DetailSection(label = c.getString(R.string.filter_wilaya_search)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Navy30)
                            .border(1.dp, Navy100, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Navy300, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        BasicTextField(
                            value         = wilayaQuery,
                            onValueChange = {
                                wilayaQuery = it
                                if (it.isNotEmpty()) wilayaExpanded = true
                            },
                            modifier      = Modifier.weight(1f),
                            textStyle     = TextStyle(color = Navy800, fontSize = 14.sp),
                            singleLine    = true,
                            decorationBox = { inner ->
                                if (wilayaQuery.isEmpty()) {
                                    Text("Search wilayas...", color = Navy300, fontSize = 14.sp)
                                }
                                inner()
                            }
                        )
                        if (wilayaQuery.isNotEmpty()) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                tint     = Navy800,
                                modifier = Modifier.size(16.dp).clickable { wilayaQuery = "" }
                            )
                        }
                    }

                    if (filterState.selectedWilayas.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier              = Modifier.fillMaxWidth()
                        ) {
                            filterState.selectedWilayas.take(3).forEach { w ->
                                WilayaChip(
                                    label    = w,
                                    onRemove = {
                                        filterState = filterState.copy(
                                            selectedWilayas = filterState.selectedWilayas - w
                                        )
                                    }
                                )
                            }
                            if (filterState.selectedWilayas.size > 3) {
                                Text(
                                    text     = "+${filterState.selectedWilayas.size - 3}",
                                    color    = Green500,
                                    fontSize = 12.sp,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }
                    }

                    AnimatedVisibility(wilayaExpanded) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Navy100, RoundedCornerShape(8.dp))
                                .background(NavyWhite)
                        ) {
                            filteredWilayas.take(8).forEachIndexed { index, wilaya ->
                                val isSelected = wilaya in filterState.selectedWilayas
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            filterState = filterState.copy(
                                                selectedWilayas = filterState.selectedWilayas.toggle(wilaya)
                                            )
                                        }
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    Text(wilaya, fontSize = 14.sp, color = Navy800)
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Green500, modifier = Modifier.size(16.dp))
                                    }
                                }
                                if (index < filteredWilayas.take(8).lastIndex) {
                                    HorizontalDivider(color = Grey100, thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }
                DetailSectionDivider()
            }

            // 4. Tender Status — same chip style as Market Sector
            item {
                DetailSection(label = c.getString(R.string.filter_tender_status)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        statuses.chunked(3).forEach { row ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { status ->
                                    val sel = status in filterState.selectedStatuses
                                    Text(
                                        text       = status,
                                        color      = if (sel) NavyWhite else Navy700,
                                        fontSize   = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines   = 1,
                                        overflow   = TextOverflow.Ellipsis,
                                        modifier   = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (sel) Green500 else Navy30)
                                            .border(
                                                width = 1.dp,
                                                color = if (sel) Green500 else Navy100,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                filterState = filterState.copy(
                                                    selectedStatuses = filterState.selectedStatuses.toggle(status)
                                                )
                                            }
                                            .padding(horizontal = 12.dp, vertical = 9.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                DetailSectionDivider()
            }

            // 5. Bank Guarantee
            item {
                DetailSection(label = "Bank Guarantee") {
                    Row(
                        modifier          = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Green50),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Green500, modifier = Modifier.size(22.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Bank Guarantee", color = Navy800, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text("Required for this tender", color = Navy500, fontSize = 12.sp)
                        }
                        Switch(
                            checked         = filterState.bankGuarantee,
                            onCheckedChange = { filterState = filterState.copy(bankGuarantee = it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor   = NavyWhite,
                                checkedTrackColor   = Green500,
                                uncheckedThumbColor = NavyWhite,
                                uncheckedTrackColor = Navy100
                            )
                        )
                    }
                }
                DetailSectionDivider()
            }

            // 6. Date Range
            item {
                DetailSection(label = "Date Range") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier              = Modifier.fillMaxWidth()
                    ) {
                        DateInputBox(
                            label       = "FROM",
                            value       = filterState.deadlineFrom,
                            placeholder = "mm / dd / yyyy",
                            modifier    = Modifier.weight(1f),
                            onClick     = { showFromPicker = true }
                        )
                        DateInputBox(
                            label       = "TO",
                            value       = filterState.deadlineTo,
                            placeholder = "mm / dd / yyyy",
                            modifier    = Modifier.weight(1f),
                            onClick     = { showToPicker = true }
                        )
                    }
                }
                DetailSectionDivider()
            }

            item { Spacer(Modifier.height(80.dp)) }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .background(NavyWhite)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Button(
                onClick  = { onApply(filterState) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green500),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Text("Show Results", color = NavyWhite, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = NavyWhite, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    onDateSelected : (String) -> Unit,
    onDismiss      : () -> Unit
) {
    val state = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton    = {
            TextButton(onClick = {
                val millis = state.selectedDateMillis
                if (millis != null) {
                    val cal = java.util.Calendar.getInstance().apply { timeInMillis = millis }
                    val formatted = "%02d / %02d / %04d".format(
                        cal.get(java.util.Calendar.MONTH) + 1,
                        cal.get(java.util.Calendar.DAY_OF_MONTH),
                        cal.get(java.util.Calendar.YEAR)
                    )
                    onDateSelected(formatted)
                } else {
                    onDismiss()
                }
            }) {
                Text("OK", color = Green500)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Navy500)
            }
        }
    ) {
        DatePicker(state = state)
    }
}

@Composable
private fun DetailSection(
    label   : String,
    content : @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyWhite)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(text = label, color = Navy800, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun SectorChipGrid(
    options  : List<String>,
    selected : Set<String>,
    onToggle : (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { item ->
                    val sel = item in selected
                    Text(
                        text       = item,
                        color      = if (sel) NavyWhite else Navy700,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis,
                        modifier   = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (sel) Green500 else Navy30)
                            .border(1.dp, if (sel) Green500 else Navy100, RoundedCornerShape(8.dp))
                            .clickable { onToggle(item) }
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WilayaChip(label: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Grey100)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 11.sp, color = Navy800)
        Spacer(Modifier.width(4.dp))
        Icon(
            Icons.Default.Close,
            contentDescription = null,
            tint     = Navy800,
            modifier = Modifier.size(12.dp).clickable { onRemove() }
        )
    }
}

@Composable
private fun DateInputBox(
    label       : String,
    value       : String,
    placeholder : String,
    modifier    : Modifier = Modifier,
    onClick     : () -> Unit
) {
    Column(modifier = modifier) {
        Text(text = label, color = Navy500, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Navy30)
                .border(1.dp, Navy100, RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 13.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text     = value.ifBlank { placeholder },
                color    = if (value.isBlank()) Navy300 else Navy800,
                fontSize = 13.sp
            )
            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Navy300, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun DetailSectionDivider() {
    Spacer(
        Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(Grey50)
    )
}

private fun Set<String>.toggle(item: String) =
    if (contains(item)) this - item else this + item