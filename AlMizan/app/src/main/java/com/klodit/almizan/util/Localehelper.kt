package com.klodit.almizan.util

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.klodit.almizan.ui.theme.AppLanguage


object LocaleHelper {


    fun setLocale(language: AppLanguage) {
        val localeList = LocaleListCompat.forLanguageTags(language.locale)
        AppCompatDelegate.setApplicationLocales(localeList)
        // Activity will recreate automatically — no manual restart needed.
    }


    fun currentLanguage(): AppLanguage {
        val tag = AppCompatDelegate.getApplicationLocales()
            .toLanguageTags()
            .take(2)          // "fr", "ar", or "en"
        return AppLanguage.entries.firstOrNull { it.locale == tag }
            ?: AppLanguage.FRENCH
    }
}