package com.klodit.almizan.ui.components



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klodit.almizan.R
import com.klodit.almizan.ui.theme.*

@Composable
fun TopBar(
    userFirstName: String = "",          // from session / ProfileViewModel
    userLastName:  String = "",
    isVerified:    Boolean = false,
    tier:          String = "OUVERT",
    unreadCount:   Int = 0,
    onNotificationsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val initials = buildString {
        append(userFirstName.firstOrNull()?.uppercaseChar() ?: "")
        append(userLastName.firstOrNull()?.uppercaseChar() ?: "")
    }.ifEmpty { "?" }

    val displayName = when {
        userFirstName.isNotEmpty() -> userFirstName
        else -> stringResource(R.string.tab_profile)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Navy800,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── App brand ───────────────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.app_name),
                    color = NavyWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Verified badge
                    if (isVerified) {
                        Row(
                            modifier = Modifier
                                .background(Green500.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Verified,
                                contentDescription = null,
                                tint = Green400,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                stringResource(R.string.topbar_verified),
                                color = Green400,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }
                    // Tier badge
                    Text(
                        text = tier,
                        color = NavyWhite.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // ── Notifications icon ────────────────────────────────────
            Box {
                IconButton(onClick = onNotificationsClick) {
                    Icon(
                        Icons.Filled.Notifications,
                        contentDescription = stringResource(R.string.topbar_notifications),
                        tint = NavyWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }
                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (-2).dp, y = 2.dp)
                            .background(Red600, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                            color = NavyWhite,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.width(4.dp))

            // ── User avatar with initials ─────────────────────────────
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Green500),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = NavyWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(8.dp))

            // ── User first name ───────────────────────────────────────
            Text(
                text = displayName,
                color = NavyWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )

            Spacer(Modifier.width(4.dp))

            // ── Logout icon ───────────────────────────────────────────
            IconButton(onClick = onLogoutClick, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Outlined.Logout,
                    contentDescription = stringResource(R.string.topbar_disconnect),
                    tint = NavyWhite.copy(alpha = 0.75f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}