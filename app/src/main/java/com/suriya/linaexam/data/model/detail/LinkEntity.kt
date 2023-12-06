package com.suriya.linaexam.data.model.detail

import com.google.gson.annotations.SerializedName

data class LinkEntity(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("url")
    val url: String? = null,
)
