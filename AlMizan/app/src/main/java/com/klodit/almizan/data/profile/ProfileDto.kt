package com.klodit.almizan.data.profile


data class ProfileResponse(
    val id               : String  = "",
    val userId           : String  = "",
    val firstName        : String  = "",
    val lastName         : String  = "",
    val email            : String  = "",
    val phone            : String  = "",
    val organizationName : String  = "",
    val nif              : String  = "",
    val nis              : String  = "",
    val rc               : String  = "",
    val isVerified       : Boolean = false,
    val tier             : String  = "OUVERT",
    val avatarUrl        : String? = null
)

data class UpdateProfileRequest(
    val firstName : String,
    val lastName  : String,
    val phone     : String
)

data class ProfileApiResponse(
    val data    : ProfileResponse? = null,
    val message : String?          = null
)