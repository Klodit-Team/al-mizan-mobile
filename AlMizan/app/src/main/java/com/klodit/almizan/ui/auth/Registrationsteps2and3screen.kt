package com.klodit.almizan.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────
//  COLORS  (same palette)
// ─────────────────────────────────────────────
private val DarkHeader3  = Color(0xFF364150)
private val GreenAccent3 = Color(0xFF4CAE4F)
private val White3       = Color.White
private val TextDark3    = Color(0xFF1A2B38)
private val TextMid3     = Color(0xFF4A6070)
private val TextLight3   = Color(0xFF8FA3B0)
private val PageBg3      = Color(0xFFECEFF1)

// ─────────────────────────────────────────────
//  REUSABLE REGISTRATION SHELL
//  (shared layout for step 2 and step 3)
// ─────────────────────────────────────────────
@Composable
private fun RegistrationShell(
    stepNumber    : Int,            // 1, 2 or 3
    totalSteps    : Int = 3,
    title         : String,
    subtitle      : String,
    continueLabel : String,
    canContinue   : Boolean,
    onContinue    : () -> Unit,
    onBack        : () -> Unit,
    onInfo        : () -> Unit = {},
    content       : @Composable ColumnScope.() -> Unit   //
) {
    val progress = stepNumber.toFloat() / totalSteps.toFloat()
    val percent  = (progress * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg3)
    ) {

        // ── top bar ──────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkHeader3)
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint               = White3,
                    modifier           = Modifier.size(24.dp)
                )
            }
            Text(
                text       = "Registration",
                fontSize   = 17.sp,
                fontWeight = FontWeight.Bold,
                color      = White3
            )
            IconButton(onClick = onInfo) {
                Icon(
                    imageVector        = Icons.Outlined.Info,
                    contentDescription = "Info",
                    tint               = White3,
                    modifier           = Modifier.size(24.dp)
                )
            }
        }

        // ── scrollable body ──────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // step progress card
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = White3),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            text          = "STEP $stepNumber OF $totalSteps",
                            fontSize      = 11.sp,
                            fontWeight    = FontWeight.Bold,
                            color         = GreenAccent3,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text       = "$percent%",
                            fontSize   = 11.sp,
                            color      = TextLight3,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFFE0E0E0))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(GreenAccent3)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // section title
            Text(text = title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark3)
            Spacer(Modifier.height(4.dp))
            Text(text = subtitle, fontSize = 13.sp, color = GreenAccent3)

            Spacer(Modifier.height(24.dp))

            // actual fields injected here
            content()

            Spacer(Modifier.height(24.dp))
        }

        // ── fixed bottom ─────────────────────
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .background(White3)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Info,
                    contentDescription = null,
                    tint               = TextLight3,
                    modifier           = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text          = "BANK-GRADE ENCRYPTION",
                    fontSize      = 9.sp,
                    color         = TextLight3,
                    letterSpacing = 1.sp,
                    fontWeight    = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick  = onContinue,
                enabled  = canContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor         = GreenAccent3,
                    disabledContainerColor = Color(0xFFB0CDB9)
                )
            ) {
                Text(
                    text       = continueLabel,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = White3
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text     = "← Back to Step ${stepNumber - 1}",
                fontSize = 13.sp,
                color    = TextMid3,
                modifier = Modifier.clickable { onBack() }
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text          = "© 2024 AL-MIZAN SOVEREIGN PLATFORM",
                fontSize      = 9.sp,
                color         = TextLight3,
                letterSpacing = 0.5.sp,
                textAlign     = TextAlign.Center
            )
        }
    }
}

// ─────────────────────────────────────────────
//  STEP 2  — placeholder,
// ─────────────────────────────────────────────
@Composable
fun RegistrationStep2Screen(
    onContinueClick : () -> Unit = {},
    onBackClick     : () -> Unit = {}
) {
    RegistrationShell(
        stepNumber    = 2,
        title         = "Step 2 Title",          //
        subtitle      = "Step 2 subtitle",        //
        continueLabel = "Continue to Step 3  →",
        canContinue   = true,                     //
        onContinue    = onContinueClick,
        onBack        = onBackClick
    ) {
        // ── placeholder box ──────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF0F4F7)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text      = "Step 2 fields go here",
                fontSize  = 14.sp,
                color     = TextLight3,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─────────────────────────────────────────────
//  STEP 3  — placeholder,
// ─────────────────────────────────────────────
@Composable
fun RegistrationStep3Screen(
    onSubmitClick : () -> Unit = {},
    onBackClick   : () -> Unit = {}
) {
    RegistrationShell(
        stepNumber    = 3,
        title         = "Step 3 Title",
        subtitle      = "Step 3 subtitle",
        continueLabel = "Submit Registration  →",
        canContinue   = true,
        onContinue    = onSubmitClick,
        onBack        = onBackClick
    ) {
        // ── placeholder box ──────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF0F4F7)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text      = "Step 3 fields go here",
                fontSize  = 14.sp,
                color     = TextLight3,
                textAlign = TextAlign.Center
            )
        }
    }
}