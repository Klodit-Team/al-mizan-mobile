package com.klodit.almizan.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klodit.almizan.data.profile.ProfileRepository
import com.klodit.almizan.data.profile.UpdateProfileRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()

    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val updateState: StateFlow<UpdateUiState> = _updateState.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteUiState>(DeleteUiState.Idle)
    val deleteState: StateFlow<DeleteUiState> = _deleteState.asStateFlow()

    fun fetchProfileByUserId(userId: String, token: String) {
        viewModelScope.launch {
            _profileState.value = ProfileUiState.Loading
            try {
                val profile = withContext(Dispatchers.IO) {
                    repository.getProfileByUserId(userId, token)
                }
                _profileState.value = ProfileUiState.Success(
                    ProfileData(
                        id               = profile.id,
                        userId           = profile.userId,
                        firstName        = profile.firstName,
                        lastName         = profile.lastName,
                        email            = profile.email,
                        phone            = profile.phone,
                        organizationName = profile.organizationName,
                        nif              = profile.nif,
                        nis              = profile.nis,
                        rc               = profile.rc,
                        isVerified       = profile.isVerified,
                        tier             = profile.tier,
                        avatarUrl        = profile.avatarUrl
                    )
                )
            } catch (e: Exception) {
                _profileState.value = ProfileUiState.Error(
                    e.localizedMessage ?: "Erreur réseau"
                )
            }
        }
    }

    fun updateProfile(profileId: String, token: String, request: UpdateProfileRequest) {
        viewModelScope.launch {
            _updateState.value = UpdateUiState.Loading
            try {
                withContext(Dispatchers.IO) {
                    repository.updateProfile(profileId, token, request)
                }
                _updateState.value = UpdateUiState.Success
            } catch (e: Exception) {
                _updateState.value = UpdateUiState.Error(
                    e.localizedMessage ?: "Erreur réseau"
                )
            }
        }
    }

    fun deleteProfile(profileId: String, token: String) {
        viewModelScope.launch {
            _deleteState.value = DeleteUiState.Loading
            try {
                withContext(Dispatchers.IO) {
                    repository.deleteProfile(profileId, token)
                }
                _deleteState.value = DeleteUiState.Success
            } catch (e: Exception) {
                _deleteState.value = DeleteUiState.Error(
                    e.localizedMessage ?: "Erreur réseau"
                )
            }
        }
    }

    fun resetUpdateState() { _updateState.value = UpdateUiState.Idle }
    fun resetDeleteState()  { _deleteState.value = DeleteUiState.Idle }
}