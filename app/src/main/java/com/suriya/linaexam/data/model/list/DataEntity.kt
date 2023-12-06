package com.suriya.linaexam.data.model.list

import com.google.gson.annotations.SerializedName
import com.suriya.linaexam.data.model.detail.CoinEntity

data class DataEntity(
    @SerializedName("coins")
    val coins: List<CoinEntity>? = emptyList(),
    @SerializedName("stats")
    val stats: StatsEntity? = null,
)
