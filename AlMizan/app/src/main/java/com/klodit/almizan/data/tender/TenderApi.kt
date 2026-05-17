package com.klodit.almizan.data.tender

import com.klodit.almizan.model.tender.Tender
import com.klodit.almizan.model.tender.TenderListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TenderApi {
    @GET("appels-offres/")
    suspend fun getTenders(): Response<TenderListResponse>

    @GET("appels-offres/{id}")
    suspend fun getTenderById(@Path("id") id: String): Response<Tender>
}