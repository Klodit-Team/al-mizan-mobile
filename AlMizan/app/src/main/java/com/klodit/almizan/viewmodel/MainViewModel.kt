package com.klodit.almizan.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.klodit.almizan.ui.components.BottomNavDestination
import com.klodit.almizan.ui.theme.AppLanguage
import com.klodit.almizan.util.LocaleHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentRoute = MutableStateFlow(BottomNavDestination.Home.route)
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    // ── User identity ─────────────────────────────────────────────────────────
    // FIX: userId and profileId were missing — the profile screen needs both
    private val _userId    = MutableStateFlow("")
    val userId: StateFlow<String> = _userId.asStateFlow()

    private val _profileId = MutableStateFlow("")
    val profileId: StateFlow<String> = _profileId.asStateFlow()

    private val _userFirstName = MutableStateFlow("")
    val userFirstName: StateFlow<String> = _userFirstName.asStateFlow()

    private val _userLastName = MutableStateFlow("")
    val userLastName: StateFlow<String> = _userLastName.asStateFlow()

    private val _isVerified = MutableStateFlow(false)
    val isVerified: StateFlow<Boolean> = _isVerified.asStateFlow()

    private val _tier = MutableStateFlow("OUVERT")
    val tier: StateFlow<String> = _tier.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    // ── Language ──────────────────────────────────────────────────────────────
    private val _language = MutableStateFlow(LocaleHelper.currentLanguage(application))
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    // ── Navigation ────────────────────────────────────────────────────────────
    fun onTabSelected(destination: BottomNavDestination) {
        _currentRoute.value = destination.route
    }

    fun onLanguageChange(lang: AppLanguage) {
        LocaleHelper.setLocale(getApplication(), lang)
        _language.value = lang
    }

    // ── Auth lifecycle ────────────────────────────────────────────────────────
    fun onLogout() {
        _userId.value        = ""
        _profileId.value     = ""
        _userFirstName.value = ""
        _userLastName.value  = ""
        _isVerified.value    = false
        _tier.value          = "OUVERT"
        _unreadCount.value   = 0
    }

    fun onLogin(
        firstName : String,
        lastName  : String,
        verified  : Boolean = false,
        tier      : String  = "OUVERT"
    ) {
        _userFirstName.value = firstName
        _userLastName.value  = lastName
        _isVerified.value    = verified
        _tier.value          = tier
    }


    fun onProfileLoaded(
        userId     : String,
        profileId  : String,
        firstName  : String,
        lastName   : String,
        isVerified : Boolean,
        tier       : String
    ) {
        _userId.value        = userId
        _profileId.value     = profileId
        _userFirstName.value = firstName
        _userLastName.value  = lastName
        _isVerified.value    = isVerified
        _tier.value          = tier
    }

    fun setUnreadCount(count: Int) {
        _unreadCount.value = count
    }
}