@file:OptIn(FlowPreview::class)

package com.suriya.linaexam.ui.screen

import android.content.Intent
import android.content.res.Configuration
import android.icu.text.DecimalFormat
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.suriya.linaexam.R
import com.suriya.linaexam.data.model.detail.CoinEntity
import com.suriya.linaexam.ui.screen.state.CoinItemViewType
import com.suriya.linaexam.ui.screen.state.CoinScreenState
import com.suriya.linaexam.ui.screen.state.CoinSearchScreenState
import com.suriya.linaexam.ui.theme.CoinFonts
import com.suriya.linaexam.ui.theme.CoinTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

private const val THREE = 3

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreenCompose(viewModel: MainViewModel = viewModel()) {
    val screenState by viewModel.screenState.collectAsState()
    val searchScreenState by viewModel.searchScreenState.collectAsState()
    var alertDialog by remember {
        mutableStateOf(Pair(false, -1))
    }
    var coinDetail by remember {
        mutableStateOf(CoinEntity())
    }
    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()
    val pullToRefreshState =
        rememberPullRefreshState(
            refreshing = viewModel.isRefresh,
            onRefresh = viewModel::refreshData,
        )
    val bottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
        )
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    LaunchedEffect(Unit) {
        snapshotFlow { viewModel.searchQuery }
            .debounce(1000L)
            .collect {
                if (it.isNotEmpty()) {
                    viewModel.searchCoin()
                }
            }
    }

    LaunchedEffect(Unit) {
        viewModel.inviteFriendState
            .collect {
                val intent =
                    Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Invite your friend.")
                        type = "text/plain"
                    }
                val sharedIntent = Intent.createChooser(intent, null)
                context.startActivity(sharedIntent)
            }
    }

    LaunchedEffect(Unit) {
        viewModel.showCoinDetail
            .collect {
                coinDetail = it
                launch {
                    bottomSheetState.show()
                }
            }
    }

    LaunchedEffect(Unit) {
        viewModel.showToast
            .collect {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
    }

    LaunchedEffect(Unit) {
        viewModel.showAlertDialog
            .collect {
                alertDialog = Pair(true, it)
            }
    }

    lazyListState.OnLoadMore {
        viewModel.loadMore()
    }

    lazyGridState.OnLoadMore {
        viewModel.loadMore()
    }

    when (val state = screenState) {
        CoinScreenState.ErrorScreen -> {
            ErrorScreen(
                message = stringResource(id = R.string.common_error_message),
                true,
                viewModel::loadData,
            )
        }

        CoinScreenState.LoadingScreen -> {
            LoadingScreen()
        }

        is CoinScreenState.SuccessScreen -> {
            ModalBottomSheetLayout(
                sheetState = bottomSheetState,
                sheetContent = {
                    CoinDetailBottomSheet(coinDetail)
                },
                sheetShape = RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp),
            ) {
                Column(
                    modifier =
                    Modifier
                        .fillMaxSize(),
                ) {
                    CustomSearchBar(viewModel.searchQuery) {
                        if (it.isEmpty()) {
                            viewModel.clearSearch()
                        }
                        viewModel.searchQuery = it
                    }
                    Divider()
                    Box(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .pullRefresh(pullToRefreshState),
                    ) {
                        if (viewModel.searchQuery.isNotEmpty()) {
                            SearchScreen(
                                searchScreenState,
                                configuration,
                                lazyListState,
                                lazyGridState,
                                viewModel.isShouldLoadMore,
                                viewModel::clickCoinItem,
                                viewModel::searchCoin,
                            )
                        } else {
                            if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                                PortraitCoinItemList(
                                    lazyListState,
                                    state.topRank,
                                    state.regular,
                                    viewModel.isShouldLoadMore,
                                    viewModel::clickCoinItem,
                                )
                            } else {
                                LandscapeCoinItemList(
                                    state.regular,
                                    state.topRank,
                                    lazyGridState,
                                    viewModel.isShouldLoadMore,
                                    viewModel::clickCoinItem,
                                )
                            }
                        }

                        PullRefreshIndicator(
                            refreshing = viewModel.isRefresh,
                            state = pullToRefreshState,
                            modifier = Modifier.align(Alignment.TopCenter),
                        )
                    }
                }
            }
        }
    }

    if (viewModel.isLoadingDialog) {
        Dialog(
            onDismissRequest = { viewModel.isLoadingDialog = false },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            ),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                CircularProgressIndicator()
            }
        }
    }

    if (alertDialog.first) {
        AlertDialog(onDismissRequest = { alertDialog = Pair(false, -1) }, buttons = {
            TextButton(onClick = { alertDialog = Pair(false, -1) }) {
                Text("OK")
            }
        }, text = {
            Text(text = stringResource(id = alertDialog.second))
        })
    }
}

@Composable
private fun SearchScreen(
    searchScreenState: CoinSearchScreenState,
    configuration: Configuration,
    lazyListState: LazyListState,
    lazyGridState: LazyGridState,
    shouldLoadMore: Boolean,
    onItemClick: (CoinItemViewType) -> Unit,
    onErrorClick: () -> Unit,
) {
    when (searchScreenState) {
        is CoinSearchScreenState.ErrorScreen -> {
            ErrorScreen(
                message = stringResource(id = R.string.common_error_message),
                true,
                onErrorClick,
            )
        }

        CoinSearchScreenState.LoadingScreen -> {
            LoadingScreen()
        }

        is CoinSearchScreenState.SuccessScreen -> {
            if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                PortraitCoinItemList(
                    lazyListState = lazyListState,
                    regular = searchScreenState.regular,
                    isShouldLoadMore = shouldLoadMore,
                    onItemClick = onItemClick,
                )
            } else {
                LandscapeCoinItemList(
                    regular = searchScreenState.regular,
                    lazyGridState = lazyGridState,
                    shouldLoadMore = shouldLoadMore,
                    onItemClick = onItemClick,
                )
            }
        }

        CoinSearchScreenState.ResultNotMatchScree -> {
            ErrorScreen(message = stringResource(id = R.string.not_match_error_message))
        }
    }
}

@Composable
private fun CustomSearchBar(
    search: String,
    onTextChange: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    BasicTextField(
        value = search,
        onValueChange = onTextChange,
        singleLine = true,
        textStyle =
        TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
        ),
        decorationBox = { innerTextField ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    modifier =
                    Modifier
                        .padding(start = 12.dp)
                        .size(24.dp),
                )
                Box(
                    modifier =
                    Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                ) {
                    if (search.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.search),
                            style =
                            TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = colorResource(id = R.color.light_gray_search_placeholder),
                            ),
                        )
                    }
                    innerTextField()
                }
                if (search.isNotEmpty()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = null,
                        modifier =
                        Modifier
                            .clickable {
                                onTextChange("")
                                focusManager.clearFocus()
                            }
                            .padding(16.dp)
                            .size(16.dp),
                    )
                }
            }
        },
        modifier =
        Modifier
            .padding(16.dp)
            .height(48.dp)
            .background(colorResource(id = R.color.light_gray_search), MaterialTheme.shapes.small)
            .fillMaxWidth(),
    )
}

@Composable
private fun PortraitCoinItemList(
    lazyListState: LazyListState,
    topRank: SnapshotStateList<CoinEntity>? = null,
    regular: List<CoinItemViewType>,
    isShouldLoadMore: Boolean,
    onItemClick: (CoinItemViewType) -> Unit,
) {
    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier =
        Modifier
            .fillMaxSize(),
    ) {
        topRank?.let {
            item {
                val title =
                    buildAnnotatedString {
                        append(stringResource(id = R.string.top))
                        withStyle(style = SpanStyle(Color.Red)) {
                            append(" $THREE ")
                        }
                        append(stringResource(id = R.string.rank))
                    }

                Column {
                    Text(
                        title,
                        style =
                        TextStyle(
                            fontFamily = CoinFonts,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        modifier = Modifier.padding(start = 8.dp),
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .padding(top = 12.dp),
                    ) {
                        topRank.forEach {
                            TopRankCoinCard(
                                it,
                                modifier =
                                Modifier
                                    .weight(1f)
                                    .clickable {
                                        onItemClick(CoinItemViewType.CoinItemView(it))
                                    },
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                stringResource(id = R.string.regular_title),
                style =
                TextStyle(
                    fontFamily = CoinFonts,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                ),
                modifier =
                Modifier
                    .padding(top = 4.dp, start = 8.dp),
            )
        }

        items(
            regular.size,
            key = { index -> regular[index].id },
            contentType = { index -> regular[index].viewType },
        ) { index ->
            when (val item = regular[index]) {
                is CoinItemViewType.CoinItemView -> {
                    RegularCoinCard(
                        coinEntity = item.data,
                        modifier =
                        Modifier.clickable {
                            onItemClick(item)
                        },
                    )
                }

                is CoinItemViewType.InviteFriendItemView -> {
                    InviteFriendCard(Modifier.clickable { onItemClick(item) })
                }
            }
        }

        if (isShouldLoadMore) {
            item {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun LandscapeCoinItemList(
    regular: List<CoinItemViewType>,
    topRank: SnapshotStateList<CoinEntity>? = null,
    lazyGridState: LazyGridState,
    shouldLoadMore: Boolean,
    onItemClick: (CoinItemViewType) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screen = configuration.screenHeightDp / 3

    LazyVerticalGrid(
        state = lazyGridState,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
    ) {
        topRank?.let {
            item(span = { GridItemSpan(maxLineSpan) }) {
                val title =
                    buildAnnotatedString {
                        append(stringResource(id = R.string.top))
                        withStyle(style = SpanStyle(Color.Red)) {
                            append(" $THREE ")
                        }
                        append(stringResource(id = R.string.rank))
                    }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        title,
                        style =
                        TextStyle(
                            fontFamily = CoinFonts,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier =
                        Modifier
                            .padding(top = 12.dp),
                    ) {
                        topRank.forEach {
                            TopRankCoinCard(
                                it,
                                modifier =
                                Modifier
                                    .width(screen.dp)
                                    .clickable {
                                        onItemClick(CoinItemViewType.CoinItemView(it))
                                    },
                            )
                        }
                    }
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(id = R.string.regular_title),
                    style =
                    TextStyle(
                        fontFamily = CoinFonts,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier =
                    Modifier
                        .padding(top = 4.dp),
                )
            }
        }

        items(
            regular.size,
            key = { index -> regular[index].id },
            contentType = { index -> regular[index].viewType },
        ) { index ->
            when (val item = regular[index]) {
                is CoinItemViewType.CoinItemView -> {
                    RegularCoinCard(
                        coinEntity = item.data,
                        Modifier.clickable { onItemClick(item) },
                    )
                }

                is CoinItemViewType.InviteFriendItemView -> {
                    InviteFriendCard(Modifier.clickable { onItemClick(item) })
                }
            }
        }

        if (shouldLoadMore) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun LazyListState.OnLoadMore(loadMore: () -> Unit) {
    val shouldLoadMore =
        remember {
            derivedStateOf {
                val lastVisibleItem =
                    layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false

                lastVisibleItem.index == layoutInfo.totalItemsCount - 1
            }
        }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .collect {
                if (it) loadMore()
            }
    }
}

@Composable
private fun LazyGridState.OnLoadMore(loadMore: () -> Unit) {
    val shouldLoadMore =
        remember {
            derivedStateOf {
                val lastVisibleItem =
                    layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false

                lastVisibleItem.index == layoutInfo.totalItemsCount - 1
            }
        }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .collect {
                if (it) loadMore()
            }
    }
}

@Composable
private fun TopRankCoinCard(
    coinEntity: CoinEntity,
    modifier: Modifier = Modifier,
) {
    Card(
        backgroundColor = colorResource(id = R.color.white_card),
        elevation = 2.dp,
        shape = MaterialTheme.shapes.small,
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            AsyncImage(
                model =
                ImageRequest.Builder(LocalContext.current)
                    .data(coinEntity.iconUrl)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = null,
                modifier =
                Modifier
                    .size(40.dp)
                    .padding(top = 16.dp),
            )
            Text(
                coinEntity.symbol.orEmpty(),
                style =
                TextStyle(
                    fontFamily = CoinFonts,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                ),
            )
            Text(
                coinEntity.name.orEmpty(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style =
                TextStyle(
                    fontFamily = CoinFonts,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = colorResource(id = R.color.light_gray),
                ),
            )
            PriceChange(change = coinEntity.change?.toDouble() ?: 0.0)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun RegularCoinCard(
    coinEntity: CoinEntity,
    modifier: Modifier = Modifier,
) {
    Card(
        backgroundColor = colorResource(id = R.color.white_card),
        elevation = 2.dp,
        shape = MaterialTheme.shapes.small,
        modifier = modifier.height(82.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model =
                ImageRequest.Builder(LocalContext.current)
                    .data(coinEntity.iconUrl)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = null,
                modifier =
                Modifier
                    .padding(horizontal = 16.dp)
                    .size(40.dp),
            )
            Column(
                modifier =
                Modifier
                    .padding(vertical = 20.dp)
                    .padding(end = 16.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = coinEntity.name.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style =
                        TextStyle(
                            fontSize = 16.sp,
                            fontFamily = CoinFonts,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.black),
                        ),
                        modifier =
                        Modifier
                            .alignByBaseline()
                            .weight(1f, fill = false),
                    )
                    Text(
                        text = "$${
                            DecimalFormat("###,###,##0.00000").format(
                                coinEntity.price?.toDouble() ?: 0,
                            )
                        }",
                        maxLines = 1,
                        style =
                        TextStyle(
                            fontSize = 12.sp,
                            fontFamily = CoinFonts,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.black),
                        ),
                        modifier = Modifier.alignByBaseline(),
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier =
                    Modifier
                        .padding(top = 6.dp)
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = coinEntity.symbol.orEmpty(),
                        style =
                        TextStyle(
                            fontSize = 14.sp,
                            fontFamily = CoinFonts,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.light_gray),
                        ),
                        modifier = Modifier.alignByBaseline(),
                    )
                    PriceChange(
                        change = coinEntity.change?.toDouble() ?: 0.0,
                        modifier = Modifier.alignByBaseline(),
                    )
                }
            }
        }
    }
}

@Composable
private fun InviteFriendCard(modifier: Modifier = Modifier) {
    val textAnnotation =
        buildAnnotatedString {
            append(stringResource(id = R.string.earn_label))
            withStyle(SpanStyle(Color.Blue)) {
                append(stringResource(id = R.string.invite_friend_label))
            }
        }
    Card(
        backgroundColor = colorResource(id = R.color.light_blue),
        elevation = 2.dp,
        shape = MaterialTheme.shapes.small,
        modifier =
        modifier
            .height(82.dp)
            .fillMaxWidth(),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_gift),
                contentDescription = null,
                modifier =
                Modifier
                    .padding(vertical = 20.dp)
                    .padding(start = 16.dp)
                    .background(Color.White, CircleShape)
                    .size(40.dp)
                    .padding(9.dp),
            )
            Text(
                textAnnotation,
                style =
                TextStyle(
                    fontSize = 16.sp,
                    fontFamily = CoinFonts,
                    fontWeight = FontWeight.Normal,
                ),
                modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
private fun PriceChange(
    change: Double,
    modifier: Modifier = Modifier,
) {
    val isDown = change >= 0
    val text = DecimalFormat("#,##0.00").format(change)
    val textColor =
        if (isDown) {
            colorResource(id = R.color.red)
        } else {
            colorResource(id = R.color.light_green)
        }
    val arrowIcon =
        if (isDown) {
            painterResource(id = R.drawable.ic_arrow_down_red)
        } else {
            painterResource(id = R.drawable.ic_arrow_up_green)
        }
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Image(painter = arrowIcon, contentDescription = null, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = text,
            style =
            TextStyle(
                fontSize = 12.sp,
                fontFamily = CoinFonts,
                fontWeight = FontWeight.Bold,
                color = textColor,
            ),
        )
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    showRetry: Boolean = false,
    onRetryClick: () -> Unit = {},
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Sorry",
                style =
                TextStyle(
                    fontSize = 20.sp,
                    fontFamily = CoinFonts,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = message,
                style =
                TextStyle(
                    fontSize = 16.sp,
                    fontFamily = CoinFonts,
                    fontWeight = FontWeight.Normal,
                    color = colorResource(id = R.color.light_gray),
                ),
            )
            if (showRetry) {
                Button(onClick = onRetryClick) {
                    Text(text = stringResource(id = R.string.retry))
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator()
    }
}

@Composable
@Preview
private fun PreviewInviteFriendCard() {
    CoinTheme {
        InviteFriendCard()
    }
}

@Composable
@Preview
private fun PreviewCustomSearch() {
    CoinTheme {
        CustomSearchBar(search = "abc", onTextChange = {})
    }
}

@Composable
@Preview
private fun PreviewErrorScreen() {
    CoinTheme {
        ErrorScreen(message = "No result match this keyword", true)
    }
}
