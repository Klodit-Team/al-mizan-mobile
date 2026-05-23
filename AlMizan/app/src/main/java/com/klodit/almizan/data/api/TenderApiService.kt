package com.klodit.almizan.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

data class TenderLotDto(
    val id: String,
    val numero: String?,
    val designation: String?,
    val description: String?,
    val montantEstime: Any?,
    val statut: String?
)

data class TenderDto(
    val id: String,
    val reference: String?,
    val objet: String?,
    val typeProcedure: String?,
    val dateLimiteSoumission: String?,
    val statut: String?,
    val organisationName: String?,
    val wilaya: String?,
    val lots: List<TenderLotDto>?
)

data class AttributionDto(
    val id: String,
    val aoId: String?,
    val appel_offre_id: String?,
    val type: String?,
    val montantAttribue: Double?,
    val montant_attribue: Double?,
    val dateAttribution: String?,
    val date_attribution: String?,
    val dateFinRecours: String?,
    val date_fin_recours: String?
)

interface TenderApiService {
    @GET("appels-offres?page=1&limit=500")
    suspend fun getAppelsOffres(): Response<ApiResponse<List<TenderDto>>>

    @GET("appels-offres/{id}")
    suspend fun getTenderById(@Path("id") id: String): Response<ApiResponse<TenderDto>>

    @GET("appels-offres/attributions")
    suspend fun getAttributions(): Response<ApiResponse<List<AttributionDto>>>
}