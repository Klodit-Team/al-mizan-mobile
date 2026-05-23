// app/src/main/java/com/klodit/almizan/data/repository/ProfileRepository.kt
package com.klodit.almizan.data.repository

import com.klodit.almizan.data.api.ProfileApiService
import com.klodit.almizan.data.profile.ProfileApiResponse
import com.klodit.almizan.data.profile.ProfileResponse
import com.klodit.almizan.data.profile.UpdateProfileRequest
import com.klodit.almizan.data.remote.ApiClient
import com.klodit.almizan.ui.profile.*
import com.klodit.almizan.ui.profile.security.Session
import com.klodit.almizan.ui.profile.security.UserSecurity
import com.klodit.almizan.ui.profile.settings.AuditLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ProfileRepository {
    private val api = ApiClient.retrofit.create(ProfileApiService::class.java)

    @Volatile
    private var cachedOperateurId: String? = null

    suspend fun getCurrentOperateurId(): String = withContext(Dispatchers.IO) {
        cachedOperateurId?.let { return@withContext it }

        val meRes = api.getMe()
        val userId = meRes.body()?.user?.userId
            ?: throw IllegalStateException("Failed to resolve userId from /auth/me")

        val opsRes = try { api.getOperateurs() } catch (e: Exception) { null }
        val operateurs = opsRes?.body() ?: emptyList()
        val operateur = operateurs.find { it.userId == userId || it.user_id == userId }
            ?: operateurs.firstOrNull()

        val opId = operateur?.id ?: "fallback_operateur_id"
        cachedOperateurId = opId
        return@withContext opId
    }

    suspend fun getProfileScreenData(): Result<ProfileScreenData> = withContext(Dispatchers.IO) {
        try {
            val meRes = api.getMe()
            if (!meRes.isSuccessful) throw Exception("Auth failed")

            val userId = meRes.body()?.user?.userId ?: throw Exception("No user ID")
            val email = meRes.body()?.user?.email ?: "user@entreprise.dz"

            // Graceful fallback if profile or operator doesn't exist for test accounts
            val profileDto = try { api.getProfile(userId).body() } catch (e: Exception) { null }
            val operateurs = try { api.getOperateurs().body() ?: emptyList() } catch (e: Exception) { emptyList() }

            val opDto = operateurs.find { it.userId == userId || it.user_id == userId } ?: operateurs.firstOrNull()
            val orgDto = opDto?.organisation

            opDto?.id?.let { cachedOperateurId = it }

            val profile = Profile(
                id = profileDto?.id ?: "",
                user_id = userId,
                nom = profileDto?.nom ?: "Nom non renseigné",
                prenom = profileDto?.prenom ?: "Prénom non renseigné",
                telephone = profileDto?.telephone ?: "-",
                langue = Langue.fromValue(profileDto?.langue ?: "fr")
            )

            val organisation = Organisation(
                denomination = orgDto?.denomination ?: "Entreprise Test (Mock)",
                nif = orgDto?.nif ?: "000000000000000",
                nis = orgDto?.nis ?: "00000000000000",
                registre_commerce = orgDto?.registre_commerce ?: "RC-0000",
                adresse = orgDto?.adresse ?: "Alger",
                wilaya = orgDto?.wilaya ?: "Alger",
                commune = orgDto?.commune ?: "Alger Centre",
                type = OrganisationType.fromValue(orgDto?.type ?: "sarl"),
                is_verified = orgDto?.is_verified ?: false
            )

            val operateur = OperateurEconomique(
                qualifications = opDto?.qualifications?.split(",")?.filter { it.isNotBlank() } ?: listOf("Standard"),
                categories = opDto?.categories?.split(",")?.filter { it.isNotBlank() } ?: listOf("Catégorie 1"),
                is_eligible = opDto?.is_eligible ?: true,
                is_blacklisted = opDto?.is_blacklisted ?: false,
                raison_blacklist = opDto?.raison_blacklist
            )

            Result.success(ProfileScreenData(User(email, true), profile, organisation, operateur))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSessions(): Result<List<Session>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getSessions()
            val sessions = response.body()?.mapIndexed { index, dto ->
                Session(
                    id = dto.id,
                    ipAddress = dto.ip_address ?: "Unknown IP",
                    userAgent = dto.user_agent ?: "Unknown Device",
                    expiresAt = parseIsoDate(dto.expires_at) ?: LocalDateTime.now().plusDays(1),
                    createdAt = parseIsoDate(dto.created_at) ?: LocalDateTime.now(),
                    isCurrentSession = index == 0
                )
            } ?: emptyList()
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserSecurity(): Result<UserSecurity> = withContext(Dispatchers.IO) {
        try {
            val sessions = getSessions().getOrDefault(emptyList())
            val lastLogin = sessions.maxByOrNull { it.createdAt }?.createdAt ?: LocalDateTime.now()
            Result.success(UserSecurity(mfaEnabled = false, lastLogin = lastLogin))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAuditLogs(): Result<List<AuditLog>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAuditLogs()
            val logs = response.body()?.map { dto ->
                AuditLog(
                    id = dto.id,
                    action = dto.action ?: "ACTION",
                    entite = dto.entite ?: "System",
                    ipAddress = dto.ip_address ?: "127.0.0.1",
                    horodatage = parseIsoDate(dto.horodatage) ?: LocalDateTime.now()
                )
            } ?: emptyList()
            Result.success(logs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDocuments(): Result<List<DocumentUiModel>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getDocuments()
            val documents = response.body()?.map { dto ->
                DocumentUiModel(
                    id = dto.id,
                    type = DocumentType.entries.find { it.name == dto.type?.uppercase() } ?: DocumentType.NIF,
                    fileName = dto.nom_fichier ?: "document.pdf",
                    fileSizeBytes = dto.taille_octets ?: 0L,
                    dateExpiration = parseLocalDate(dto.date_expiration) ?: LocalDate.now().plusYears(1),
                    createdAt = parseIsoDate(dto.created_at) ?: LocalDateTime.now(),
                    isValide = dto.is_valide ?: false,
                    ocrScoreConfiance = dto.ocr_score_confiance ?: 0.0,
                    ocrIsConforme = dto.ocr_is_conforme ?: false,
                    ocrAnomalies = dto.ocr_anomalies
                )
            } ?: emptyList()
            Result.success(documents)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(
        profileId: String,
        request: UpdateProfileRequest
    ): Result<ProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.updateProfile(profileId, request)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                Result.failure(Exception("Erreur mise a jour profil"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProfile(profileId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteProfile(profileId)
            val body: ProfileApiResponse? = response.body()
            val status = body?.status ?: response.code()
            if (response.isSuccessful && status in 200..299) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.message ?: "Erreur suppression compte"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseIsoDate(isoString: String?): LocalDateTime? = try {
        LocalDateTime.parse(isoString?.replace("Z", ""), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (e: Exception) { null }

    private fun parseLocalDate(dateString: String?): LocalDate? = try {
        LocalDate.parse(dateString?.substringBefore("T"), DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (e: Exception) { null }
}