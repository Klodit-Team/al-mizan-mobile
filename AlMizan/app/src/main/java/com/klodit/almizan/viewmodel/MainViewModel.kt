package com.klodit.almizan.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.klodit.almizan.ui.components.BottomNavDestination
import com.klodit.almizan.ui.theme.AppLanguage
import com.klodit.almizan.util.LocaleHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// AndroidViewModel gives us access to Application context
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentRoute = MutableStateFlow(BottomNavDestination.Home.route)
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    private val _userName = MutableStateFlow("CodedTech")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _language = MutableStateFlow(LocaleHelper.currentLanguage())
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun onTabSelected(destination: BottomNavDestination) {
        _currentRoute.value = destination.route
    }

    fun onLanguageChange(lang: AppLanguage) {
        LocaleHelper.setLocale(lang)
        _language.value = lang
    }

    fun onLogout() {
        _userName.value = ""
    }

    fun onLogin(name: String) {
        _userName.value = name
    }
}