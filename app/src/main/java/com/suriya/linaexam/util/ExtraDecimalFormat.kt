package com.suriya.linaexam.util

import android.content.Context
import android.icu.text.DecimalFormat
import com.suriya.linaexam.R

class ExtraDecimalFormat(context: Context) {
    private val suffixMillion = context.getString(R.string.million)
    private val suffixBillion = context.getString(R.string.billion)
    private val suffixTrillion = context.getString(R.string.trillion)
    private val decimalFormat = DecimalFormat("0.00")

    fun format(number: Long): String {
        return when {
            number < MILLION -> number.toString()
            number < BILLION ->
                "${
                    decimalFormat.format(
                        calculateNumber(
                            number,
                            MILLION,
                        ),
                    )
                } $suffixMillion"

            number < TRILLION ->
                "${
                    decimalFormat.format(
                        calculateNumber(
                            number,
                            BILLION,
                        ),
                    )
                } $suffixBillion"

            else -> "${decimalFormat.format(calculateNumber(number, TRILLION))} $suffixTrillion"
        }
    }

    private fun calculateNumber(
        number: Long,
        div: Long,
    ) = number.times(100).div(div).times(0.01)

    private companion object {
        const val MILLION = 1000000L
        const val BILLION = 1000000000L
        const val TRILLION = 1000000000000L
    }
}
