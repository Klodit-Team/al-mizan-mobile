package com.klodit.almizan.viewmodel.notification


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.klodit.almizan.data.remote.ApiClient
import com.klodit.almizan.data.repository.NotificationRepository
import com.klodit.almizan.model.NotificationDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NotificationUiState {
    object Loading                                        : NotificationUiState()
    data class Success(val items: List<NotificationDto>)  : NotificationUiState()
    data class Error(val message: String)                 : NotificationUiState()
}

class NotificationViewModel : ViewModel() {

    private val repo = NotificationRepository(ApiClient.notificationApi)

    private val _uiState     = MutableStateFlow<NotificationUiState>(NotificationUiState.Loading)
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    // track which page we loaded last (for pull-to-refresh reset)
    private var currentPage = 1
    private val allItems    = mutableListOf<NotificationDto>()

    init { refresh() }

    fun refresh() {
      /*  viewModelScope.launch {
            _uiState.value = NotificationUiState.Loading
            currentPage    = 1
            allItems.clear()

            repo.getMyNotifications(page = 1).fold(
                onSuccess = { paginated ->
                    allItems.addAll(paginated.data)
                    _uiState.value = NotificationUiState.Success(allItems.toList())
                },
                onFailure = {
                    _uiState.value = NotificationUiState.Error(it.message ?: "Unknown error")
                }
            )
            fetchUnreadCount()
        }*/

          val mockNotifications = listOf(
              NotificationDto(
                  id = "1",
                  userId = "demo-user",
                  titre = "New Tender Published",
                  contenu = "A new construction tender has been published in Algiers.",
                  type = "PLATEFORME",
                  categorie = "PUBLICATION",
                  statut = "ENVOYE",
                  isLue = false,
                  dateEnvoi = "2026-06-16T10:30:00Z",
                  dateLecture = null,
                  destinataire = null,
                  refEntiteId = "AO-2026-001",
                  refEntiteType = "TENDER",
                  createdAt = "2026-06-16T10:30:00Z"
              ),
              NotificationDto(
                  id = "2",
                  userId = "demo-user",
                  titre = "Tender Awarded",
                  contenu = "Tender AO-2026-145 has been awarded to SARL TechBuild.",
                  type = "PLATEFORME",
                  categorie = "ATTRIBUTION",
                  statut = "ENVOYE",
                  isLue = false,
                  dateEnvoi = "2026-06-16T08:15:00Z",
                  dateLecture = null,
                  destinataire = null,
                  refEntiteId = "AO-2026-145",
                  refEntiteType = "TENDER",
                  createdAt = "2026-06-16T08:15:00Z"
              )
          )

          allItems.clear()
          allItems.addAll(mockNotifications)

          _uiState.value = NotificationUiState.Success(mockNotifications)
          _unreadCount.value = mockNotifications.count { !it.isLue }
      }


    fun fetchUnreadCount() {
       /* viewModelScope.launch {
            repo.getUnreadCount().onSuccess { _unreadCount.value = it }
        }*/
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            repo.markAsRead(id).onSuccess {
                // optimistically update the list in-place
                val updated = allItems.map { n -> if (n.id == id) n.copy(isLue = true) else n }
                allItems.clear(); allItems.addAll(updated)
                _uiState.value = NotificationUiState.Success(allItems.toList())
                _unreadCount.value = (_unreadCount.value - 1).coerceAtLeast(0)
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            repo.markAllAsRead().onSuccess {
                val updated = allItems.map { n -> n.copy(isLue = true) }
                allItems.clear(); allItems.addAll(updated)
                _uiState.value = NotificationUiState.Success(allItems.toList())
                _unreadCount.value = 0
            }
        }
    }
}