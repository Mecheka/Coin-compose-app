package com.suriya.linaexam.ui.screen

import android.icu.text.DecimalFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.suriya.linaexam.R
import com.suriya.linaexam.data.model.detail.CoinEntity
import com.suriya.linaexam.ui.theme.CoinFonts
import com.suriya.linaexam.ui.theme.CoinTheme
import com.suriya.linaexam.util.ExtraDecimalFormat
import android.graphics.Color as GColor

@Composable
fun CoinDetailBottomSheet(coinEntity: CoinEntity) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp),
    ) {
        Row {
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
                        .size(50.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row {
                    Text(
                        text = coinEntity.name.orEmpty(),
                        style =
                            TextStyle(
                                color = coinEntity.color.getColor(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = CoinFonts,
                            ),
                    )
                    Text(
                        text = "(${coinEntity.symbol})",
                        style =
                            TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = CoinFonts,
                            ),
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
                CommonPriceSession(
                    title = stringResource(id = R.string.price),
                    price =
                        DecimalFormat("###,###,###,##0.00").format(
                            coinEntity.price?.toDouble() ?: 0,
                        ),
                )
                CommonPriceSession(
                    title = stringResource(id = R.string.market_cap),
                    price =
                        ExtraDecimalFormat(LocalContext.current).format(
                            coinEntity.marketCap?.toLong() ?: 0L,
                        ),
                )
            }
        }
        Text(
            text = coinEntity.description.orEmpty(),
            style =
                TextStyle(
                    fontSize = 14.sp,
                    fontFamily = CoinFonts,
                    fontWeight = FontWeight.Normal,
                    color =
                        colorResource(
                            id = R.color.light_gray,
                        ),
                ),
            modifier = Modifier.padding(vertical = 16.dp),
        )
        Divider()
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
        ) {
            Text(
                text = stringResource(id = R.string.go_to_web),
                style =
                    TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CoinFonts,
                        color =
                            colorResource(
                                id = R.color.light_blue_link,
                            ),
                    ),
                modifier =
                    Modifier.clickable {
                        uriHandler.openUri(coinEntity.websiteUrl.orEmpty())
                    },
            )
        }
    }
}

@Composable
private fun CommonPriceSession(
    title: String,
    price: String,
) {
    Row {
        Text(
            title,
            style =
                TextStyle(
                    fontSize = 12.sp,
                    fontFamily = CoinFonts,
                    fontWeight = FontWeight.Bold,
                ),
        )

        Text(
            "$ $price",
            style =
                TextStyle(
                    fontSize = 12.sp,
                    fontFamily = CoinFonts,
                    fontWeight = FontWeight.Normal,
                ),
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
@Preview
private fun PreviewCoinDetailBottomSheet() {
    CoinTheme {
        CoinDetailBottomSheet(
            CoinEntity(
                name = "Bitcoin",
                symbol = "BTC",
                price = "41718.20424802698",
                marketCap = "816077661056",
                description = "Bitcoin is a digital currency with a finite supply, allowing users to send/receive money without a central bank/government, often nicknamed \\\"Digital Gold\\\".",
            ),
        )
    }
}

fun String?.getColor(): Color {
    return try {
        Color(GColor.parseColor(this ?: "#000000"))
    } catch (_: IllegalArgumentException) {
        Color.Black
    }
}
