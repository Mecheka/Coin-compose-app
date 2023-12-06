package com.suriya.linaexam.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suriya.linaexam.R
import com.suriya.linaexam.data.model.detail.CoinEntity
import com.suriya.linaexam.data.repository.CoinRepository
import com.suriya.linaexam.ui.screen.state.CoinItemViewType
import com.suriya.linaexam.ui.screen.state.CoinScreenState
import com.suriya.linaexam.ui.screen.state.CoinSearchScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor(private val coinRepository: CoinRepository) : ViewModel() {
        var isRefresh by mutableStateOf(false)
        var isShouldLoadMore by mutableStateOf(false)
            private set
        var isLoadingDialog by mutableStateOf(false)
        var searchQuery by mutableStateOf("")

        private val _inviteFriendState = Channel<Int>()
        val inviteFriendState = _inviteFriendState.receiveAsFlow()
        private val _showCoinDetail = Channel<CoinEntity>()
        val showCoinDetail = _showCoinDetail.receiveAsFlow()
        private val _showToast = Channel<Int>()
        val showToast = _showToast.receiveAsFlow()
        private val _showAlertDialog = Channel<Int>()
        val showAlertDialog = _showAlertDialog.receiveAsFlow()
        private val _screenState = MutableStateFlow<CoinScreenState>(CoinScreenState.LoadingScreen)
        val screenState = _screenState.asStateFlow()
        private val _searchScreenState =
            MutableStateFlow<CoinSearchScreenState>(CoinSearchScreenState.LoadingScreen)
        val searchScreenState = _searchScreenState.asStateFlow()

        private val originalSource = mutableListOf<CoinEntity>()
        private val searchOriginalSource = mutableListOf<CoinEntity>()
        private var currentOffset = DEFAULT_OFFSET // Default offset is 23 for first load
        private var currentSearchOffset = NEXT_OFFSET // Default offset is 20 for first load
        private val topRankState = mutableStateListOf<CoinEntity>()

        init {
            loadData()
        }

        fun refreshData() {
            isRefresh = true
            if (searchQuery.isNotEmpty()) {
                searchCoin(true)
            } else {
                loadData(true)
            }
        }

        fun searchCoin(onRefresh: Boolean = false) {
            currentSearchOffset = NEXT_OFFSET
            searchOriginalSource.clear()
            if (!onRefresh) {
                _searchScreenState.value = CoinSearchScreenState.LoadingScreen
            }
            viewModelScope.launch {
                coinRepository.getCoins(searchQuery)
                    .flowOn(Dispatchers.IO)
                    .catch {
                        _searchScreenState.value = CoinSearchScreenState.ErrorScreen
                    }
                    .collect { result ->
                        result.onSuccess { entity ->
                            if (entity.data?.coins.isNullOrEmpty()) {
                                _searchScreenState.value =
                                    CoinSearchScreenState.ResultNotMatchScree
                            } else {
                                isRefresh = false
                                searchOriginalSource.addAll(entity.data?.coins.orEmpty())
                                isShouldLoadMore =
                                    searchOriginalSource.size < (entity.data?.stats?.total ?: 0)

                                mappingSearchCoinList()
                            }
                        }.onFailure {
                            _searchScreenState.value = CoinSearchScreenState.ErrorScreen
                        }
                    }
            }
        }

        fun clearSearch() {
            searchOriginalSource.clear()
            mappingRegularCoinList()
        }

        fun loadMore() {
            if (searchQuery.isEmpty()) {
                regularLoadMore()
            } else {
                searchLoadMore()
            }
        }

        fun clickCoinItem(item: CoinItemViewType) {
            when (item) {
                is CoinItemViewType.CoinItemView -> {
                    loadCoinDetail(item.data.uuid.orEmpty())
                }

                is CoinItemViewType.InviteFriendItemView -> {
                    _inviteFriendState.trySend(item.index)
                }
            }
        }

        fun loadData(onRefresh: Boolean = false) {
            currentOffset = DEFAULT_OFFSET // Reset offset
            originalSource.clear()
            if (!onRefresh) {
                _screenState.value = CoinScreenState.LoadingScreen
            }
            viewModelScope.launch {
                coinRepository.getCoins(limit = "23")
                    .flowOn(Dispatchers.IO)
                    .catch {
                        _screenState.value = CoinScreenState.ErrorScreen
                    }.collect { result ->
                        result.onSuccess { entity ->
                            if (entity.data?.coins.isNullOrEmpty()) {
                                _screenState.value = CoinScreenState.ErrorScreen
                            } else {
                                isRefresh = false
                                topRankState.clear()
                                originalSource.addAll(entity.data?.coins.orEmpty())
                                isShouldLoadMore =
                                    originalSource.size < (entity.data?.stats?.total ?: 0)
                                originalSource.subList(0, 3).forEach {
                                    topRankState.add(it)
                                }
                                mappingRegularCoinList()
                            }
                        }.onFailure {
                            _screenState.value = CoinScreenState.ErrorScreen
                        }
                    }
            }
        }

        private fun regularLoadMore() {
            viewModelScope.launch {
                coinRepository.getCoins(offset = currentOffset)
                    .flowOn(Dispatchers.IO)
                    .catch {
                        _showToast.trySend(R.string.common_error_message)
                    }.collect { result ->
                        result.onSuccess { entity ->
                            if (entity.data?.coins.isNullOrEmpty()) {
                                isShouldLoadMore = false
                                _showToast.trySend(R.string.not_found_error_message)
                            } else {
                                currentOffset += NEXT_OFFSET
                                originalSource.addAll(entity.data?.coins.orEmpty())
                                isShouldLoadMore =
                                    originalSource.size < (entity.data?.stats?.total ?: 0)
                                mappingRegularCoinList()
                            }
                        }.onFailure {
                            _showToast.trySend(R.string.common_error_message)
                        }
                    }
            }
        }

        private fun searchLoadMore() {
            viewModelScope.launch {
                coinRepository.getCoins(searchQuery, currentOffset)
                    .flowOn(Dispatchers.IO)
                    .catch {
                        _showToast.trySend(R.string.common_error_message)
                    }.collect { result ->
                        result.onSuccess { entity ->
                            if (entity.data?.coins.isNullOrEmpty()) {
                                isShouldLoadMore = false
                                _showToast.trySend(R.string.not_found_error_message)
                            } else {
                                currentSearchOffset += NEXT_OFFSET
                                searchOriginalSource.addAll(entity.data?.coins.orEmpty())
                                isShouldLoadMore =
                                    searchOriginalSource.size < (entity.data?.stats?.total ?: 0)

                                mappingSearchCoinList()
                            }
                        }.onFailure {
                            _showToast.trySend(R.string.common_error_message)
                        }
                    }
            }
        }

        private fun mappingRegularCoinList() {
            val regular: MutableList<CoinItemViewType> =
                originalSource.subList(
                    3,
                    originalSource.size,
                ).map {
                    CoinItemViewType.CoinItemView(it)
                }.toMutableList()

            generateInviteFriendPosition(regular.size).forEach { expectedIndex ->
                if (expectedIndex <= regular.size) {
                    regular.add(
                        expectedIndex - 1,
                        CoinItemViewType.InviteFriendItemView(expectedIndex - 1),
                    )
                }
            }

            _screenState.value =
                CoinScreenState.SuccessScreen(topRankState, regular)
        }

        private fun mappingSearchCoinList() {
            val regular: MutableList<CoinItemViewType> =
                searchOriginalSource.map {
                    CoinItemViewType.CoinItemView(it)
                }.toMutableList()
            generateInviteFriendPosition(regular.size).forEach { expectedIndex ->
                if (expectedIndex <= regular.size) {
                    regular.add(
                        expectedIndex - 1,
                        CoinItemViewType.InviteFriendItemView(expectedIndex - 1),
                    )
                }
            }

            _searchScreenState.value = CoinSearchScreenState.SuccessScreen(regular)
        }

        private fun loadCoinDetail(uuid: String) {
            isLoadingDialog = true
            viewModelScope.launch {
                coinRepository.getCoinByUUID(uuid)
                    .flowOn(Dispatchers.Main)
                    .catch {
                        isLoadingDialog = false
                        _showAlertDialog.trySend(R.string.common_error_message)
                    }.collect { result ->
                        result.onSuccess { entity ->
                            isLoadingDialog = false
                            _showCoinDetail.trySend(entity.data?.coin ?: CoinEntity())
                        }.onFailure {
                            isLoadingDialog = false
                            _showAlertDialog.trySend(R.string.common_error_message)
                        }
                    }
            }
        }

        private fun generateInviteFriendPosition(total: Int): List<Int> {
            var default = 5
            val list = mutableListOf(default)
            while (default <= total) {
                default *= 2
                list.add(default)
            }
            return list
        }

        private companion object {
            const val NEXT_OFFSET = 20
            const val DEFAULT_OFFSET = 23
        }
    }
