package com.klodit.almizan.data.profile

import retrofit2.http.*

interface ProfileApi {


  /*  @GET("users/profiles/user/{userId}")
    suspend fun getProfileByUserId(
        @Path("userId") userId: String
    ): ProfileApiResponse*/

    @GET("users/profiles/user/{userId}")
    suspend fun getProfileByUserId(
        @Path("userId") userId: String
    ): ProfileResponse

    @PATCH("users/profiles/{id}")
    suspend fun updateProfile(
        @Path("id") profileId : String,
        @Body       request   : UpdateProfileRequest
    ): ProfileResponse

    @DELETE("users/profiles/{id}")
    suspend fun deleteProfile(
        @Path("id") profileId: String
    ): ProfileApiResponse


}