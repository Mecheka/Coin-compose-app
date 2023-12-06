package com.suriya.linaexam.data.model.detail

import com.google.gson.annotations.SerializedName

data class DetailDataEntity(
    @SerializedName("coin")
    val coin: CoinEntity? = null,
)
