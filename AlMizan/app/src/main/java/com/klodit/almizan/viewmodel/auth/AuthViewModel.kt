package com.klodit.almizan.viewmodel.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klodit.almizan.data.auth.AuthApi
import com.klodit.almizan.data.auth.AuthRepository
import com.klodit.almizan.data.auth.RegisterRequest
import com.klodit.almizan.data.remote.ApiClient
import com.klodit.almizan.ui.theme.AppLanguage
import kotlinx.coroutines.launch

data class RegStep1Data(
    val orgName : String,
    val nif     : String,
    val nis     : String,
    val rc      : String
)

data class RegStep2Data(
    val phone    : String,
    val email    : String,
    val password : String
)

sealed class AuthState {
    object Idle    : AuthState()
    object Loading : AuthState()
    data class Success(val message: String = "") : AuthState()
    data class Error(val message: String)        : AuthState()
}

class AuthViewModel : ViewModel() {

    private val api        = ApiClient.retrofit.create(AuthApi::class.java)
    private val repository = AuthRepository(api)

    var authState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

    var step1Data: RegStep1Data? by mutableStateOf(null)
        private set

    var step2Data: RegStep2Data? by mutableStateOf(null)
        private set

    // ── Login ─────────────────────────────────────────────────────────────────
    fun login(
        email    : String,
        password : String,
        onSuccess: (token: String) -> Unit
    ) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val response = repository.login(email, password)
                val token    = response.resolvedToken() ?: ""
                authState    = AuthState.Success(token)
                onSuccess(token)
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                authState = AuthState.Error(
                    when (e.code()) {
                        401  -> "Email ou mot de passe incorrect"
                        403  -> "Accès refusé"
                        429  -> "Trop de tentatives, réessayez plus tard"
                        502  -> "Service temporairement indisponible"
                        else -> "Erreur serveur (${e.code()}): $errorBody"
                    }
                )
            } catch (e: java.net.UnknownHostException) {
                authState = AuthState.Error("Pas de connexion internet")
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Erreur de connexion")
            }
        }
    }

    // ── Registration step helpers ─────────────────────────────────────────────
    fun saveStep1(orgName: String, nif: String, nis: String, rc: String) {
        step1Data = RegStep1Data(orgName, nif, nis, rc)
    }

    fun saveStep2(phone: String, email: String, password: String) {
        step2Data = RegStep2Data(phone, email, password)
    }

    // ── Final registration submit ─────────────────────────────────────────────
    fun register(
        selectedLang: AppLanguage = AppLanguage.FRENCH,
        onSuccess   : (userId: String) -> Unit
    ) {
        val s1 = step1Data ?: run {
            authState = AuthState.Error("Données étape 1 manquantes")
            return
        }
        val s2 = step2Data ?: run {
            authState = AuthState.Error("Données étape 2 manquantes")
            return
        }

        val request = RegisterRequest(
            email             = s2.email,
            password          = s2.password,
            role              = "SERVICE_CONTRACTANT",
            langue            = selectedLang.locale,   // uses actual selected language
            nom               = "",
            prenom            = "",
            telephone         = s2.phone,
            denomination      = s1.orgName,
            nif               = s1.nif,
            nis               = s1.nis,
            registre_commerce = s1.rc,
            adresse           = null,
            wilaya            = null,
            commune           = null,
            type              = null,
            code_service      = null,
            secteur_activite  = null,
            ordonnateur       = null
        )

        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val response = repository.register(request)
                authState    = AuthState.Success(response.message ?: "Inscription réussie")
                onSuccess(response.user_id ?: "")
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                authState = AuthState.Error(
                    when (e.code()) {
                        409  -> "Un compte avec cet email existe déjà"
                        422  -> "Données invalides: $errorBody"
                        429  -> "Trop de tentatives, réessayez plus tard"
                        502  -> "Service temporairement indisponible"
                        else -> "Erreur serveur (${e.code()})"
                    }
                )
            } catch (e: java.net.UnknownHostException) {
                authState = AuthState.Error("Pas de connexion internet")
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Erreur d'inscription")
            }
        }
    }

    fun clearError() {
        if (authState is AuthState.Error) authState = AuthState.Idle
    }

    fun resetState() {
        authState = AuthState.Idle
        step1Data = null
        step2Data = null
    }

    // ── Forgot password ───────────────────────────────────────────────────────────
    fun forgotPassword(email: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                repository.forgotPassword(email)
                authState = AuthState.Success("Code envoyé à $email")
                onSuccess()
            } catch (e: retrofit2.HttpException) {
                authState = AuthState.Error(
                    when (e.code()) {
                        404  -> "Aucun compte trouvé avec cet email"
                        429  -> "Trop de tentatives, réessayez plus tard"
                        else -> "Erreur serveur (${e.code()})"
                    }
                )
            } catch (e: java.net.UnknownHostException) {
                authState = AuthState.Error("Pas de connexion internet")
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Erreur")
            }
        }
    }

    // ── Verify OTP token (used in VerificationScreen AND SetNewPasswordScreen) ────
    fun verifyToken(token: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                repository.verifyToken(token)
                authState = AuthState.Success()
                onSuccess()
            } catch (e: retrofit2.HttpException) {
                authState = AuthState.Error(
                    when (e.code()) {
                        401  -> "Code invalide ou expiré"
                        429  -> "Trop de tentatives"
                        else -> "Erreur serveur (${e.code()})"
                    }
                )
            } catch (e: java.net.UnknownHostException) {
                authState = AuthState.Error("Pas de connexion internet")
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Erreur")
            }
        }
    }

    // ── Reset password ────────────────────────────────────────────────────────────
    fun resetPassword(token: String, newPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                repository.resetPassword(token, newPassword)
                authState = AuthState.Success("Mot de passe réinitialisé")
                onSuccess()
            } catch (e: retrofit2.HttpException) {
                authState = AuthState.Error(
                    when (e.code()) {
                        401  -> "Code invalide ou expiré"
                        422  -> "Mot de passe trop faible"
                        else -> "Erreur serveur (${e.code()})"
                    }
                )
            } catch (e: java.net.UnknownHostException) {
                authState = AuthState.Error("Pas de connexion internet")
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Erreur")
            }
        }
    }
}