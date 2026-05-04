package com.klodit.almizan.data.repository

import com.klodit.almizan.data.api.ApiClient
import com.klodit.almizan.data.api.ProfileApiService
import com.klodit.almizan.ui.profile.*
import com.klodit.almizan.ui.profile.security.Session
import com.klodit.almizan.ui.profile.security.UserSecurity
import com.klodit.almizan.ui.profile.settings.AuditLog
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ProfileRepository {
    private val api = ApiClient.retrofit.create(ProfileApiService::class.java)

    // In-memory cache for the current operateur ID (resolved once per session)
    @Volatile
    private var cachedOperateurId: String? = null

    /**
     * Resolves the current operateur ID dynamically:
     * 1. Calls /auth/me to get the userId
     * 2. Calls /users/operateurs-economiques to find the matching operateur
     * 3. Caches the result in memory so subsequent calls are free
     */
    suspend fun getCurrentOperateurId(): String {
        cachedOperateurId?.let { return it }

        val meRes = api.getMe()
        val userId = meRes.body()?.data?.user?.userId
            ?: throw IllegalStateException("Failed to resolve userId from /auth/me")

        val opsRes = api.getOperateurs()
        val operateurs = opsRes.body()?.data ?: emptyList()
        val operateur = operateurs.find { it.userId == userId || it.user_id == userId }
            ?: operateurs.firstOrNull()
            ?: throw IllegalStateException("No operateur found for userId=$userId")

        val opId = operateur.id
            ?: throw IllegalStateException("Operateur has null id for userId=$userId")

        cachedOperateurId = opId
        return opId
    }

    suspend fun getProfileScreenData(): Result<ProfileScreenData> {
        return try {
            // 1. Get Me (Identity)
            val meRes = api.getMe()
            val userId = meRes.body()?.data?.user?.userId
                ?: throw IllegalStateException("Failed to resolve userId from /auth/me")
            val email = meRes.body()?.data?.user?.email ?: "operateur@entreprise.dz"

            // 2. Get User Profile
            val profileRes = api.getProfile(userId)
            val profileDto = profileRes.body()?.data
            
            // 3. Get Operateur + Organisation Data
            val opsRes = api.getOperateurs()
            val operateurs = opsRes.body()?.data ?: emptyList()
            val opDto = operateurs.find { it.userId == userId || it.user_id == userId } 
                ?: operateurs.firstOrNull()

            val orgDto = opDto?.organisation

            // Cache the operateur ID while we have it
            opDto?.id?.let { cachedOperateurId = it }

            // Map to UI Models
            val profile = Profile(
                id = profileDto?.id ?: "",
                user_id = userId,
                nom = profileDto?.nom ?: "Nom inconnu",
                prenom = profileDto?.prenom ?: "Prénom inconnu",
                telephone = profileDto?.telephone ?: "Non renseigné",
                langue = Langue.fromValue(profileDto?.langue ?: "fr")
            )

            val organisation = Organisation(
                denomination = orgDto?.denomination ?: "Entreprise non renseignée",
                nif = orgDto?.nif ?: "N/A",
                nis = orgDto?.nis ?: "N/A",
                registre_commerce = orgDto?.registre_commerce ?: "N/A",
                adresse = orgDto?.adresse ?: "N/A",
                wilaya = orgDto?.wilaya ?: "N/A",
                commune = orgDto?.commune ?: "N/A",
                type = OrganisationType.fromValue(orgDto?.type ?: "sarl"),
                is_verified = orgDto?.is_verified ?: false
            )

            val operateur = OperateurEconomique(
                qualifications = opDto?.qualifications?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
                categories = opDto?.categories?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
                is_eligible = opDto?.is_eligible ?: false,
                is_blacklisted = opDto?.is_blacklisted ?: false,
                raison_blacklist = opDto?.raison_blacklist
            )

            val data = ProfileScreenData(
                user = User(email = email, is_active = true),
                profile = profile,
                organisation = organisation,
                operateur = operateur
            )

            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSessions(): Result<List<Session>> {
        return try {
            val response = api.getSessions()
            val sessions = response.body()?.data?.mapIndexed { index, dto ->
                Session(
                    id = dto.id,
                    ipAddress = dto.ip_address ?: "Unknown IP",
                    userAgent = dto.user_agent ?: "Unknown Device",
                    expiresAt = parseIsoDate(dto.expires_at) ?: LocalDateTime.now().plusDays(1),
                    createdAt = parseIsoDate(dto.created_at) ?: LocalDateTime.now(),
                    isCurrentSession = index == 0 // Assuming first session is current for demo
                )
            } ?: emptyList()
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Derives UserSecurity from the sessions list.
     * MFA status and last login are inferred from session data since
     * there is no dedicated API endpoint for security settings yet.
     */
    suspend fun getUserSecurity(): Result<UserSecurity> {
        return try {
            val sessionsResult = getSessions()
            val sessions = sessionsResult.getOrDefault(emptyList())
            val lastLogin = sessions.maxByOrNull { it.createdAt }?.createdAt ?: LocalDateTime.now()
            Result.success(
                UserSecurity(
                    mfaEnabled = false, // Will be updated when MFA API is available
                    lastLogin = lastLogin
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAuditLogs(): Result<List<AuditLog>> {
        return try {
            val response = api.getAuditLogs()
            val logs = response.body()?.data?.map { dto ->
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

    suspend fun getDocuments(): Result<List<DocumentUiModel>> {
        return try {
            val response = api.getDocuments()
            val documents = response.body()?.data?.map { dto ->
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

    private fun parseIsoDate(isoString: String?): LocalDateTime? {
        if (isoString.isNullOrEmpty()) return null
        return try {
            LocalDateTime.parse(isoString.replace("Z", ""), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseLocalDate(dateString: String?): LocalDate? {
        if (dateString.isNullOrEmpty()) return null
        return try {
            LocalDate.parse(dateString.substringBefore("T"), DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            null
        }
    }
}