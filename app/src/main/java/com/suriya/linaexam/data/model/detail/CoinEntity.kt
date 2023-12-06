package com.suriya.linaexam.data.model.detail

import com.google.gson.annotations.SerializedName

data class CoinEntity(
    @SerializedName("allTimeHigh")
    val allTimeHigh: AllTimeHighEntity? = null,
    @SerializedName("btcPrice")
    val btcPrice: String? = null,
    @SerializedName("change")
    val change: String? = null,
    @SerializedName("coinrankingUrl")
    val coinrankingUrl: String? = null,
    @SerializedName("color")
    val color: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("fullyDilutedMarketCap")
    val fullyDilutedMarketCap: String? = null,
    @SerializedName("24hVolume")
    val hVolume: String? = null,
    @SerializedName("hasContent")
    val hasContent: Boolean? = null,
    @SerializedName("iconUrl")
    val iconUrl: String? = null,
    @SerializedName("links")
    val links: List<LinkEntity?>? = null,
    @SerializedName("listedAt")
    val listedAt: Int? = null,
    @SerializedName("lowVolume")
    val lowVolume: Boolean? = null,
    @SerializedName("marketCap")
    val marketCap: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("numberOfExchanges")
    val numberOfExchanges: Int? = null,
    @SerializedName("numberOfMarkets")
    val numberOfMarkets: Int? = null,
    @SerializedName("price")
    val price: String? = null,
    @SerializedName("priceAt")
    val priceAt: Int? = null,
    @SerializedName("rank")
    val rank: Int? = null,
    @SerializedName("sparkline")
    val sparkline: List<String?>? = null,
    @SerializedName("supply")
    val supply: SupplyEntity? = null,
    @SerializedName("symbol")
    val symbol: String? = null,
    @SerializedName("tags")
    val tags: List<String?>? = null,
    @SerializedName("tier")
    val tier: Int? = null,
    @SerializedName("uuid")
    val uuid: String? = null,
    @SerializedName("websiteUrl")
    val websiteUrl: String? = null,
)
