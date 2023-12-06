package com.suriya.linaexam.ui.screen.state

sealed class CoinSearchScreenState {
    data object LoadingScreen : CoinSearchScreenState()

    data object ErrorScreen : CoinSearchScreenState()

    data object ResultNotMatchScree : CoinSearchScreenState()

    data class SuccessScreen(
        val regular: List<CoinItemViewType>,
    ) : CoinSearchScreenState()
}
