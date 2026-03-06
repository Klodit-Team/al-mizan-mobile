package com.klodit.almizan.ui.Registration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
private val DarkHeader2  = Color(0xFF364150)
private val GreenAccent2 = Color(0xFF4CAE4F)
private val GreenLight   = Color(0xFFE8F5E9)
private val White2       = Color.White
private val BorderGrey2  = Color(0xFFDDE3E8)
private val TextDark2    = Color(0xFF1A2B38)
private val TextMid2     = Color(0xFF4A6070)
private val TextLight2   = Color(0xFF8FA3B0)
private val FieldBg2     = Color(0xFFF8FAFB)
private val PageBg2      = Color(0xFFECEFF1)
private val LabelGrey    = Color(0xFF6B8090)

// ─────────────────────────────────────────────
//  STRINGS PER LANGUAGE
// ─────────────────────────────────────────────
private data class RegStrings(
    val stepLabel     : String,
    val sectionTitle  : String,
    val sectionSub    : String,
    val fieldOrgName  : String,
    val fieldNif      : String,
    val fieldNis      : String,
    val fieldRc       : String,
    val placeholderOrg: String,
    val placeholderNif: String,
    val placeholderNis: String,
    val placeholderRc : String,
    val secureTitle   : String,
    val secureBody    : String,
    val encryption    : String,
    val continueBtn   : String,
    val backLabel     : String,
    val footer        : String
) {
    companion object {
        val french = RegStrings(
            stepLabel      = "ÉTAPE 1 SUR 3",
            sectionTitle   = "Détails de l'organisation",
            sectionSub     = "Informations générales & identifiants fiscaux",
            fieldOrgName   = "DÉNOMINATION SOCIALE",
            fieldNif       = "CODE NIF",
            fieldNis       = "NUMÉRO NIS",
            fieldRc        = "NUMÉRO DU REGISTRE COMMERCIAL (RC)",
            placeholderOrg = "ex. Al-Mizan Solutions Sarl",
            placeholderNif = "NIF à 15 chiffres",
            placeholderNis = "NIS",
            placeholderRc  = "Entrer le numéro RC",
            secureTitle    = "Données Souveraines Sécurisées",
            secureBody     = "Les données d'identité de votre organisation sont chiffrées et gérées selon des protocoles souverains.",
            encryption     = "CHIFFREMENT BANCAIRE",
            continueBtn    = "Continuer vers l'étape 2  →",
            backLabel      = "← Retour à l'accueil",
            footer         = "© 2024 PLATEFORME SOUVERAINE AL-MIZAN"
        )
        val arabic = RegStrings(
            stepLabel      = "الخطوة 1 من 3",
            sectionTitle   = "تفاصيل المنظمة",
            sectionSub     = "المعلومات العامة والمعرفات الضريبية",
            fieldOrgName   = "الاسم القانوني للمنظمة",
            fieldNif       = "رمز NIF",
            fieldNis       = "رقم NIS",
            fieldRc        = "رقم السجل التجاري (RC)",
            placeholderOrg = "مثال: Al-Mizan Solutions",
            placeholderNif = "NIF مكون من 15 رقمًا",
            placeholderNis = "NIS",
            placeholderRc  = "أدخل رقم RC",
            secureTitle    = "بيانات سيادية آمنة",
            secureBody     = "يتم تشفير بيانات هوية مؤسستك وإدارتها وفق بروتوكولات سيادية.",
            encryption     = "تشفير مصرفي",
            continueBtn    = "المتابعة إلى الخطوة 2  ←",
            backLabel      = "→ العودة إلى الترحيب",
            footer         = "© 2024 منصة الميزان السيادية"
        )
        val english = RegStrings(
            stepLabel      = "STEP 1 OF 3",
            sectionTitle   = "Organization Details",
            sectionSub     = "General Information & Fiscal Identifiers",
            fieldOrgName   = "LEGAL ORGANIZATION NAME",
            fieldNif       = "NIF CODE",
            fieldNis       = "NIS NUMBER",
            fieldRc        = "COMMERCIAL REGISTER NUMBER (RC)",
            placeholderOrg = "e.g. Al-Mizan Solutions Ltd.",
            placeholderNif = "15-digit NIF",
            placeholderNis = "NIS",
            placeholderRc  = "Enter RC Number",
            secureTitle    = "Secure Sovereign Data",
            secureBody     = "Your organization's identity data is encrypted and managed under sovereign protocols.",
            encryption     = "BANK-GRADE ENCRYPTION",
            continueBtn    = "Continue to Step 2  →",
            backLabel      = "← Back to Welcome",
            footer         = "© 2024 AL-MIZAN SOVEREIGN PLATFORM"
        )
    }
}

// ─────────────────────────────────────────────
//  REGISTRATION STEP 1 SCREEN
// ─────────────────────────────────────────────
@Composable
fun RegistrationStep1Screen(
    onContinueClick : (orgName: String, nif: String, nis: String, rc: String) -> Unit = { _, _, _, _ -> },
    onBackClick     : () -> Unit = {},
    onInfoClick     : () -> Unit = {},
    selectedLang    : AppLanguage = AppLanguage.FRENCH,
    onLanguageChange: (AppLanguage) -> Unit = {}
) {
    var orgName by remember { mutableStateOf("") }
    var nif     by remember { mutableStateOf("") }
    var nis     by remember { mutableStateOf("") }
    var rc      by remember { mutableStateOf("") }

    val canContinue = orgName.isNotBlank() && nif.isNotBlank()
            && nis.isNotBlank() && rc.isNotBlank()

    val strings = when (selectedLang) {
        AppLanguage.FRENCH  -> RegStrings.french
        AppLanguage.ARABIC  -> RegStrings.arabic
        AppLanguage.ENGLISH -> RegStrings.english
    }

    val screenWidth   = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth     = if (screenWidth < 500.dp) screenWidth * 0.90f else 420.dp
    val overlapAmount = 32.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg2)
    ) {

        // ── scrollable body ──────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // dark header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkHeader2)
                    .statusBarsPadding()
                    .padding(bottom = overlapAmount + 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint               = White2,
                            modifier           = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text       = "Registration",
                        fontSize   = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color      = White2
                    )
                    IconButton(onClick = onInfoClick) {
                        Icon(
                            imageVector        = Icons.Outlined.Info,
                            contentDescription = "Info",
                            tint               = White2,
                            modifier           = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // step progress card (overlapping)
            Card(
                modifier  = Modifier
                    .width(cardWidth)
                    .offset(y = -overlapAmount)
                    .zIndex(1f),
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = White2),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            text          = strings.stepLabel,
                            fontSize      = 11.sp,
                            fontWeight    = FontWeight.Bold,
                            color         = GreenAccent2,
                            letterSpacing = 1.sp
                        )
                        Text("33%", fontSize = 11.sp, color = TextLight2, fontWeight = FontWeight.Medium)
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
                                .fillMaxWidth(0.33f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(GreenAccent2)
                        )
                    }
                }
            }

            Spacer(Modifier.height((-overlapAmount.value + 8).dp))

            // main content
            Column(modifier = Modifier.width(cardWidth)) {

                Text(strings.sectionTitle, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark2)
                Spacer(Modifier.height(4.dp))
                Text(strings.sectionSub, fontSize = 13.sp, color = GreenAccent2)

                Spacer(Modifier.height(24.dp))

                RegFieldLabel(strings.fieldOrgName)
                Spacer(Modifier.height(6.dp))
                RegTextField(orgName, { orgName = it }, strings.placeholderOrg)

                Spacer(Modifier.height(16.dp))

                RegFieldLabel(strings.fieldNif)
                Spacer(Modifier.height(6.dp))
                RegTextField(nif, { nif = it }, strings.placeholderNif)

                Spacer(Modifier.height(16.dp))

                RegFieldLabel(strings.fieldNis)
                Spacer(Modifier.height(6.dp))
                RegTextField(nis, { nis = it }, strings.placeholderNis)

                Spacer(Modifier.height(16.dp))

                RegFieldLabel(strings.fieldRc)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value         = rc,
                    onValueChange = { rc = it },
                    placeholder   = { Text(strings.placeholderRc, color = TextLight2, fontSize = 13.sp) },
                    leadingIcon   = {
                        Icon(Icons.Outlined.Edit, contentDescription = null, tint = TextLight2, modifier = Modifier.size(18.dp))
                    },
                    modifier   = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape      = RoundedCornerShape(8.dp),
                    colors     = regFieldColors()
                )

                Spacer(Modifier.height(24.dp))

                // secure notice
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GreenLight)
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = GreenAccent2, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(strings.secureTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark2)
                        Spacer(Modifier.height(3.dp))
                        Text(strings.secureBody, fontSize = 12.sp, color = TextMid2, lineHeight = 17.sp)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // language selector
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    AppLanguage.entries.forEachIndexed { index, lang ->
                        if (index > 0) Text("  •  ", fontSize = 11.sp, color = TextLight2)
                        Text(
                            text       = lang.label,
                            fontSize   = 11.sp,
                            color      = if (selectedLang == lang) GreenAccent2 else TextLight2,
                            fontWeight = if (selectedLang == lang) FontWeight.Bold else FontWeight.Normal,
                            modifier   = Modifier.clickable { onLanguageChange(lang) }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }

        // ── fixed bottom ─────────────────────
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .background(White2)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = TextLight2, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(5.dp))
                Text(strings.encryption, fontSize = 9.sp, color = TextLight2, letterSpacing = 1.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick  = { onContinueClick(orgName, nif, nis, rc) },
                enabled  = canContinue,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = GreenAccent2,
                    disabledContainerColor = Color(0xFFB0CDB9)
                )
            ) {
                Text(strings.continueBtn, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = White2)
            }

            Spacer(Modifier.height(10.dp))

            Text(strings.backLabel, fontSize = 13.sp, color = TextMid2, modifier = Modifier.clickable { onBackClick() })

            Spacer(Modifier.height(8.dp))

            Text(strings.footer, fontSize = 9.sp, color = TextLight2, letterSpacing = 0.5.sp, textAlign = TextAlign.Center)
        }
    }
}

// ─────────────────────────────────────────────
//  HELPERS
// ─────────────────────────────────────────────
@Composable
private fun RegFieldLabel(text: String) {
    Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LabelGrey, letterSpacing = 0.8.sp)
}

@Composable
private fun RegTextField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        placeholder   = { Text(placeholder, color = TextLight2, fontSize = 13.sp) },
        modifier      = Modifier.fillMaxWidth(),
        singleLine    = true,
        shape         = RoundedCornerShape(8.dp),
        colors        = regFieldColors()
    )
}

@Composable
private fun regFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = GreenAccent2,
    unfocusedBorderColor    = BorderGrey2,
    focusedTextColor        = TextDark2,
    unfocusedTextColor      = TextDark2,
    cursorColor             = GreenAccent2,
    focusedContainerColor   = FieldBg2,
    unfocusedContainerColor = FieldBg2
)