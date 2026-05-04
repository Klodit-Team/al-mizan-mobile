package com.klodit.almizan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klodit.almizan.data.repository.ProfileRepository
import com.klodit.almizan.ui.profile.DocumentUiModel
import com.klodit.almizan.ui.profile.ProfileScreenData
import com.klodit.almizan.ui.profile.security.Session
import com.klodit.almizan.ui.profile.security.UserSecurity
import com.klodit.almizan.ui.profile.settings.AuditLog
import com.klodit.almizan.ui.profile.settings.NotificationCategory
import com.klodit.almizan.ui.profile.settings.NotificationChannel
import com.klodit.almizan.ui.profile.settings.NotificationPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class ProfileViewModel : ViewModel() {
    private val repository = ProfileRepository()

    // ── Profile ──
    private val _profileData = MutableStateFlow<ProfileScreenData?>(null)
    val profileData: StateFlow<ProfileScreenData?> = _profileData.asStateFlow()

    // ── Security ──
    private val _sessions = MutableStateFlow<List<Session>>(emptyList())
    val sessions: StateFlow<List<Session>> = _sessions.asStateFlow()

    private val _userSecurity = MutableStateFlow<UserSecurity?>(null)
    val userSecurity: StateFlow<UserSecurity?> = _userSecurity.asStateFlow()

    private val _passwordLastChangedDays = MutableStateFlow(0)
    val passwordLastChangedDays: StateFlow<Int> = _passwordLastChangedDays.asStateFlow()

    // ── Documents ──
    private val _documents = MutableStateFlow<List<DocumentUiModel>>(emptyList())
    val documents: StateFlow<List<DocumentUiModel>> = _documents.asStateFlow()

    // ── Settings ──
    private val _auditLogs = MutableStateFlow<List<AuditLog>>(emptyList())
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    private val _notificationPreference = MutableStateFlow(
        NotificationPreference(
            channels = NotificationChannel.entries.associateWith { true },
            categories = NotificationCategory.entries.associateWith { true }
        )
    )
    val notificationPreference: StateFlow<NotificationPreference> = _notificationPreference.asStateFlow()

    // ── Loading ──
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadProfileData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getProfileScreenData().onSuccess { _profileData.value = it }
            _isLoading.value = false
        }
    }

    fun loadSecurityData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getSessions().onSuccess { _sessions.value = it }
            repository.getUserSecurity().onSuccess { security ->
                _userSecurity.value = security
                // Derive passwordLastChangedDays from lastLogin as a proxy
                // until a dedicated password-change-date API is available
                _passwordLastChangedDays.value = ChronoUnit.DAYS.between(
                    security.lastLogin,
                    LocalDateTime.now()
                ).toInt().coerceAtLeast(0)
            }
            _isLoading.value = false
        }
    }

    fun loadDocumentsData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getDocuments().onSuccess { _documents.value = it }
            _isLoading.value = false
        }
    }

    fun loadSettingsData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAuditLogs().onSuccess { _auditLogs.value = it }
            _isLoading.value = false
        }
    }
}