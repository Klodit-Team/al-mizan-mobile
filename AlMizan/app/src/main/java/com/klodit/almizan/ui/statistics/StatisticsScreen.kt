package com.klodit.almizan.ui.statistics


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klodit.almizan.viewmodel.statistics.StatisticsData
import com.klodit.almizan.viewmodel.statistics.StatisticsUiState
import com.klodit.almizan.viewmodel.statistics.StatisticsViewModel

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.min

// ─── Palette ──────────────────────────────────────────────────────────────────

private val ColorActive      = Color(0xFF1565C0)
private val ColorAttributed  = Color(0xFF2E7D32)
private val ColorExpiring    = Color(0xFFE65100)
private val ColorCancelled   = Color(0xFFC62828)
private val ColorAccent      = Color(0xFF1565C0)

private val StatusColors = mapOf(
    "PUBLIE"          to Color(0xFF1565C0),
    "EN_COURS"        to Color(0xFF0277BD),
    "OUVERTURE_PLIS"  to Color(0xFF6A1B9A),
    "EVALUATION"      to Color(0xFFF57F17),
    "ATTRIBUE"        to Color(0xFF2E7D32),
    "ANNULE"          to Color(0xFFC62828),
    "CLOTURE"         to Color(0xFF546E7A),
)

private val SectorColors = listOf(
    Color(0xFF1565C0), Color(0xFF6A1B9A), Color(0xFF00695C),
    Color(0xFFE65100), Color(0xFF4527A0), Color(0xFF558B2F),
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onBack: () -> Unit,
    vm: StatisticsViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistiques", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = vm::load) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is StatisticsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is StatisticsUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = vm::load) { Text("Réessayer") }
                    }
                }

                is StatisticsUiState.Success -> StatisticsContent(data = state.data)
            }
        }
    }
}

// ─── Main content ─────────────────────────────────────────────────────────────

@Composable
private fun StatisticsContent(data: StatisticsData) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1 — Overview cards
        item { SectionLabel("Vue d'ensemble") }
        item { OverviewCards(data) }

        // 2 — Status breakdown
        item { SectionLabel("Par statut") }
        item { StatusGrid(data.byStatus) }

        // 3 — Monthly chart
        item { SectionLabel("Publications mensuelles") }
        item { MonthlyBarChart(data.monthlyPublications) }

        // 4 — Sector chart
        item { SectionLabel("Par secteur d'activité") }
        item { HorizontalBarChart(entries = data.bySector, colors = SectorColors) }

        // 5 — Procedure donut
        item { SectionLabel("Types de procédure") }
        item { DonutChart(entries = data.byProcedureType) }

        // 6 — Wilaya highlight
        item { SectionLabel("Highlights") }
        item { HighlightCards(data) }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

// ─── Overview cards ───────────────────────────────────────────────────────────

@Composable
private fun OverviewCards(data: StatisticsData) {
    val cards = listOf(
        Triple("Actifs",       data.totalActive,      ColorActive),
        Triple("Attribués",    data.totalAttributed,  ColorAttributed),
        Triple("Expirent < 7j",data.expiringIn7Days,  ColorExpiring),
        Triple("Annulés",      data.totalCancelled,   ColorCancelled),
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        cards.forEach { (label, value, color) ->
            StatCard(
                label = label,
                value = value.toString(),
                accentColor = color,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ─── Status grid ──────────────────────────────────────────────────────────────

@Composable
private fun StatusGrid(byStatus: Map<String, Int>) {
    val entries = byStatus.entries.toList()
    val chunked = entries.chunked(3)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            chunked.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { (status, count) ->
                        val color = StatusColors[status] ?: Color.Gray
                        StatusChip(
                            label = statusLabel(status),
                            count = count,
                            color = color,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill empty slots in last row
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(count.toString(), fontWeight = FontWeight.Bold, color = color, fontSize = 18.sp)
        Text(label, fontSize = 9.sp, color = color, textAlign = TextAlign.Center, maxLines = 2)
    }
}

private fun statusLabel(key: String) = when (key) {
    "PUBLIE"         -> "Publié"
    "EN_COURS"       -> "En cours"
    "OUVERTURE_PLIS" -> "Ouv. plis"
    "EVALUATION"     -> "Évaluation"
    "ATTRIBUE"       -> "Attribué"
    "ANNULE"         -> "Annulé"
    "CLOTURE"        -> "Clôturé"
    else             -> key.lowercase().replaceFirstChar { it.uppercase() }
}

// ─── Monthly bar chart ────────────────────────────────────────────────────────

@Composable
private fun MonthlyBarChart(monthly: List<Pair<String, Int>>) {
    var animated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animated = true }
    val progress by animateFloatAsState(
        targetValue = if (animated) 1f else 0f,
        animationSpec = tween(800),
        label = "bar_anim"
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val maxVal = monthly.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1
            val barColor = ColorAccent

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                monthly.forEach { (label, count) ->
                    val fraction = (count.toFloat() / maxVal) * progress
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (count > 0) {
                            Text(
                                text = count.toString(),
                                fontSize = 9.sp,
                                color = barColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .fillMaxHeight(fraction.coerceAtLeast(0.02f))
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (count > 0) barColor else barColor.copy(alpha = 0.15f))
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                monthly.forEach { (label, _) ->
                    Text(
                        text = label,
                        fontSize = 9.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ─── Horizontal bar chart (sectors) ──────────────────────────────────────────

@Composable
private fun HorizontalBarChart(
    entries: List<Pair<String, Int>>,
    colors: List<Color>
) {
    if (entries.isEmpty()) {
        EmptyState("Aucune donnée de secteur")
        return
    }

    var animated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animated = true }
    val progress by animateFloatAsState(
        targetValue = if (animated) 1f else 0f,
        animationSpec = tween(700),
        label = "hbar_anim"
    )

    val top = entries.take(7)
    val maxVal = top.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            top.forEachIndexed { i, (label, count) ->
                val fraction = (count.toFloat() / maxVal) * progress
                val color = colors[i % colors.size]
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = count.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = color
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(color.copy(alpha = 0.15f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(color)
                        )
                    }
                }
            }
        }
    }
}

// ─── Donut chart (procedure types) ───────────────────────────────────────────

@Composable
private fun DonutChart(entries: List<Pair<String, Int>>) {
    if (entries.isEmpty()) {
        EmptyState("Aucune donnée de procédure")
        return
    }

    val total = entries.sumOf { it.second }.toFloat().coerceAtLeast(1f)
    val donutColors = SectorColors

    var animated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animated = true }
    val progress by animateFloatAsState(
        targetValue = if (animated) 1f else 0f,
        animationSpec = tween(900),
        label = "donut_anim"
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Donut
            Canvas(modifier = Modifier.size(110.dp)) {
                val stroke = Stroke(width = 22.dp.toPx(), cap = StrokeCap.Butt)
                var startAngle = -90f
                entries.forEachIndexed { i, (_, count) ->
                    val sweep = (count / total) * 360f * progress
                    drawArc(
                        color = donutColors[i % donutColors.size],
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = stroke
                    )
                    startAngle += sweep
                }
            }

            // Legend
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                entries.take(6).forEachIndexed { i, (label, count) ->
                    val pct = (count / total * 100).toInt()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(donutColors[i % donutColors.size])
                        )
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$pct%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = donutColors[i % donutColors.size]
                        )
                    }
                }
            }
        }
    }
}

// ─── Highlight cards ──────────────────────────────────────────────────────────

@Composable
private fun HighlightCards(data: StatisticsData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top wilaya
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Wilaya la + active", fontSize = 10.sp, color = Color.White.copy(alpha = 0.75f))
                Text(
                    data.topWilaya,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${data.topWilayaCount} appels d'offres",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        // Average montant
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Montant moyen estimé", fontSize = 10.sp, color = Color.White.copy(alpha = 0.75f))
                Text(
                    formatMontant(data.averageMontant),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "DA",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.8.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun EmptyState(msg: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(msg, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
    }
}

private fun formatMontant(value: Double): String {
    if (value == 0.0) return "—"
    return when {
        value >= 1_000_000_000 -> "%.1f Mrd".format(value / 1_000_000_000)
        value >= 1_000_000     -> "%.1f M".format(value / 1_000_000)
        value >= 1_000         -> "%.0f K".format(value / 1_000)
        else                   -> "%.0f".format(value)
    }
}