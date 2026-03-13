package com.klodit.almizan.ui.theme


/**
 * Supported UI languages for Al-Mizan.
 * Stored in NavGraph state and passed down to every screen.
 * When the user picks a language, we call AppCompatDelegate.setApplicationLocales()
 * which triggers an Activity recreation and loads the correct strings.xml.
 */
enum class AppLanguage(
    val label  : String,   // display label shown in the switcher
    val locale : String    // BCP-47 locale tag
) {
    FRENCH ("FRANÇAIS",  "fr"),
    ARABIC ("العربية",   "ar"),
    ENGLISH("ENGLISH",   "en")
}