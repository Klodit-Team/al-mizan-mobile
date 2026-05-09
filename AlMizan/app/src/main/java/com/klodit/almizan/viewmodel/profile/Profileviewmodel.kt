package com.klodit.almizan.viewmodel.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klodit.almizan.data.profile.DeleteUiState
import com.klodit.almizan.data.profile.ProfileRepository
import com.klodit.almizan.data.profile.ProfileUiState
import com.klodit.almizan.data.profile.UpdateProfileRequest
import com.klodit.almizan.data.profile.UpdateUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()

    private val _profileUiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    private val _updateUiState = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val updateUiState: StateFlow<UpdateUiState> = _updateUiState.asStateFlow()

    private val _deleteUiState = MutableStateFlow<DeleteUiState>(DeleteUiState.Idle)
    val deleteUiState: StateFlow<DeleteUiState> = _deleteUiState.asStateFlow()

    fun fetchProfileByUserId(userId: String, token: String) {
        Log.d("PROFILE_DEBUG", "userId='$userId'  blank=${userId.isBlank()}")
        Log.d("PROFILE_DEBUG", "token='$token'  blank=${token.isBlank()}")
        viewModelScope.launch {

            if (_profileUiState.value !is ProfileUiState.Success) {
                _profileUiState.value = ProfileUiState.Loading
            }
            try {
                val profile = withContext(Dispatchers.IO) {
                    repository.getProfileByUserId(userId, token)
                }
                _profileUiState.value = ProfileUiState.Success(profile.toProfileData())
            } catch (e: Exception) {
                _profileUiState.value = ProfileUiState.Error(
                    e.localizedMessage ?: "Erreur réseau"
                )
            }
        }
    }

    fun updateProfile(profileId: String, token: String, request: UpdateProfileRequest) {
        viewModelScope.launch {
            _updateUiState.value = UpdateUiState.Loading
            try {
                withContext(Dispatchers.IO) {
                    repository.updateProfile(profileId, token, request)
                }
                _updateUiState.value = UpdateUiState.Success("Profil mis à jour avec succès")
            } catch (e: Exception) {
                _updateUiState.value = UpdateUiState.Error(
                    e.localizedMessage ?: "Erreur réseau"
                )
            }
        }
    }

    fun deleteProfile(profileId: String, token: String) {
        viewModelScope.launch {
            _deleteUiState.value = DeleteUiState.Loading
            try {
                withContext(Dispatchers.IO) {
                    repository.deleteProfile(profileId, token)
                }
                _deleteUiState.value = DeleteUiState.Success("Compte supprimé avec succès")
            } catch (e: Exception) {
                _deleteUiState.value = DeleteUiState.Error(
                    e.localizedMessage ?: "Erreur réseau"
                )
            }
        }
    }

    fun resetUpdateState() { _updateUiState.value = UpdateUiState.Idle }
    fun resetDeleteState()  { _deleteUiState.value = DeleteUiState.Idle }
}