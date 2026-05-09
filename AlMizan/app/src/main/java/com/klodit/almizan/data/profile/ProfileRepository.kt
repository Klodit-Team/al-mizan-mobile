package com.klodit.almizan.data.profile

import com.klodit.almizan.data.remote.ApiClient

class ProfileRepository {

    private val api = ApiClient.retrofit.create(ProfileApi::class.java)


    private fun bearer(token: String) = "Bearer $token"

 /*   suspend fun getProfileByUserId(userId: String, token: String): ProfileResponse {
        // Temporary: log raw JSON
        val rawApi = ApiClient.retrofit.create(okhttp3.ResponseBody::class.java)

        val response = api.getProfileByUserId(userId)
        android.util.Log.d("PROFILE_RAW", "status=${response.status} message='${response.message}' data=${response.data}")
        return response.data
            ?: throw Exception(response.message.ifBlank { "Profile data is null" })
    }*/
    suspend fun getProfileByUserId(userId: String, token: String): ProfileResponse {
        android.util.Log.d("PROFILE_RAW", "=== CALLING API ===")
        return try {
            val result = api.getProfileByUserId(userId)
            android.util.Log.d("PROFILE_RAW", "=== SUCCESS: $result ===")
            result
        } catch (e: Exception) {
            android.util.Log.e("PROFILE_RAW", "=== ERROR: ${e.message} ===")
            throw e
        }
    }
    suspend fun updateProfile(
        profileId : String,
        token     : String,
        request   : UpdateProfileRequest
    ): ProfileResponse {
        android.util.Log.d("UPDATE_RAW", "request=$request")
        return try {
            val response = api.updateProfile(profileId, request)
            android.util.Log.d("UPDATE_RAW", "raw response=$response")
            response
        } catch (e: retrofit2.HttpException) {
            val body = e.response()?.errorBody()?.string()
            android.util.Log.e("UPDATE_RAW", "HTTP ${e.code()}: $body")
            throw Exception(body ?: "Update failed")
        } catch (e: Exception) {
            android.util.Log.e("UPDATE_RAW", "Exception: ${e.message}")
            throw e
        }
    }

    suspend fun deleteProfile(profileId: String, token: String) {
        val response = api.deleteProfile(profileId)
        if (response.status !in 200..299) {
            throw Exception(response.message.ifBlank { "Delete failed" })
        }
    }
}