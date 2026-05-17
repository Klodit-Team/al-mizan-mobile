package com.klodit.almizan.data.tender

import com.klodit.almizan.data.remote.ApiClient
import com.klodit.almizan.model.tender.Tender

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

    suspend fun fetchTenderById(id: String): Result<Tender> {
        return try {
            val response = api.getTenderById(id)
            if (response.isSuccessful) {
                val tender = response.body()
                if (tender != null) Result.success(tender)
                else Result.failure(Exception("Tender not found"))
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}