package com.klodit.almizan.data.profile

import retrofit2.http.*

interface ProfileApi {

    @GET("users/profiles/user/{userId}")
    suspend fun getProfileByUserId(
        @Path("userId") userId: String,
        @Header("Authorization") token: String
    ): ProfileApiResponse

    @PATCH("users/profiles/{profileId}")
    suspend fun updateProfile(
        @Path("profileId") profileId: String,
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): ProfileApiResponse

    @DELETE("users/profiles/{profileId}")
    suspend fun deleteProfile(
        @Path("profileId") profileId: String,
        @Header("Authorization") token: String
    ): ProfileApiResponse
}