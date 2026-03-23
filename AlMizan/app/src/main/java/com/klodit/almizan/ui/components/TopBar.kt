package com.klodit.almizan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klodit.almizan.R
import com.klodit.almizan.ui.theme.*

@Composable
fun AlMizanTopBar(
    userName: String = "",
    language: AppLanguage = AppLanguage.FRENCH,
    localizedContext: android.content.Context,        // ← context with correct locale baked in
    onLanguageChange: (AppLanguage) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    // These read from strings.xml using the localized context
    // They update correctly when language changes because localizedContext changes
    val verified    = localizedContext.getString(R.string.topbar_verified)
    val tier        = localizedContext.getString(R.string.topbar_tier)
    val disconnect  = localizedContext.getString(R.string.topbar_disconnect)

    var accountMenuExpanded  by remember { mutableStateOf(false) }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Navy800)
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // ── Left: avatar + name + disconnect dropdown ─────────────────────
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.clickable { accountMenuExpanded = true }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier         = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(NavyWhite)
                    ) {
                        Icon(Icons.Default.Person, null, tint = Navy800, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text       = userName.ifBlank { "User" },
                                color      = NavyWhite,
                                fontWeight = FontWeight.SemiBold,
                                fontSize   = 14.sp
                            )
                            Spacer(Modifier.width(6.dp))
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier         = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Green500.copy(alpha = 0.25f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(verified, color = Green500, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(tier, color = Green500, fontSize = 10.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.8.sp)
                    }
                }

                DropdownMenu(
                    expanded         = accountMenuExpanded,
                    onDismissRequest = { accountMenuExpanded = false },
                    modifier         = Modifier.background(NavyWhite)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Logout, null, tint = Navy800, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(10.dp))
                                Text(disconnect, color = Navy800, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        },
                        onClick = {
                            accountMenuExpanded = false
                            onLogoutClick()
                        }
                    )
                }
            }

            // ── Right: language switcher + bell ───────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier          = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NavyWhite.copy(alpha = 0.15f))
                            .clickable { languageMenuExpanded = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(language.locale.uppercase(), color = NavyWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(2.dp))
                        Icon(Icons.Outlined.KeyboardArrowDown, null, tint = NavyWhite, modifier = Modifier.size(14.dp))
                    }

                    DropdownMenu(
                        expanded         = languageMenuExpanded,
                        onDismissRequest = { languageMenuExpanded = false },
                        modifier         = Modifier.background(NavyWhite)
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            val isSelected = lang == language
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment     = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier              = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text       = lang.label,
                                            color      = if (isSelected) Green500 else Navy800,
                                            fontSize   = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (isSelected) {
                                            Spacer(Modifier.width(12.dp))
                                            Text("✓", color = Green500, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                },
                                onClick = {
                                    languageMenuExpanded = false
                                    onLanguageChange(lang)
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.width(4.dp))

                IconButton(onClick = onNotificationClick, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Outlined.Notifications, null, tint = NavyWhite, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}