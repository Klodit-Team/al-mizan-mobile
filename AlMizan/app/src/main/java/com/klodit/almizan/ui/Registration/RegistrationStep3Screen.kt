package com.klodit.almizan.ui.Registration

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.klodit.almizan.ui.auth.AppLanguage

// ─────────────────────────────────────────────
//  COLORS
// ─────────────────────────────────────────────
private val DarkHeader3  = Color(0xFF364150)
private val GreenAccent3 = Color(0xFF4CAE4F)
private val GreenLight3  = Color(0xFFE8F5E9)
private val BlueLight    = Color(0xFFEEF4FB)
private val BlueAccent   = Color(0xFF1565C0)
private val White3       = Color.White
private val BorderGrey3  = Color(0xFFDDE3E8)
private val TextDark3    = Color(0xFF1A2B38)
private val TextMid3     = Color(0xFF4A6070)
private val TextLight3   = Color(0xFF8FA3B0)
private val FieldBg3     = Color(0xFFF8FAFB)
private val PageBg3      = Color(0xFFECEFF1)

// ─────────────────────────────────────────────
//  STEP 3 STRINGS
// ─────────────────────────────────────────────
private data class Step3Strings(
    val stepLabel    : String,
    val sectionTitle : String,
    val sectionSub   : String,
    val docRC        : String,
    val tapToScan    : String,
    val docNIF       : String,
    val docCNAS      : String,
    val aiExtracting : String,
    val privacyTitle : String,
    val privacyBody  : String,
    val e2ee         : String,
    val submitBtn    : String,
    val backLabel    : String,
    val footer       : String
) {
    companion object {
        val french = Step3Strings(
            stepLabel    = "ÉTAPE 3 SUR 3",
            sectionTitle = "Vérification des Documents",
            sectionSub   = "Veuillez télécharger ou scanner les documents légaux requis pour la vérification.",
            docRC        = "Registre Commercial (RC)",
            tapToScan    = "APPUYER POUR SCANNER",
            docNIF       = "Certificat NIF",
            docCNAS      = "Attestation CNAS",
            aiExtracting = "IA en cours d'extraction...",
            privacyTitle = "Garantie de Confidentialité",
            privacyBody  = "Vos documents sont chiffrés et accessibles uniquement par le personnel de vérification autorisé.",
            e2ee         = "SÉCURISÉ E2EE",
            submitBtn    = "Soumettre l'inscription",
            backLabel    = "← Retour aux détails du représentant",
            footer       = "© 2024 PLATEFORME SOUVERAINE AL-MIZAN"
        )
        val arabic = Step3Strings(
            stepLabel    = "الخطوة 3 من 3",
            sectionTitle = "التحقق من الوثائق",
            sectionSub   = "يرجى تحميل أو مسح الوثائق القانونية المطلوبة للتحقق.",
            docRC        = "السجل التجاري (RC)",
            tapToScan    = "انقر للمسح الضوئي",
            docNIF       = "شهادة NIF",
            docCNAS      = "شهادة CNAS",
            aiExtracting = "الذكاء الاصطناعي يستخرج البيانات...",
            privacyTitle = "ضمان خصوصية البيانات",
            privacyBody  = "وثائقك مشفرة ولا يمكن الوصول إليها إلا من قِبل موظفي التحقق المعتمدين.",
            e2ee         = "مؤمّن E2EE",
            submitBtn    = "إرسال التسجيل",
            backLabel    = "→ العودة إلى تفاصيل الممثل",
            footer       = "© 2024 منصة الميزان السيادية"
        )
        val english = Step3Strings(
            stepLabel    = "STEP 3 OF 3",
            sectionTitle = "Document Verification",
            sectionSub   = "Please upload or scan the required legal documents for verification.",
            docRC        = "Commercial Register (RC)",
            tapToScan    = "TAP TO SCAN",
            docNIF       = "NIF Certificate",
            docCNAS      = "CNAS Clearance",
            aiExtracting = "AI extracting data...",
            privacyTitle = "Data Privacy Guarantee",
            privacyBody  = "Your documents are encrypted and only accessible by authorized verification personnel.",
            e2ee         = "E2EE SECURED",
            submitBtn    = "Submit Registration",
            backLabel    = "← Back to Representative Details",
            footer       = "© 2024 AL-MIZAN SOVEREIGN PLATFORM"
        )
    }
}

// ─────────────────────────────────────────────
//  DOCUMENT STATES
// ─────────────────────────────────────────────
private enum class DocState { EMPTY, LOADING, DONE }

// ─────────────────────────────────────────────
//  REGISTRATION STEP 3 — Document Verification
// ─────────────────────────────────────────────
@Composable
fun RegistrationStep3Screen(
    onSubmitClick   : () -> Unit = {},
    onBackClick     : () -> Unit = {},
    selectedLang    : AppLanguage = AppLanguage.FRENCH,
    onLanguageChange: (AppLanguage) -> Unit = {}
) {
    val strings = when (selectedLang) {
        AppLanguage.FRENCH  -> Step3Strings.french
        AppLanguage.ARABIC  -> Step3Strings.arabic
        AppLanguage.ENGLISH -> Step3Strings.english
    }

    val nifState  by remember { mutableStateOf(DocState.LOADING) }
    val cnasState by remember { mutableStateOf(DocState.DONE) }

    val screenWidth   = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth     = if (screenWidth < 500.dp) screenWidth * 0.90f else 420.dp
    val overlapAmount = 32.dp

    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)),
        label         = "rotation"
    )

    Column(modifier = Modifier.fillMaxSize().background(PageBg3)) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // dark header
            Box(
                modifier = Modifier.fillMaxWidth().background(DarkHeader3)
                    .statusBarsPadding().padding(bottom = overlapAmount + 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = White3, modifier = Modifier.size(24.dp))
                    }
                    Text("Registration", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = White3)
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Info, null, tint = White3, modifier = Modifier.size(24.dp))
                    }
                }
            }

            // progress card
            Card(
                modifier  = Modifier.width(cardWidth).offset(y = -overlapAmount).zIndex(1f),
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = White3),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(strings.stepLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenAccent3, letterSpacing = 1.sp)
                        Text("100%", fontSize = 11.sp, color = TextLight3, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)).background(GreenAccent3))
                }
            }

            Spacer(Modifier.height((-overlapAmount.value + 8).dp))

            Column(modifier = Modifier.width(cardWidth)) {

                Text(strings.sectionTitle, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark3)
                Spacer(Modifier.height(4.dp))
                Text(strings.sectionSub, fontSize = 13.sp, color = TextMid3, lineHeight = 18.sp)
                Spacer(Modifier.height(24.dp))

                // RC — tap to scan
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, BorderGrey3, RoundedCornerShape(12.dp))
                        .background(White3)
                        .clickable { /* TODO: open camera/scanner */ }
                        .padding(vertical = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier         = Modifier.size(56.dp).clip(CircleShape).background(GreenLight3),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.PhotoCamera, null, tint = GreenAccent3, modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(strings.docRC, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark3)
                        Spacer(Modifier.height(4.dp))
                        Text(strings.tapToScan, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenAccent3, letterSpacing = 1.sp)
                    }
                }

                Spacer(Modifier.height(12.dp))

                // NIF — loading
                DocRow(
                    icon            = { Icon(Icons.Outlined.Description, null, tint = BlueAccent, modifier = Modifier.size(22.dp).rotate(rotation)) },
                    title           = strings.docNIF,
                    subtitle        = strings.aiExtracting,
                    state           = nifState,
                    subtitleColor   = BlueAccent,
                    showProgressBar = true
                )

                Spacer(Modifier.height(12.dp))

                // CNAS — done
                DocRow(
                    icon            = { Icon(Icons.Outlined.PictureAsPdf, null, tint = GreenAccent3, modifier = Modifier.size(22.dp)) },
                    title           = strings.docCNAS,
                    subtitle        = "File: CNAS_Clearance_2024.pdf",
                    state           = cnasState,
                    subtitleColor   = TextLight3,
                    showProgressBar = false,
                    borderColor     = GreenAccent3
                )

                Spacer(Modifier.height(16.dp))

                // privacy notice
                Row(
                    modifier          = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(BlueLight).padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Outlined.Shield, null, tint = BlueAccent, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(strings.privacyTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark3)
                        Spacer(Modifier.height(3.dp))
                        Text(strings.privacyBody, fontSize = 12.sp, color = TextMid3, lineHeight = 17.sp)
                    }
                }

                Spacer(Modifier.height(24.dp))
                LangSelector3(selectedLang = selectedLang, onLanguageChange = onLanguageChange)
                Spacer(Modifier.height(24.dp))
            }
        }

        // fixed bottom
        Column(
            modifier            = Modifier.fillMaxWidth().background(White3).padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Icon(Icons.Outlined.Lock, null, tint = TextLight3, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(5.dp))
                Text(strings.e2ee, fontSize = 9.sp, color = TextLight3, letterSpacing = 1.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick  = onSubmitClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = GreenAccent3)
            ) {
                Text(strings.submitBtn, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = White3)
            }
            Spacer(Modifier.height(10.dp))
            Text(strings.backLabel, fontSize = 13.sp, color = TextMid3, modifier = Modifier.clickable { onBackClick() })
            Spacer(Modifier.height(8.dp))
            Text(strings.footer, fontSize = 9.sp, color = TextLight3, letterSpacing = 0.5.sp, textAlign = TextAlign.Center)
        }
    }
}

// ─────────────────────────────────────────────
//  DOC ROW
// ─────────────────────────────────────────────
@Composable
private fun DocRow(
    icon           : @Composable () -> Unit,
    title          : String,
    subtitle       : String,
    state          : DocState,
    subtitleColor  : Color,
    showProgressBar: Boolean,
    borderColor    : Color = BorderGrey3
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .background(White3)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(FieldBg3),
            contentAlignment = Alignment.Center
        ) { icon() }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark3)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, fontSize = 12.sp, color = subtitleColor)
            if (showProgressBar) {
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    modifier   = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
                    color      = BlueAccent,
                    trackColor = Color(0xFFE0E0E0)
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        when (state) {
            DocState.DONE -> {
                Box(
                    modifier         = Modifier.size(28.dp).clip(CircleShape).background(GreenAccent3),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Check, null, tint = White3, modifier = Modifier.size(16.dp))
                }
            }
            DocState.LOADING -> {
                Icon(Icons.Outlined.MoreVert, null, tint = TextLight3, modifier = Modifier.size(20.dp))
            }
            DocState.EMPTY -> {}
        }
    }
}

// ─────────────────────────────────────────────
//  LANGUAGE SELECTOR
// ─────────────────────────────────────────────
@Composable
private fun LangSelector3(
    selectedLang    : AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        AppLanguage.entries.forEachIndexed { index, lang ->
            if (index > 0) Text("  •  ", fontSize = 11.sp, color = TextLight3)
            Text(
                text       = lang.label,
                fontSize   = 11.sp,
                color      = if (selectedLang == lang) GreenAccent3 else TextLight3,
                fontWeight = if (selectedLang == lang) FontWeight.Bold else FontWeight.Normal,
                modifier   = Modifier.clickable { onLanguageChange(lang) }
            )
        }
    }
}