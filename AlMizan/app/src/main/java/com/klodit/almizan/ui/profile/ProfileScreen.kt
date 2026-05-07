package com.klodit.almizan.ui.profile

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klodit.almizan.R
import com.klodit.almizan.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klodit.almizan.viewmodel.ProfileViewModel
// ─────────────────────────────────────────────
//  PROFILE SCREEN
// ─────────────────────────────────────────────

@Composable
fun ProfileScreen(
    localizedContext: Context,
    viewModel: ProfileViewModel = viewModel(),
    onEditPersonalInfo: () -> Unit = {},
    onRequestOrganisationUpdate: () -> Unit = {},
    onNavigateToDocuments: () -> Unit = {},
    onNavigateToSecurity: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val profileDataState by viewModel.profileData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadProfileData()
    }

    val scrollState = rememberScrollState()
    val profileData = profileDataState

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Green500)
        }
        return
    }

    if (profileData == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.ErrorOutline, null, tint = Red600, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(16.dp))
                Text("Erreur de chargement du profil.", color = Navy800, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Button(onClick = { viewModel.loadProfileData() }, colors = ButtonDefaults.buttonColors(containerColor = Green500)) {
                    Text("Réessayer")
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header Banner
        ProfileHeaderBanner(
            localizedContext = localizedContext,
            profileData = profileData
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section 1: Personal Info
        PersonalInfoCard(
            localizedContext = localizedContext,
            profileData = profileData,
            onEdit = onEditPersonalInfo
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section 2: Organisation Info
        OrganisationInfoCard(
            localizedContext = localizedContext,
            organisation = profileData.organisation,
            onRequestUpdate = onRequestOrganisationUpdate
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Section 3: Operational Profile
        OperationalProfileCard(
            localizedContext = localizedContext,
            operateur = profileData.operateur
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation Buttons
        NavigationButtonsSection(
            localizedContext = localizedContext,
            onNavigateToDocuments = onNavigateToDocuments,
            onNavigateToSecurity = onNavigateToSecurity,
            onNavigateToSettings = onNavigateToSettings
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ─────────────────────────────────────────────
//  HEADER BANNER
// ─────────────────────────────────────────────

@Composable
private fun ProfileHeaderBanner(
    localizedContext: Context,
    profileData: ProfileScreenData
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Navy800)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Avatar with initials
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(NavyWhite.copy(alpha = 0.15f))
                        .border(2.dp, NavyWhite.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profileData.initials,
                        color = NavyWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Status Pills Column
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Verified Badge
                    if (profileData.organisation.is_verified) {
                        StatusPill(
                            text = localizedContext.getString(R.string.profile_verified),
                            backgroundColor = Green500.copy(alpha = 0.15f),
                            textColor = Green400,
                            icon = Icons.Outlined.Verified
                        )
                    }

                    // Eligibility / Blacklist Status
                    when {
                        profileData.operateur.is_blacklisted -> {
                            BlacklistedStatusPill(
                                localizedContext = localizedContext,
                                reason = profileData.operateur.raison_blacklist
                            )
                        }
                        profileData.operateur.is_eligible -> {
                            StatusPill(
                                text = localizedContext.getString(R.string.profile_eligible),
                                backgroundColor = Green500.copy(alpha = 0.15f),
                                textColor = Green400,
                                icon = null
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Company Denomination
            Text(
                text = profileData.organisation.denomination,
                color = NavyWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Company Type Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(NavyWhite.copy(alpha = 0.1f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = profileData.organisation.type.value.uppercase(),
                    color = NavyWhite.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun StatusPill(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    icon: ImageVector?
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun BlacklistedStatusPill(
    localizedContext: Context,
    reason: String?
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Red600.copy(alpha = 0.15f))
            .clickable { if (reason != null) showDialog = true }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = localizedContext.getString(R.string.profile_blacklisted),
            color = Red600,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
        if (reason != null) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = localizedContext.getString(R.string.profile_blacklist_reason),
                tint = Red600,
                modifier = Modifier.size(14.dp)
            )
        }
    }

    // Dialog for blacklist reason
    if (showDialog && reason != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = Red600,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = localizedContext.getString(R.string.profile_blacklist_reason),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = reason)
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(
                        text = localizedContext.getString(R.string.profile_close),
                        color = Navy800
                    )
                }
            },
            containerColor = NavyWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// ─────────────────────────────────────────────
//  SECTION 1: PERSONAL INFO CARD
// ─────────────────────────────────────────────

@Composable
private fun PersonalInfoCard(
    localizedContext: Context,
    profileData: ProfileScreenData,
    onEdit: () -> Unit
) {
    ProfileCard(
        title = localizedContext.getString(R.string.profile_personal_info),
        icon = Icons.Outlined.Person,
        actionContent = {
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = localizedContext.getString(R.string.profile_edit),
                    tint = Navy600,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoRow(
                label = localizedContext.getString(R.string.profile_full_name),
                value = profileData.fullName,
                icon = Icons.Outlined.Badge
            )
            InfoRow(
                label = localizedContext.getString(R.string.profile_email),
                value = profileData.user.email,
                icon = Icons.Outlined.Email
            )
            InfoRow(
                label = localizedContext.getString(R.string.profile_phone),
                value = profileData.profile.telephone,
                icon = Icons.Outlined.Phone
            )
            InfoRow(
                label = localizedContext.getString(R.string.profile_language),
                value = getLanguageDisplayName(localizedContext, profileData.profile.langue),
                icon = Icons.Outlined.Language
            )
        }
    }
}

@Composable
private fun getLanguageDisplayName(context: Context, langue: Langue): String {
    return when (langue) {
        Langue.FR -> context.getString(R.string.lang_french)
        Langue.AR -> context.getString(R.string.lang_arabic)
        Langue.EN -> context.getString(R.string.lang_english)
    }
}

// ─────────────────────────────────────────────
//  SECTION 2: ORGANISATION INFO CARD
// ─────────────────────────────────────────────

@Composable
private fun OrganisationInfoCard(
    localizedContext: Context,
    organisation: Organisation,
    onRequestUpdate: () -> Unit
) {
    ProfileCard(
        title = localizedContext.getString(R.string.profile_organisation_info),
        icon = Icons.Outlined.Business
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Grid of identifiers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IdentifierBox(
                    label = localizedContext.getString(R.string.profile_nif),
                    value = organisation.nif,
                    modifier = Modifier.weight(1f)
                )
                IdentifierBox(
                    label = localizedContext.getString(R.string.profile_nis),
                    value = organisation.nis,
                    modifier = Modifier.weight(1f)
                )
            }

            IdentifierBox(
                label = localizedContext.getString(R.string.profile_rc),
                value = organisation.registre_commerce,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(color = Grey100)

            InfoRow(
                label = localizedContext.getString(R.string.profile_address),
                value = organisation.adresse,
                icon = Icons.Outlined.LocationOn
            )
            InfoRow(
                label = localizedContext.getString(R.string.profile_wilaya),
                value = "${organisation.wilaya}, ${organisation.commune}",
                icon = Icons.Outlined.Map
            )
            InfoRow(
                label = localizedContext.getString(R.string.profile_company_type),
                value = organisation.type.value.uppercase(),
                icon = Icons.Outlined.Category
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Request Update Button
            OutlinedButton(
                onClick = onRequestUpdate,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Navy800
                ),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    width = 1.5.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.EditNote,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = localizedContext.getString(R.string.profile_request_update),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun IdentifierBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Grey100.copy(alpha = 0.5f))
            .padding(12.dp)
    ) {
        Text(
            text = label,
            color = Navy500,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = Navy900,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ─────────────────────────────────────────────
//  SECTION 3: OPERATIONAL PROFILE CARD
// ─────────────────────────────────────────────

@Composable
private fun OperationalProfileCard(
    localizedContext: Context,
    operateur: OperateurEconomique
) {
    ProfileCard(
        title = localizedContext.getString(R.string.profile_operational),
        icon = Icons.Outlined.WorkOutline
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Categories as chips
            Column {
                Text(
                    text = localizedContext.getString(R.string.profile_categories),
                    color = Navy600,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(operateur.categories) { category ->
                        CategoryChip(text = category)
                    }
                }
            }

            HorizontalDivider(color = Grey100)

            // Qualifications
            Column {
                Text(
                    text = localizedContext.getString(R.string.profile_qualifications),
                    color = Navy600,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                operateur.qualifications.forEach { qualification ->
                    QualificationItem(text = qualification)
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Blue50)
            .border(1.dp, BlueBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = Blue800,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun QualificationItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Green50),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Verified,
                contentDescription = null,
                tint = Green600,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Navy900,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ─────────────────────────────────────────────
//  NAVIGATION BUTTONS SECTION
// ─────────────────────────────────────────────

@Composable
private fun NavigationButtonsSection(
    localizedContext: Context,
    onNavigateToDocuments: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Administrative Documents - Primary Style
        NavigationButton(
            text = localizedContext.getString(R.string.profile_nav_documents),
            icon = Icons.AutoMirrored.Outlined.InsertDriveFile,
            isPrimary = true,
            onClick = onNavigateToDocuments
        )

        // Security & MFA - Outlined Style
        NavigationButton(
            text = localizedContext.getString(R.string.profile_nav_security),
            icon = Icons.Outlined.Security,
            isPrimary = false,
            onClick = onNavigateToSecurity
        )

        // Settings & Audit - Outlined Style
        NavigationButton(
            text = localizedContext.getString(R.string.profile_nav_settings),
            icon = Icons.Outlined.Settings,
            isPrimary = false,
            onClick = onNavigateToSettings
        )
    }
}

@Composable
private fun NavigationButton(
    text: String,
    icon: ImageVector,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    if (isPrimary) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Navy800,
                contentColor = NavyWhite
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 4.dp
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Navy800
            ),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                width = 1.5.dp
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────
//  SHARED COMPONENTS
// ─────────────────────────────────────────────

@Composable
private fun ProfileCard(
    title: String,
    icon: ImageVector,
    actionContent: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Navy50),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Navy800,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    color = Navy900,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                actionContent?.invoke()
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Grey100)
            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Navy500,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = Navy500,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = Navy900,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
