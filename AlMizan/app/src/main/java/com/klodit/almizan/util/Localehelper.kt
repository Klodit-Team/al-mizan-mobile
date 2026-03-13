package com.klodit.almizan.util

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.klodit.almizan.ui.theme.AppLanguage

/**
 * Switches the app locale using AppCompatDelegate (API 33+ compatible).
 * Android will automatically reload the Activity and pick up the correct strings.xml.
 *
 * Usage in any Composable:
 *   val context = LocalContext.current
 *   LocaleHelper.setLocale(context, AppLanguage.ARABIC)
 *
 * Prerequisites in build.gradle (app):
 *   android {
 *       defaultConfig {
 *           resourceConfigurations += ["fr", "ar", "en"]
 *       }
 *   }
 *
 * Prerequisites in AndroidManifest.xml inside <application>:
 *   <service android:name="androidx.appcompat.app.AppLocalesMetadataHolderService"
 *       android:enabled="false"
 *       android:exported="false">
 *       <meta-data android:name="autoStoreLocales" android:value="true" />
 *   </service>
 */
object LocaleHelper {

    /**
     * Apply [language] as the app-wide locale.
     * AppCompatDelegate persists the choice across app restarts automatically
     * when autoStoreLocales = true in the manifest service above.
     */
    fun setLocale(language: AppLanguage) {
        val localeList = LocaleListCompat.forLanguageTags(language.locale)
        AppCompatDelegate.setApplicationLocales(localeList)
        // Activity will recreate automatically — no manual restart needed.
    }

    /**
     * Returns the currently active [AppLanguage] based on the system/app locale.
     * Falls back to FRENCH if the locale isn't mapped.
     */
    fun currentLanguage(): AppLanguage {
        val tag = AppCompatDelegate.getApplicationLocales()
            .toLanguageTags()
            .take(2)          // "fr", "ar", or "en"
        return AppLanguage.entries.firstOrNull { it.locale == tag }
            ?: AppLanguage.FRENCH
    }
}