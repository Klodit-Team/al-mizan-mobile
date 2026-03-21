package com.klodit.almizan.viewmodel



import androidx.lifecycle.ViewModel
import com.klodit.almizan.ui.components.BottomNavDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    // ── Current tab / route ───────────────────────────────────────────────────
    private val _currentRoute = MutableStateFlow(BottomNavDestination.Home.route)
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    // ── User state ────────────────────────────────────────────────────────────
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userName = MutableStateFlow("CodedTech")
    val userName: StateFlow<String> = _userName.asStateFlow()

    // ── Actions ───────────────────────────────────────────────────────────────

    fun onTabSelected(destination: BottomNavDestination) {
        _currentRoute.value = destination.route
    }

    fun onLogout() {
        _isLoggedIn.value = false
        _userName.value   = ""
        // TODO: clear session, navigate to login
    }

    fun onLogin(name: String) {
        _userName.value   = name
        _isLoggedIn.value = true
    }
}