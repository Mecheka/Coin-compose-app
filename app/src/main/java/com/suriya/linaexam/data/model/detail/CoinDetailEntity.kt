package com.suriya.linaexam.data.model.detail

import com.google.gson.annotations.SerializedName

data class CoinDetailEntity(
    @SerializedName("data")
    val `data`: DetailDataEntity? = null,
    @SerializedName("status")
    val status: String? = null,
)
