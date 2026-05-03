package com.klodit.almizan.data.remote

import okhttp3.Dns
import okhttp3.OkHttpClient
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

    private val httpClient = OkHttpClient.Builder()
        .dns(ipv4Only)
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