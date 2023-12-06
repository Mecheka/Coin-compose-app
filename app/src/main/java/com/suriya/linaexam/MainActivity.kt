package com.suriya.linaexam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.suriya.linaexam.ui.screen.MainScreenCompose
import com.suriya.linaexam.ui.theme.CoinTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoinTheme {
                MainScreenCompose()
            }
        }
    }
}
