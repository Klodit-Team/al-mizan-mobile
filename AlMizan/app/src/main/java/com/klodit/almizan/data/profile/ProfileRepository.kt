package com.klodit.almizan.data.profile

import com.klodit.almizan.data.remote.ApiClient

class ProfileRepository {
    private val api = ApiClient.retrofit.create(ProfileApi::class.java)

    suspend fun getProfileByUserId(userId: String, token: String): ProfileResponse {
        val response = api.getProfileByUserId(userId, "Bearer $token")
        return response.data ?: throw Exception("Profile data is null")
    }

    suspend fun updateProfile(
        profileId : String,
        token     : String,
        request   : UpdateProfileRequest
    ): ProfileResponse {
        val response = api.updateProfile(profileId, "Bearer $token", request)
        return response.data ?: throw Exception("Update failed")
    }

    suspend fun deleteProfile(profileId: String, token: String) {
        api.deleteProfile(profileId, "Bearer $token")
    }
}