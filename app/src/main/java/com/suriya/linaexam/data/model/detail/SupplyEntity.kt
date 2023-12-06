package com.suriya.linaexam.data.model.detail

import com.google.gson.annotations.SerializedName

data class SupplyEntity(
    @SerializedName("circulating")
    val circulating: String? = null,
    @SerializedName("confirmed")
    val confirmed: Boolean? = null,
    @SerializedName("max")
    val max: String? = null,
    @SerializedName("supplyAt")
    val supplyAt: Int? = null,
    @SerializedName("total")
    val total: String? = null,
)
