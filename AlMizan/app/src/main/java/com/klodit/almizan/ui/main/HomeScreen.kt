package com.klodit.almizan.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klodit.almizan.data.Tender
import com.klodit.almizan.data.TenderRepository

// ─────────────────────────────────────────────
//  COLORS
// ─────────────────────────────────────────────
private val HmDark       = Color(0xFF364150)
private val HmGreen      = Color(0xFF4CAE4F)
private val HmGreenLight = Color(0xFFE8F5E9)
private val HmWhite      = Color.White
private val HmPageBg     = Color(0xFFF5F7F9)
private val HmTextDark   = Color(0xFF1A2B38)
private val HmTextMid    = Color(0xFF4A6070)
private val HmTextLight  = Color(0xFF8FA3B0)
private val HmBorder     = Color(0xFFDDE3E8)
private val HmNatBg      = Color(0xFFE8F5E9)
private val HmNatText    = Color(0xFF2E7D32)
private val HmIntlBg     = Color(0xFFF3E5F5)
private val HmIntlText   = Color(0xFF6A1B9A)
private val HmRedDeadline= Color(0xFFE53935)
private val HmStatsBg    = Color(0xFF2C3E50)

// ─────────────────────────────────────────────
//  HOME SCREEN
// ─────────────────────────────────────────────
@Composable
fun HomeScreen(
    onSearchClick  : () -> Unit = {},
    onViewAllClick : () -> Unit = {},
    onTenderClick  : (String) -> Unit = {},
    onSignInClick  : () -> Unit = {},
    onMarketStats  : () -> Unit = {},
    onLegalInfo    : () -> Unit = {},
    onHelpCenter   : () -> Unit = {}
) {
    // ── load data ───────────────────────────
    // TODO: swap with ViewModel when backend ready:
    // val tenders by viewModel.tenders.collectAsState()
    // val stats   by viewModel.stats.collectAsState()
    val stats   = remember { TenderRepository.getStats() }
    val tenders = remember { TenderRepository.getLatestTenders() }

    var searchQuery  by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(HmPageBg)) {

        // ══ TOP NAV BAR ══════════════════════
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(HmWhite)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AccountCircle, null, tint = HmGreen, modifier = Modifier.size(30.dp))
                Spacer(Modifier.width(6.dp))
                Text("AL-MIZAN", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = HmTextDark, letterSpacing = 1.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick        = onSignInClick,
                    shape          = RoundedCornerShape(6.dp),
                    colors         = ButtonDefaults.buttonColors(containerColor = HmGreen),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier       = Modifier.height(32.dp)
                ) {
                    Text("SIGN IN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HmWhite)
                }
                Icon(Icons.Outlined.Menu, null, tint = HmTextDark, modifier = Modifier.size(22.dp))
            }
        }

        // ══ SCROLLABLE BODY ══════════════════
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
        ) {

            // ── HERO ─────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HmWhite)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    text       = "Le Portail Souverain des\nMarchés Publics en",
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color      = HmTextDark,
                    lineHeight = 34.sp
                )
                Text("Algérie", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = HmGreen)

                Spacer(Modifier.height(10.dp))

                Text(
                    text       = "Accédez à l'ensemble des opportunités d'affaires publiques en Algérie sur une plateforme sécurisée, transparente et centralisée.",
                    fontSize   = 13.sp,
                    color      = HmTextMid,
                    lineHeight = 19.sp
                )

                Spacer(Modifier.height(20.dp))

                // search field
                OutlinedTextField(
                    value         = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder   = { Text("Rechercher un marché...", fontSize = 13.sp, color = HmTextLight) },
                    leadingIcon   = { Icon(Icons.Outlined.Search, null, tint = HmTextLight, modifier = Modifier.size(18.dp)) },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    shape         = RoundedCornerShape(8.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = HmGreen,
                        unfocusedBorderColor    = HmBorder,
                        focusedTextColor        = HmTextDark,
                        unfocusedTextColor      = HmTextDark,
                        focusedContainerColor   = HmWhite,
                        unfocusedContainerColor = HmWhite
                    )
                )

                Spacer(Modifier.height(10.dp))

                // sector + wilaya
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip("Secteur", Modifier.weight(1f))
                    FilterChip("Wilaya",  Modifier.weight(1f))
                }

                Spacer(Modifier.height(14.dp))

                Button(
                    onClick  = onSearchClick,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape    = RoundedCornerShape(8.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = HmGreen)
                ) {
                    Text("Rechercher →", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = HmWhite)
                }
            }

            Spacer(Modifier.height(2.dp))

            // ── STATS BAR ────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth().background(HmStatsBg).padding(vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                StatItem(stats.activeTenders, "ACTIVE TENDERS")
                Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color(0xFF4A6070)))
                StatItem(stats.awarded, "AWARDED")
                Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color(0xFF4A6070)))
                StatItem(stats.operators, "OPERATORS")
            }

            Spacer(Modifier.height(20.dp))

            // ── EXPLORE PLATFORM ─────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Explore Platform", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = HmTextDark)
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ExploreCard(Icons.Outlined.Info,          "Market Stats", "View insights",      Modifier.weight(1f), onMarketStats)
                    ExploreCard(Icons.Outlined.Lock,          "Legal Info",   "Law 23-12 & terms",  Modifier.weight(1f), onLegalInfo)
                    ExploreCard(Icons.Outlined.AccountCircle, "Help Center",  "24/7 assistance",    Modifier.weight(1f), onHelpCenter)
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── LATEST TENDERS ───────────────
            Row(
                modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Latest Tenders", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = HmTextDark)
                Text(
                    text       = "View All →",
                    fontSize   = 13.sp,
                    color      = HmGreen,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.clickable { onViewAllClick() }
                )
            }

            Spacer(Modifier.height(12.dp))

            // tender list
            // TODO: replace tenders with viewModel.tenders.collectAsState()
            Column(
                modifier            = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                tenders.forEach { tender ->
                    TenderCard(tender = tender, onClick = { onTenderClick(tender.id) })
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── PILLARS ──────────────────────
            Column(
                modifier            = Modifier.fillMaxWidth().background(HmWhite).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("The Pillars of Al-Mizan", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = HmTextDark, textAlign = TextAlign.Center)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Providing a sovereign infrastructure for transparent and efficient public procurement in Algeria.",
                    fontSize = 13.sp, color = HmTextMid, textAlign = TextAlign.Center, lineHeight = 18.sp
                )
                Spacer(Modifier.height(28.dp))
                PillarItem(Icons.Outlined.Check,  "Total Transparency", "Each step of the procurement process is tracked and visible to authorized parties, ensuring fair competition.")
                Spacer(Modifier.height(24.dp))
                PillarItem(Icons.Outlined.Lock,   "E2EE Security",      "End-to-end encrypted bids and sovereign data hosting protect sensitive commercial information.")
                Spacer(Modifier.height(24.dp))
                PillarItem(Icons.Outlined.Search, "AI-Driven Analysis", "Advanced analytics and pattern detection to optimize public spending and identify market trends.")
            }

            Spacer(Modifier.height(80.dp))
        }

        // ══ BOTTOM NAV ═══════════════════════
        Row(
            modifier              = Modifier.fillMaxWidth().background(HmWhite).navigationBarsPadding().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem(Icons.Outlined.Home,          "Home",    true)
            BottomNavItem(Icons.Outlined.Search,        "Search",  false)
            BottomNavItem(Icons.Outlined.Notifications, "Alerts",  false)
            BottomNavItem(Icons.Outlined.AccountCircle, "Profile", false)
        }
    }
}

// ─────────────────────────────────────────────
//  SMALL COMPONENTS
// ─────────────────────────────────────────────

@Composable
private fun FilterChip(label: String, modifier: Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, HmBorder, RoundedCornerShape(8.dp))
            .background(HmWhite)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = HmTextMid)
        Icon(Icons.Outlined.KeyboardArrowDown, null, tint = HmTextLight, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = HmWhite)
        Text(label, fontSize = 9.sp, color = HmTextLight, letterSpacing = 0.5.sp)
    }
}

@Composable
private fun ExploreCard(icon: ImageVector, title: String, subtitle: String, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(HmWhite)
            .border(1.dp, HmBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier         = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(HmGreenLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = HmGreen, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(title,    fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HmTextDark, textAlign = TextAlign.Center)
        Text(subtitle, fontSize = 10.sp, color = HmTextLight,          textAlign = TextAlign.Center)
    }
}

@Composable
private fun TenderCard(tender: Tender, onClick: () -> Unit) {
    val isNational    = tender.type == "NATIONAL"
    val typeBg        = if (isNational) HmNatBg   else HmIntlBg
    val typeColor     = if (isNational) HmNatText  else HmIntlText
    val deadlineColor = if (tender.daysLeft <= 7) HmRedDeadline else HmGreen

    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(10.dp),
        colors    = CardDefaults.cardColors(containerColor = HmWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // type + date row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(typeBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(tender.type, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = typeColor, letterSpacing = 0.5.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Info, null, tint = HmTextLight, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(tender.date, fontSize = 11.sp, color = HmTextLight)
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(tender.organization, fontSize = 11.sp, color = HmTextMid, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            Text(
                text       = tender.title,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold,
                color      = HmTextDark,
                maxLines   = 2,
                overflow   = TextOverflow.Ellipsis,
                lineHeight = 19.sp
            )

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = HmBorder, thickness = 0.5.dp)
            Spacer(Modifier.height(10.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("DEADLINE", fontSize = 9.sp, color = HmTextLight, letterSpacing = 0.5.sp)
                    Text(tender.deadline, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = deadlineColor)
                }
                Text("View Details →", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = HmGreen,
                    modifier = Modifier.clickable { onClick() })
            }
        }
    }
}

@Composable
private fun PillarItem(icon: ImageVector, title: String, description: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier         = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp)).background(HmGreenLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = HmGreen, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(10.dp))
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = HmTextDark)
        Spacer(Modifier.height(6.dp))
        Text(description, fontSize = 13.sp, color = HmTextMid, textAlign = TextAlign.Center, lineHeight = 18.sp)
    }
}

@Composable
private fun BottomNavItem(icon: ImageVector, label: String, selected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector        = icon,
            contentDescription = label,
            tint               = if (selected) HmGreen else HmTextLight,
            modifier           = Modifier.size(24.dp)
        )
        Text(
            text       = label,
            fontSize   = 10.sp,
            color      = if (selected) HmGreen else HmTextLight,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}