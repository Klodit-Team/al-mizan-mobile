package com.klodit.almizan.data.repository


import com.klodit.almizan.data.api.NotificationApiService
import com.klodit.almizan.model.NotificationDto
import com.klodit.almizan.model.PaginatedNotificationsDto


class NotificationRepository(
    private val api: NotificationApiService
) {

    suspend fun getMyNotifications(
        page  : Int      = 1,
        limit : Int      = 20,
        isLue : Boolean? = null
    ): Result<PaginatedNotificationsDto> = runCatching {
        val resp = api.getMyNotifications(page, limit, isLue)
        resp.body() ?: error("Empty response (${resp.code()})")
    }

    suspend fun getUnreadCount(): Result<Int> = runCatching {
        val resp = api.getUnreadCount()
        resp.body()?.count ?: 0
    }

    suspend fun markAsRead(id: String): Result<NotificationDto> = runCatching {
        val resp = api.markAsRead(id)
        resp.body() ?: error("Empty response (${resp.code()})")
    }

    suspend fun markAllAsRead(): Result<Int> = runCatching {
        val resp = api.markAllAsRead()
        resp.body()?.count ?: 0
    }
}