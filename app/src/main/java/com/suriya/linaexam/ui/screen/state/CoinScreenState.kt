package com.suriya.linaexam.ui.screen.state

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.suriya.linaexam.data.model.detail.CoinEntity

sealed class CoinScreenState {
    data object LoadingScreen : CoinScreenState()

    data object ErrorScreen : CoinScreenState()

    data class SuccessScreen(
        val topRank: SnapshotStateList<CoinEntity>? = null,
        val regular: List<CoinItemViewType>,
    ) : CoinScreenState()
}
