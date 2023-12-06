package com.suriya.linaexam.ui.screen.state

import com.suriya.linaexam.data.model.detail.CoinEntity

sealed class CoinItemViewType(val id: String, val viewType: String) {
    data class CoinItemView(val data: CoinEntity) :
        CoinItemViewType(data.uuid.orEmpty(), "CoinItemView")

    data class InviteFriendItemView(val index: Int) :
        CoinItemViewType(index.toString(), "InviteFriendItemView")
}
