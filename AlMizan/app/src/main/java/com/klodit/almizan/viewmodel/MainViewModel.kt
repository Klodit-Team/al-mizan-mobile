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

    // Bid Wizard navigation state
    // TODO: Replace with proper navigation when integrating with NavController
    private val _showBidWizard = MutableStateFlow(false)
    val showBidWizard: StateFlow<Boolean> = _showBidWizard.asStateFlow()

    private val _currentBidAppelOffreId = MutableStateFlow<String?>(null)
    val currentBidAppelOffreId: StateFlow<String?> = _currentBidAppelOffreId.asStateFlow()

    // Bid Status screen state
    private val _showBidStatus = MutableStateFlow(false)
    val showBidStatus: StateFlow<Boolean> = _showBidStatus.asStateFlow()

    private val _currentStatusSubmissionId = MutableStateFlow<String?>(null)
    val currentStatusSubmissionId: StateFlow<String?> = _currentStatusSubmissionId.asStateFlow()

    // Evaluation Results screen state
    private val _showEvaluationResults = MutableStateFlow(false)
    val showEvaluationResults: StateFlow<Boolean> = _showEvaluationResults.asStateFlow()

    private val _currentEvalSubmissionId = MutableStateFlow<String?>(null)
    val currentEvalSubmissionId: StateFlow<String?> = _currentEvalSubmissionId.asStateFlow()

    // File Appeal screen state
    private val _showFileAppeal = MutableStateFlow(false)
    val showFileAppeal: StateFlow<Boolean> = _showFileAppeal.asStateFlow()

    private val _currentAppealSubmissionId = MutableStateFlow<String?>(null)
    val currentAppealSubmissionId: StateFlow<String?> = _currentAppealSubmissionId.asStateFlow()

    // Documents screen state
    private val _showDocuments = MutableStateFlow(false)
    val showDocuments: StateFlow<Boolean> = _showDocuments.asStateFlow()

    // Security screen state
    private val _showSecurity = MutableStateFlow(false)
    val showSecurity: StateFlow<Boolean> = _showSecurity.asStateFlow()

    // Settings screen state
    private val _showSettings = MutableStateFlow(false)
    val showSettings: StateFlow<Boolean> = _showSettings.asStateFlow()

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

    fun openBidWizard(appelOffreId: String) {
        _currentBidAppelOffreId.value = appelOffreId
        _showBidWizard.value = true
    }

    fun closeBidWizard() {
        _showBidWizard.value = false
        _currentBidAppelOffreId.value = null
    }

    fun openBidStatus(submissionId: String) {
        _currentStatusSubmissionId.value = submissionId
        _showBidStatus.value = true
    }

    fun closeBidStatus() {
        _showBidStatus.value = false
        _currentStatusSubmissionId.value = null
    }

    fun openEvaluationResults(submissionId: String) {
        _currentEvalSubmissionId.value = submissionId
        _showEvaluationResults.value = true
    }

    fun closeEvaluationResults() {
        _showEvaluationResults.value = false
        _currentEvalSubmissionId.value = null
    }

    fun openFileAppeal(submissionId: String) {
        _currentAppealSubmissionId.value = submissionId
        _showFileAppeal.value = true
    }

    fun closeFileAppeal() {
        _showFileAppeal.value = false
        _currentAppealSubmissionId.value = null
    }

    fun openDocuments() {
        _showDocuments.value = true
    }

    fun closeDocuments() {
        _showDocuments.value = false
    }

    fun openSecurity() {
        _showSecurity.value = true
    }

    fun closeSecurity() {
        _showSecurity.value = false
    }

    fun openSettings() {
        _showSettings.value = true
    }

    fun closeSettings() {
        _showSettings.value = false
    }
}
