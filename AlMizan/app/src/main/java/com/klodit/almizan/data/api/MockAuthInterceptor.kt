package com.klodit.almizan.data.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

/**
 * Auth Interceptor that performs a real login against the API gateway
 * and caches the access_token for the entire app session.
 */
class MockAuthInterceptor : Interceptor {

    companion object {
        private const val TAG = "AuthInterceptor"
        private const val LOGIN_URL = "https://api.klodit.app/api/v1/auth/login"
    }

    // Cached token – only fetched once per app session
    @Volatile
    private var cachedToken: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = getToken()

        val newRequest = chain.request().newBuilder().apply {
            if (token != null) {
                addHeader("Authorization", "Bearer $token")
                // Gateway expects this cookie for session validation in some routes
                addHeader("Cookie", "access_token=$token")
            }
            addHeader("X-Internal-Service", "api-gateway")
        }.build()

        return chain.proceed(newRequest)
    }

    /**
     * Returns the cached token, or performs a synchronous login to obtain one.
     */
    @Synchronized
    private fun getToken(): String? {
        // Return cached token if already fetched
        cachedToken?.let { return it }

        return try {
            val jsonBody = JSONObject().apply {
                put("email", "sariyanouche7@gmail.com")
                put("password", "Password123!")
            }

            val requestBody = jsonBody.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaType())

            val request = Request.Builder()
                .url(LOGIN_URL)
                .post(requestBody)
                .build()

            // Use a separate bare OkHttpClient to avoid infinite interceptor loop
            val loginClient = OkHttpClient.Builder().build()
            val response = loginClient.newCall(request).execute()

            var token: String? = null

            // 1. Try to extract from Set-Cookie header
            val cookies = response.headers("Set-Cookie")
            for (cookie in cookies) {
                if (cookie.startsWith("access_token=")) {
                    token = cookie.substringAfter("access_token=").substringBefore(";")
                    break
                }
            }

            // 2. Fallback: extract from response body JSON
            if (token.isNullOrBlank()) {
                val bodyString = response.body?.string()
                if (!bodyString.isNullOrBlank()) {
                    val json = JSONObject(bodyString)
                    // Try common paths: data.access_token, access_token, data.token
                    token = json.optJSONObject("data")?.optString("access_token")
                        ?: json.optString("access_token")
                        ?: json.optJSONObject("data")?.optString("token")
                        ?: json.optString("token")
                    if (token.isNullOrBlank()) token = null
                }
            }

            if (token != null) {
                Log.d(TAG, "Successfully obtained auth token")
                cachedToken = token
            } else {
                Log.e(TAG, "Login succeeded (HTTP ${response.code}) but no token found in response")
            }

            token
        } catch (e: Exception) {
            Log.e(TAG, "Login failed: ${e.message}", e)
            null
        }
    }
}