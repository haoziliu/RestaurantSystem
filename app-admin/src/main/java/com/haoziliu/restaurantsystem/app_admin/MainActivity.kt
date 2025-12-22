package com.haoziliu.restaurantsystem.app_admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import com.haoziliu.restaurantsystem.app_admin.ui.dashboard.AdminScreen
import com.haoziliu.restaurantsystem.app_admin.ui.theme.RestaurantSystemTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RestaurantSystemTheme {
                AdminScreen()
            }
        }
    }
}