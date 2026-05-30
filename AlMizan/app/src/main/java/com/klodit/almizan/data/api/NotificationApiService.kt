package com.klodit.almizan.data.api


import com.klodit.almizan.model.CountDto
import com.klodit.almizan.model.NotificationDto
import com.klodit.almizan.model.PaginatedNotificationsDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApiService {

    /** My notifications (paginated, optional filters) */
    @GET("notification-service/v1/notifications/mes-notifications")
    suspend fun getMyNotifications(
        @Query("page")    page  : Int     = 1,
        @Query("limit")   limit : Int     = 20,
        @Query("isLue")   isLue : Boolean? = null
    ): Response<PaginatedNotificationsDto>

    /** Unread count badge */
    @GET("notification-service/v1/notifications/non-lues/count")
    suspend fun getUnreadCount(): Response<CountDto>

    /** Mark a single notification as read */
    @PATCH("notification-service/v1/notifications/{id}/lire")
    suspend fun markAsRead(@Path("id") id: String): Response<NotificationDto>

    /** Mark ALL notifications as read */
    @PATCH("notification-service/v1/notifications/marquer-toutes-lues")
    suspend fun markAllAsRead(): Response<CountDto>
}