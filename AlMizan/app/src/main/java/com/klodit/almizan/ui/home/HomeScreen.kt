package com.klodit.almizan.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klodit.almizan.model.tender.Tender
import com.klodit.almizan.ui.theme.*
import com.klodit.almizan.viewmodel.HomeStats
import com.klodit.almizan.viewmodel.HomeUiState
import com.klodit.almizan.viewmodel.HomeViewModel
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

// ─── Entry point ──────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    onTenderClick: (String) -> Unit = {},
    onViewAllClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var searchQuery    by remember { mutableStateOf("") }
    var selectedSecteur by remember { mutableStateOf("Secteur") }
    var selectedWilaya  by remember { mutableStateOf("Wilaya") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy50)
            .padding(innerPadding),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {

        // ── Hero ──────────────────────────────────────────────────────────
        item {
            HeroSection(
                searchQuery     = searchQuery,
                onSearchChange  = { searchQuery = it },
                selectedSecteur = selectedSecteur,
                onSecteurChange = { selectedSecteur = it },
                selectedWilaya  = selectedWilaya,
                onWilayaChange  = { selectedWilaya = it },
                onSearch        = onViewAllClick
            )
        }

        // ── Stats bar ─────────────────────────────────────────────────────
        item {
            when (uiState) {
                is HomeUiState.Loading ->
                    StatsBar(stats = null, isLoading = true)
                is HomeUiState.Success ->
                    StatsBar(stats = (uiState as HomeUiState.Success).stats, isLoading = false)
                is HomeUiState.Error ->
                    StatsBar(stats = null, isLoading = false)
            }
        }

        // ── Explore platform ──────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            ExplorePlatformSection()
        }

        // ── Latest tenders header ─────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "Latest Tenders",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Navy900
                )
                TextButton(onClick = onViewAllClick) {
                    Text(
                        "View All →",
                        color      = Green500,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // ── Tender cards ──────────────────────────────────────────────────
        when (uiState) {
            is HomeUiState.Loading -> {
                items(3) { TenderCardSkeleton() }
            }
            is HomeUiState.Success -> {
                val tenders = (uiState as HomeUiState.Success).latestTenders
                if (tenders.isEmpty()) {
                    item { EmptyTendersPlaceholder() }
                } else {
                    items(tenders, key = { it.id }) { tender ->
                        TenderCard(tender = tender, onClick = { onTenderClick(tender.id) })
                    }
                }
            }
            is HomeUiState.Error -> {
                item {
                    ErrorCard(
                        message = (uiState as HomeUiState.Error).message,
                        onRetry = { viewModel.loadHomeData() }
                    )
                }
            }
        }

        // ── Pillars ───────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(32.dp))
            PillarsSection()
            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─── Hero Section ─────────────────────────────────────────────────────────────

@Composable
private fun HeroSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedSecteur: String,
    onSecteurChange: (String) -> Unit,
    selectedWilaya: String,
    onWilayaChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Navy800)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 32.dp)
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = NavyWhite)) { append("Le Portail Souverain\ndes ") }
                    withStyle(SpanStyle(color = Green500))  { append("Marchés Publics") }
                    withStyle(SpanStyle(color = NavyWhite)) { append(" en\nAlgérie") }
                },
                fontSize   = 26.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Accédez à l'ensemble des opportunités d'affaires publiques en Algérie sur une plateforme sécurisée, transparente et centralisée.",
                fontSize   = 13.sp,
                color      = Navy300,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(24.dp))

            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = NavyWhite),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value         = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder   = {
                            Text("Rechercher un marché...", color = Navy400, fontSize = 14.sp)
                        },
                        leadingIcon = {
                            Icon(Icons.Outlined.Search, contentDescription = null, tint = Navy400)
                        },
                        modifier    = Modifier.fillMaxWidth(),
                        shape       = RoundedCornerShape(8.dp),
                        singleLine  = true,
                        colors      = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Navy100,
                            focusedBorderColor   = Green500,
                            cursorColor          = Green500
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DropdownChip(
                            label    = selectedSecteur,
                            modifier = Modifier.weight(1f),
                            onClick  = { /* open secteur bottom sheet */ }
                        )
                        DropdownChip(
                            label    = selectedWilaya,
                            modifier = Modifier.weight(1f),
                            onClick  = { /* open wilaya bottom sheet */ }
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick  = onSearch,
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(8.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = Green500)
                    ) {
                        Text(
                            "Rechercher →",
                            color      = NavyWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 15.sp,
                            modifier   = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DropdownChip(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick          = onClick,
        modifier         = modifier.height(48.dp),
        shape            = RoundedCornerShape(8.dp),
        contentPadding   = PaddingValues(horizontal = 12.dp),
        colors           = ButtonDefaults.outlinedButtonColors(contentColor = Navy700),
        border           = ButtonDefaults.outlinedButtonBorder
    ) {
        Text(label, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp))
    }
}

// ─── Stats Bar ────────────────────────────────────────────────────────────────

@Composable
private fun StatsBar(stats: HomeStats?, isLoading: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Green500)
            .padding(vertical = 20.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            StatItem(
                value     = if (isLoading) "…" else "${stats?.activeTenders ?: 0}",
                label     = "ACTIVE TENDERS",
                icon      = Icons.Outlined.Description
            )
            StatDivider()
            StatItem(
                value     = if (isLoading) "…" else "${stats?.awarded ?: 0}",
                label     = "AWARDED",
                icon      = Icons.Outlined.CheckCircle
            )
            StatDivider()
            StatItem(
                value     = if (isLoading) "…" else "${stats?.total ?: 0}",
                label     = "TOTAL AOs",
                icon      = Icons.Outlined.Groups
            )
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = NavyWhite, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = NavyWhite)
        Text(label, fontSize = 10.sp, color = Color(0xCCFFFFFF), letterSpacing = 0.5.sp)
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .height(40.dp)
            .width(1.dp)
            .background(Color(0x33FFFFFF))
    )
}

// ─── Explore Platform ─────────────────────────────────────────────────────────

private data class PlatformShortcut(val icon: ImageVector, val title: String, val subtitle: String)

@Composable
private fun ExplorePlatformSection() {
    val shortcuts = listOf(
        PlatformShortcut(Icons.Outlined.BarChart,      "Market Stats",  "View insights"),
        PlatformShortcut(Icons.Outlined.Gavel,         "Legal Info",    "Law 23-12 & terms"),
        PlatformShortcut(Icons.Outlined.SupportAgent,  "Help Center",   "24/7 assistance")
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            "Explore Platform",
            fontSize   = 18.sp,
            fontWeight = FontWeight.Bold,
            color      = Navy900
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            shortcuts.forEach { shortcut ->
                ShortcutCard(shortcut = shortcut, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ShortcutCard(shortcut: PlatformShortcut, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier.clickable { },
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = NavyWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier             = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment  = Alignment.CenterHorizontally
        ) {
            Box(
                modifier        = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Green50),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    shortcut.icon,
                    contentDescription = null,
                    tint               = Green600,
                    modifier           = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                shortcut.title,
                fontSize   = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color      = Navy900,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                shortcut.subtitle,
                fontSize = 10.sp,
                color    = Navy500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ─── Tender Card ──────────────────────────────────────────────────────────────

@Composable
fun TenderCard(tender: Tender, onClick: () -> Unit) {
    // Compute days left from dateLimiteSoumission
    val daysLeft = remember(tender.dateLimiteSoumission) {
        try {
            val deadline = OffsetDateTime.parse(tender.dateLimiteSoumission)
            ChronoUnit.DAYS.between(OffsetDateTime.now(), deadline).toInt().coerceAtLeast(0)
        } catch (e: Exception) { -1 }
    }

    val deadlineLabel = when {
        daysLeft < 0  -> "Date inconnue"
        daysLeft == 0 -> "Aujourd'hui"
        daysLeft == 1 -> "Demain"
        else          -> "Dans $daysLeft jours"
    }

    val deadlineColor = when {
        daysLeft in 0..7  -> Red600
        daysLeft in 8..14 -> Orange400
        else              -> Green600
    }

    // Derive scope from typeProcedure or wilaya — use wilaya as display location
    val scopeLabel = tender.typeProcedure
        .replace("_", " ")
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }

    val scopeColor = when (tender.typeProcedure.uppercase()) {
        "AO_OUVERT"    -> Blue800
        "AO_RESTREINT" -> Color(0xFF6A1B9A)
        "CONCOURS"     -> Color(0xFF00838F)
        "GRE_A_GRE"   -> Orange400
        else           -> Navy700
    }
    val scopeBg = when (tender.typeProcedure.uppercase()) {
        "AO_OUVERT"    -> Blue50
        "AO_RESTREINT" -> Color(0xFFF3E5F5)
        "CONCOURS"     -> Color(0xFFE0F7FA)
        "GRE_A_GRE"   -> Color(0xFFFFF8E1)
        else           -> Navy50
    }

    // Format publication date
    val dateLabel = remember(tender.datePublication) {
        try {
            val dt = OffsetDateTime.parse(tender.datePublication)
            "${dt.dayOfMonth.toString().padStart(2,'0')}/${dt.monthValue.toString().padStart(2,'0')}/${dt.year}"
        } catch (e: Exception) {
            tender.createdAt.take(10).replace("-", "/").let {
                if (it.length == 10) "${it.substring(8)}/${it.substring(5,7)}/${it.substring(0,4)}" else "—"
            }
        }
    }

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = NavyWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Top row: type badge + date
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(scopeBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(scopeLabel, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = scopeColor)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CalendarToday, contentDescription = null, modifier = Modifier.size(12.dp), tint = Navy400)
                    Spacer(Modifier.width(4.dp))
                    Text(dateLabel, fontSize = 11.sp, color = Navy400)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Wilaya + secteur
            Text(
                "${tender.wilaya.uppercase()} · ${tender.secteurActivite}",
                fontSize     = 10.sp,
                fontWeight   = FontWeight.Medium,
                color        = Navy500,
                letterSpacing = 0.3.sp,
                maxLines     = 1,
                overflow     = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            // Object / title
            Text(
                tender.objet,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = Navy900,
                lineHeight = 20.sp,
                maxLines   = 3,
                overflow   = TextOverflow.Ellipsis
            )

            // Reference tag
            Spacer(Modifier.height(6.dp))
            Text(
                tender.reference,
                fontSize = 11.sp,
                color    = Navy400
            )

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Grey100)
            Spacer(Modifier.height(10.dp))

            // Deadline row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("DEADLINE", fontSize = 9.sp, color = Navy500, letterSpacing = 0.5.sp)
                    Text(deadlineLabel, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = deadlineColor)
                }
                OutlinedButton(
                    onClick          = onClick,
                    shape            = RoundedCornerShape(8.dp),
                    contentPadding   = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    colors           = ButtonDefaults.outlinedButtonColors(contentColor = Green600),
                    border           = ButtonDefaults.outlinedButtonBorder
                ) {
                    Text("View Details", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

// ─── Skeleton loader ──────────────────────────────────────────────────────────

@Composable
private fun TenderCardSkeleton() {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = NavyWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SkeletonBox(80.dp, 18.dp)
                SkeletonBox(70.dp, 14.dp)
            }
            Spacer(Modifier.height(10.dp))
            SkeletonBox(150.dp, 12.dp)
            Spacer(Modifier.height(8.dp))
            SkeletonBox(300.dp, 14.dp)
            Spacer(Modifier.height(6.dp))
            SkeletonBox(200.dp, 14.dp)
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Grey100)
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SkeletonBox(80.dp, 32.dp)
                SkeletonBox(110.dp, 32.dp)
            }
        }
    }
}

@Composable
private fun SkeletonBox(width: androidx.compose.ui.unit.Dp, height: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(4.dp))
            .background(Grey200)
    )
}

// ─── Empty / Error states ─────────────────────────────────────────────────────

@Composable
private fun EmptyTendersPlaceholder() {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Outlined.SearchOff, contentDescription = null, tint = Navy300, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(12.dp))
        Text("Aucun appel d'offres disponible", color = Navy500, fontSize = 14.sp)
    }
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = RedNotice),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = Red600, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(message, color = Red600, fontSize = 13.sp, modifier = Modifier.weight(1f))
            TextButton(onClick = onRetry) {
                Text("Retry", color = Red600, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─── Pillars Section ──────────────────────────────────────────────────────────

private data class Pillar(val icon: ImageVector, val title: String, val description: String)

@Composable
private fun PillarsSection() {
    val pillars = listOf(
        Pillar(
            Icons.Outlined.Visibility,
            "Total Transparency",
            "Each step of the procurement process is tracked and visible to authorized parties, ensuring fair competition."
        ),
        Pillar(
            Icons.Outlined.Lock,
            "E2EE Security",
            "End-to-end encrypted bids and sovereign data hosting protect sensitive commercial information."
        ),
        Pillar(
            Icons.Outlined.AutoGraph,
            "AI-Driven Analysis",
            "Advanced analytics and pattern detection to optimize public spending and identify market trends."
        )
    )

    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .background(Navy800)
            .padding(horizontal = 24.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("The Pillars of Al-Mizan", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NavyWhite)
        Spacer(Modifier.height(8.dp))
        Text(
            "Providing a sovereign infrastructure for transparent and efficient public procurement in Algeria.",
            fontSize  = 13.sp,
            color     = Navy300,
            lineHeight = 20.sp,
            modifier  = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(Modifier.height(32.dp))
        pillars.forEach { pillar ->
            PillarItem(pillar = pillar)
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun PillarItem(pillar: Pillar) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier        = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Navy900),
            contentAlignment = Alignment.Center
        ) {
            Icon(pillar.icon, contentDescription = null, tint = Green500, modifier = Modifier.size(30.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(pillar.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyWhite)
        Spacer(Modifier.height(8.dp))
        Text(
            pillar.description,
            fontSize   = 13.sp,
            color      = Navy300,
            lineHeight = 20.sp,
            modifier   = Modifier.padding(horizontal = 16.dp)
        )
    }
}