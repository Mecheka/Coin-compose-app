package com.suriya.linaexam.data.model.list

import com.google.gson.annotations.SerializedName

data class CoinsEntity(
    @SerializedName("data")
    val `data`: DataEntity? = null,
    @SerializedName("status")
    val status: String? = null,
)
