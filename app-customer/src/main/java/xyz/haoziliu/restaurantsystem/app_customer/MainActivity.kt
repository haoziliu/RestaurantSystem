package xyz.haoziliu.restaurantsystem.app_customer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import xyz.haoziliu.restaurantsystem.app_customer.ui.App
import xyz.haoziliu.restaurantsystem.app_customer.ui.theme.RestaurantSystemTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RestaurantSystemTheme {
                App()
            }
        }
    }
}