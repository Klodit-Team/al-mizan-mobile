package com.klodit.almizan.data.remote

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Inet4Address
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = "https://api.klodit.app/api/v1/"

    private val ipv4Only = object : Dns {
        override fun lookup(hostname: String): List<java.net.InetAddress> {
            return Dns.SYSTEM.lookup(hostname)
                .filter { it is Inet4Address }
                .ifEmpty { Dns.SYSTEM.lookup(hostname) }
        }
    }

    private val cookieJar = object : CookieJar {
        @Volatile
        private var accessTokenCookie: Cookie? = null

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            cookies.firstOrNull { it.name == "access_token" }?.let { cookie ->
                accessTokenCookie = cookie
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            val cookie = accessTokenCookie
            return if (cookie != null && cookie.expiresAt > System.currentTimeMillis()) {
                listOf(cookie)
            } else {
                emptyList()
            }
        }
    }

    private fun getAccessToken(url: HttpUrl): String? {
        val cookie = cookieJar.loadForRequest(url).firstOrNull { it.name == "access_token" }
        return cookie?.value
    }

    private val httpClient = OkHttpClient.Builder()
        .dns(ipv4Only)
        .cookieJar(cookieJar)
        .addInterceptor { chain ->
            val request = chain.request()
            val builder = request.newBuilder()
                .header("X-Internal-Service", "api-gateway")

            val hasAuthorization = !request.header("Authorization").isNullOrBlank()
            if (!hasAuthorization) {
                val token = getAccessToken(request.url)
                if (!token.isNullOrBlank()) {
                    builder.header("Authorization", "Bearer $token")
                }
            }

            chain.proceed(builder.build())
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}