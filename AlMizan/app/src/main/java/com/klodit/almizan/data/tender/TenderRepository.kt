package com.klodit.almizan.data.tender

import com.klodit.almizan.data.remote.ApiClient
import com.klodit.almizan.model.tender.Tender
import com.klodit.almizan.model.tender.TenderListResponse
import retrofit2.Response
import retrofit2.http.GET

interface TenderApi {
    @GET("appels-offres/")
    suspend fun getTenders(): Response<TenderListResponse>
}

class TenderRepository {

    private val api: TenderApi = ApiClient.retrofit.create(TenderApi::class.java)

    suspend fun fetchTenders(): Result<List<Tender>> {
        return try {
            val response = api.getTenders()
            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}