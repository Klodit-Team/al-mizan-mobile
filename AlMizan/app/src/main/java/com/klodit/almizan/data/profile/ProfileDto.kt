package com.klodit.almizan.data.profile

import com.google.gson.annotations.SerializedName

// ── API envelope ─────────────────────────────────────────────────────────────

data class ProfileApiResponse(
    val status  : Int            = 0,
    val message : String         = "",
    val data    : ProfileResponse? = null
)

// ── Raw API payload ───────────────────────────────────────────────────────────
data class ProfileResponse(
    val id               : String  = "",
    val userId           : String  = "",
    @SerializedName("prenom")
    val firstName        : String  = "",
    @SerializedName("nom")
    val lastName         : String  = "",
    val email            : String  = "",
    @SerializedName("telephone")
    val phone            : String  = "",
    @SerializedName("denomination")
    val organizationName : String  = "",
    val nif              : String  = "",
    val nis              : String  = "",
    val rc               : String  = "",
    val isVerified       : Boolean = false,
    val tier             : String  = "OUVERT",
    val avatarUrl        : String? = null
) {

    fun toProfileData() = ProfileData(
        id               = id,
        userId           = userId,
        firstName        = firstName,
        lastName         = lastName,
        email            = email,
        phone            = phone,
        organizationName = organizationName,
        nif              = nif,
        nis              = nis,
        rc               = rc,
        isVerified       = isVerified,
        tier             = tier,
        avatarUrl        = avatarUrl
    )
}

// ── Request bodies ────────────────────────────────────────────────────────────
data class UpdateProfileRequest(
    @SerializedName("prenom")
    val firstName : String,
    @SerializedName("nom")
    val lastName  : String,
    @SerializedName("telephone")
    val phone     : String
)


data class DeleteAccountRequest(
    val password: String
)

// ── UI model ──────────────────────────────────────────────────────────────────
data class ProfileData(
    val id               : String,
    val userId           : String,
    val firstName        : String,
    val lastName         : String,
    val email            : String,
    val phone            : String,
    val organizationName : String,
    val nif              : String,
    val nis              : String,
    val rc               : String,
    val isVerified       : Boolean,
    val tier             : String,
    val avatarUrl        : String?
)

// ── UI states ─────────────────────────────────────────────────────────────────
sealed class ProfileUiState {
    object Idle                                  : ProfileUiState()
    object Loading                         : ProfileUiState()
    data class Success(val profile: ProfileData) : ProfileUiState()
    data class Error(val message: String)  : ProfileUiState()
}

sealed class UpdateUiState {
    object Idle                              : UpdateUiState()
    object Loading                           : UpdateUiState()
    data class Success(val message: String)  : UpdateUiState()
    data class Error(val message: String)    : UpdateUiState()
}

sealed class DeleteUiState {
    object Idle                              : DeleteUiState()
    object Loading                           : DeleteUiState()
    data class Success(val message: String)  : DeleteUiState()
    data class Error(val message: String)    : DeleteUiState()
}