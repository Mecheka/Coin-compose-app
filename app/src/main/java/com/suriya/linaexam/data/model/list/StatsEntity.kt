package com.suriya.linaexam.data.model.list

import com.google.gson.annotations.SerializedName

data class StatsEntity(
    @SerializedName("total")
    val total: Int? = null,
    @SerializedName("total24hVolume")
    val total24hVolume: String? = null,
    @SerializedName("totalCoins")
    val totalCoins: Int? = null,
    @SerializedName("totalExchanges")
    val totalExchanges: Int? = null,
    @SerializedName("totalMarketCap")
    val totalMarketCap: String? = null,
    @SerializedName("totalMarkets")
    val totalMarkets: Int? = null,
)
