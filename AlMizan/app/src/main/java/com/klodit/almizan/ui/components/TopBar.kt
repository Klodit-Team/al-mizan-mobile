package com.klodit.almizan.ui.components

import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klodit.almizan.R
import com.klodit.almizan.ui.theme.*

/**
 * AlMizanTopBar — switches between pre-login and post-login layout.
 *
 * PRE-LOGIN  → logo on the left, SIGN IN button + hamburger on the right
 * POST-LOGIN → person icon + username + verified badge on the left,
 *              notification bell on the right, logout dropdown on avatar tap
 */
@Composable
fun AlMizanTopBar(
    isLoggedIn: Boolean,
    userName: String = "",
    onSignInClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Navy800)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoggedIn) {
            PostLoginBar(
                userName = userName,
                onNotificationClick = onNotificationClick,
                onAvatarClick = onAvatarClick,
                onLogoutClick = onLogoutClick
            )
        } else {
            PreLoginBar(
                onSignInClick = onSignInClick,
                onMenuClick = onMenuClick
            )
        }
    }
}

// ─── Pre-login ────────────────────────────────────────────────────────────────

@Composable
private fun PreLoginBar(
    onSignInClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Logo from res/drawable/logo.png
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Al-Mizan logo",
            modifier = Modifier.height(36.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onSignInClick,
                colors = ButtonDefaults.buttonColors(containerColor = Green500),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "SIGN IN",
                    color = NavyWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(Modifier.width(12.dp))
            IconButton(onClick = onMenuClick, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = NavyWhite,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// ─── Post-login ───────────────────────────────────────────────────────────────

@Composable
private fun PostLoginBar(
    userName: String,
    onNotificationClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var showLogoutMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        // ── Left: avatar + name + logout dropdown ─────────────────────────────
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    showLogoutMenu = !showLogoutMenu
                    onAvatarClick()
                }
            ) {
                // Person icon in white circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(NavyWhite)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User avatar",
                        tint = Navy800,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column(verticalArrangement = Arrangement.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = userName.ifBlank { "User" },
                            color = NavyWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.width(4.dp))
                        // Green verified checkmark badge
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Green500)
                        ) {
                            Text(
                                text = "✓",
                                color = NavyWhite,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "OPEN",
                        color = Green500,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            // Logout dropdown — appears below the avatar row
            androidx.compose.animation.AnimatedVisibility(
                visible = showLogoutMenu,
                modifier = Modifier
                    .padding(top = 48.dp)
                    .align(Alignment.BottomStart),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    shadowElevation = 8.dp,
                    color = NavyWhite,
                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(10.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                showLogoutMenu = false
                                onLogoutClick()
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Logout,
                            contentDescription = "Logout",
                            tint = Navy800,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Sign out",
                            color = Navy800,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // ── Right: notification bell ──────────────────────────────────────────
        IconButton(onClick = onNotificationClick, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = NavyWhite,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}