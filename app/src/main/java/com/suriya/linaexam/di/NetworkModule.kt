package com.suriya.linaexam.di

import android.util.Log
import com.suriya.linaexam.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.gson.gson

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    const val NETWORK_TAG = "Coin_network"

    @Provides
    fun providesHttpClient(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                gson()
            }
            install(Logging) {
                logger =
                    object : Logger {
                        override fun log(message: String) {
                            Log.v(NETWORK_TAG, message)
                        }
                    }
                level = LogLevel.ALL
            }
            install(ResponseObserver) {
                onResponse {
                    Log.d(NETWORK_TAG, "Http status: ${it.status}")
                }
            }
            install(DefaultRequest) {
                url("https://api.coinranking.com")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(
                    "x-access-token",
                    BuildConfig.TOKEN,
                )
            }
        }
    }
}
