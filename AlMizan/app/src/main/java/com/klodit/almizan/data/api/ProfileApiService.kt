// app/src/main/java/com/klodit/almizan/data/api/ProfileApiService.kt
package com.klodit.almizan.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

// --- DTOs ---
data class MeResponseDto(val user: MeUserDto?)
data class MeUserDto(val userId: String?, val email: String?)

data class ProfileDto(
    val id: String?,
    val nom: String?,
    val prenom: String?,
    val telephone: String?,
    val langue: String?
)

data class OrganisationDto(
    val denomination: String?,
    val nif: String?,
    val nis: String?,
    val registre_commerce: String?,
    val adresse: String?,
    val wilaya: String?,
    val commune: String?,
    val type: String?,
    val is_verified: Boolean?
)

data class OperateurDto(
    val id: String?,
    val userId: String?,
    val user_id: String?,
    val qualifications: String?,
    val categories: String?,
    val is_eligible: Boolean?,
    val is_blacklisted: Boolean?,
    val raison_blacklist: String?,
    val organisation: OrganisationDto?
)

data class SessionDto(
    val id: String,
    val ip_address: String?,
    val user_agent: String?,
    val expires_at: String?,
    val created_at: String?
)

data class AuditLogDto(
    val id: Long,
    val action: String?,
    val entite: String?,
    val ip_address: String?,
    val horodatage: String?
)

data class DocumentDto(
    val id: Long,
    val type: String?,
    val nom_fichier: String?,
    val taille_octets: Long?,
    val date_expiration: String?,
    val created_at: String?,
    val is_valide: Boolean?,
    val hash_sha256: String?,
    val ocr_score_confiance: Double?,
    val ocr_is_conforme: Boolean?,
    val ocr_anomalies: String?
)

interface ProfileApiService {
    @GET("auth/me")
    suspend fun getMe(): Response<MeResponseDto>

    @GET("users/profiles/user/{userId}")
    suspend fun getProfile(@Path("userId") userId: String): Response<ProfileDto>

    @GET("users/operateurs-economiques?page=1&limit=200")
    suspend fun getOperateurs(): Response<List<OperateurDto>>

    @GET("auth/sessions")
    suspend fun getSessions(): Response<List<SessionDto>>

    @GET("audit/logs")
    suspend fun getAuditLogs(): Response<List<AuditLogDto>>

    @GET("users/pieces-administratives")
    suspend fun getDocuments(): Response<List<DocumentDto>>
}