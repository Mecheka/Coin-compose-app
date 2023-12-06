package com.suriya.linaexam.data.repository

import com.suriya.linaexam.data.model.detail.CoinDetailEntity
import com.suriya.linaexam.data.model.list.CoinsEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val LIMIT = "20"

interface CoinRepository {
    fun getCoins(
        search: String = "",
        offset: Int = 0,
        limit: String = LIMIT,
    ): Flow<Result<CoinsEntity>>

    fun getCoinByUUID(uuid: String): Flow<Result<CoinDetailEntity>>
}

class CoinRepositoryImpl
    @Inject
    constructor(private val httpClient: HttpClient) : CoinRepository {
        override fun getCoins(
            search: String,
            offset: Int,
            limit: String,
        ): Flow<Result<CoinsEntity>> {
            return flow {
                val response =
                    httpClient.get("/v2/coins") {
                        url.parameters.apply {
                            append("limit", limit)
                            append("offset", offset.toString())
                            append("search", search)
                        }
                    }
                if (response.status.isSuccess()) {
                    emit(Result.success(response.body()))
                } else {
                    emit(Result.failure(IllegalStateException()))
                }
            }
        }

        override fun getCoinByUUID(uuid: String): Flow<Result<CoinDetailEntity>> {
            return flow {
                val response = httpClient.get("/v2/coin/$uuid")
                if (response.status.isSuccess()) {
                    emit(Result.success(response.body()))
                } else {
                    emit(Result.failure(IllegalStateException()))
                }
            }
        }
    }
