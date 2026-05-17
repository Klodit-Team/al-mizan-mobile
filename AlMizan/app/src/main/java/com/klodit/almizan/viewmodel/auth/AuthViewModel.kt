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
import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


data class RegStep1Data(
    val orgName : String,
    val nif     : String,
    val nis     : String,
    val rc      : String
)

data class RegStep2Data(
    val phone    : String,
    val email    : String,
    val password : String,
    val nom      : String,
    val prenom   : String
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

    var uploadState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

    var step1Data: RegStep1Data? by mutableStateOf(null)
        private set

    var step2Data: RegStep2Data? by mutableStateOf(null)
        private set

    // ── Session state (populated after login/register) ────────────────────────
    var authToken by mutableStateOf<String?>(null)
        private set


    var currentUserId by mutableStateOf<String?>(null)
        private set

    // ── Login ─────────────────────────────────────────────────────────────────
    var failedLoginAttempts by mutableStateOf(0)
        private set

    fun login(
        email    : String,
        password : String,
        onSuccess: (token: String, userId: String) -> Unit,
        onLocked : () -> Unit = {}
    ) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val (response, token) = repository.login(email, password)
                android.util.Log.d("AUTH_DEBUG", "cookie token = '$token'")
                authToken     = token
                currentUserId = response.user?.userId ?: ""
                authState     = AuthState.Success(token)


                failedLoginAttempts = 0
                authToken           = token

                currentUserId = decodeUserIdFromJwt(token) ?: response.user?.userId
                authState           = AuthState.Success(token)
                android.util.Log.d("AUTH_DEBUG", "Login success — token=$token userId=$currentUserId")
                onSuccess(token, currentUserId ?: "")
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                when (e.code()) {
                    401 -> {
                        failedLoginAttempts++
                        if (failedLoginAttempts >= 5) {
                            authState = AuthState.Idle
                            onLocked()
                        } else {
                            authState = AuthState.Error(
                                "Email ou mot de passe incorrect (${failedLoginAttempts}/5)"
                            )
                        }
                    }
                    403  -> authState = AuthState.Error("Accès refusé")
                    429  -> { authState = AuthState.Idle; onLocked() }
                    502  -> authState = AuthState.Error("Service temporairement indisponible")
                    else -> authState = AuthState.Error("Erreur serveur (${e.code()}): $errorBody")
                }
            } catch (e: java.net.UnknownHostException) {
                authState = AuthState.Error("Pas de connexion internet")
            } catch (e: Exception) {
                authState = AuthState.Error(e.message ?: "Erreur de connexion")
            }
        }
    }

    fun resetFailedAttempts() { failedLoginAttempts = 0 }

    // ── Registration step helpers ─────────────────────────────────────────────
    fun saveStep1(orgName: String, nif: String, nis: String, rc: String) {
        step1Data = RegStep1Data(orgName, nif, nis, rc)
    }

    fun saveStep2(phone: String, email: String, password: String, nom: String, prenom: String) {
        step2Data = RegStep2Data(phone, email, password, nom, prenom)
    }

    // ── Final registration submit ─────────────────────────────────────────────
    fun register(
        selectedLang: AppLanguage = AppLanguage.FRENCH,
        onSuccess   : (userId: String) -> Unit
    ) {
        val s1 = step1Data ?: run { authState = AuthState.Error("Données étape 1 manquantes"); return }
        val s2 = step2Data ?: run { authState = AuthState.Error("Données étape 2 manquantes"); return }

        val request = RegisterRequest(
            email             = s2.email,
            password          = s2.password,
            role              = "SERVICE_CONTRACTANT",
            langue            = selectedLang.locale,
            nom               = s2.nom,
            prenom            = s2.prenom,
            telephone         = s2.phone,
            denomination      = s1.orgName,
            nif               = s1.nif,
            nis               = s1.nis,
            registre_commerce = s1.rc,
            adresse           = "string",
            wilaya            = "string",
            commune           = "string",
            type              = "EPA",
            code_service      = "string",
            secteur_activite  = "string",
            ordonnateur       = "string"
        )

        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val response  = repository.register(request)
                authToken     = response.resolvedToken()
                currentUserId = response.user_id ?: ""
                authState     = AuthState.Success(response.message ?: "Inscription réussie")
                onSuccess(response.user_id ?: "")
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                android.util.Log.e("AUTH_DEBUG", "HTTP ${e.code()}: $errorBody")
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

    fun clearError() { if (authState is AuthState.Error) authState = AuthState.Idle }

    fun resetState() {
        authState     = AuthState.Idle
        step1Data     = null
        step2Data     = null
    }

    fun clearSession() {
        authToken     = null
        currentUserId = null
        authState     = AuthState.Idle
        step1Data     = null
        step2Data     = null
        failedLoginAttempts = 0
    }

    // ── Forgot password ───────────────────────────────────────────────────────
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

    // ── Verify OTP token ─────────────────────────────────────────────────────
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

    // ── Reset password ────────────────────────────────────────────────────────
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

    // ── Document upload ───────────────────────────────────────────────────────
    fun uploadDocument(context: Context, uri: Uri, onSuccess: () -> Unit) {
        val token = authToken ?: run { uploadState = AuthState.Error("Non authentifié"); return }
        viewModelScope.launch {
            uploadState = AuthState.Loading
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes       = inputStream?.readBytes() ?: throw Exception("Fichier illisible")
                val mimeType    = context.contentResolver.getType(uri) ?: "application/pdf"
                val fileName    = uri.lastPathSegment ?: "document.pdf"
                val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                val part        = MultipartBody.Part.createFormData("file", fileName, requestBody)
                repository.uploadDocument(token, part)
                uploadState = AuthState.Success("Document uploadé")
                onSuccess()
            } catch (e: retrofit2.HttpException) {
                uploadState = AuthState.Error(
                    when (e.code()) {
                        401  -> "Session expirée, reconnectez-vous"
                        413  -> "Fichier trop volumineux"
                        415  -> "Format de fichier non supporté"
                        429  -> "Trop de tentatives"
                        502  -> "Service indisponible"
                        else -> "Erreur upload (${e.code()})"
                    }
                )
            } catch (e: java.net.UnknownHostException) {
                uploadState = AuthState.Error("Pas de connexion internet")
            } catch (e: Exception) {
                uploadState = AuthState.Error(e.message ?: "Erreur upload")
            }
        }
    }

    fun clearUploadError() { if (uploadState is AuthState.Error) uploadState = AuthState.Idle }

    // ── JWT userId decoder ──────────────────────────

    private fun decodeUserIdFromJwt(token: String): String? {
        return try {
            val payload = token.split(".").getOrNull(1) ?: return null
            // JWT uses base64url (no padding) — add padding manually
            val padded  = payload + "=".repeat((4 - payload.length % 4) % 4)
            val decoded = android.util.Base64.decode(padded, android.util.Base64.URL_SAFE)
            val json    = org.json.JSONObject(String(decoded))
            // Try common claim names
            json.optString("sub").takeIf { it.isNotEmpty() }
                ?: json.optString("userId").takeIf { it.isNotEmpty() }
                ?: json.optString("id").takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            null
        }
    }


    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.logout()
            } catch (e: Exception) {
                // ignore errors, clear session anyway
            }
            clearSession()
            onSuccess()
        }
    }


}