package com.suriya.linaexam.data.model.detail

import com.google.gson.annotations.SerializedName

data class AllTimeHighEntity(
    @SerializedName("price")
    val price: String? = null,
    @SerializedName("timestamp")
    val timestamp: Int? = null,
)
