package xyz.haoziliu.restaurantsystem.app_customer.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import xyz.haoziliu.restaurantsystem.app_customer.ui.cart.CartScreen
import xyz.haoziliu.restaurantsystem.app_customer.ui.menu.MenuScreen
import xyz.haoziliu.restaurantsystem.app_customer.ui.order.OrderSuccessScreen
import xyz.haoziliu.restaurantsystem.app_customer.ui.welcome.WelcomeScreen

@Composable
fun App() {
    val navController = rememberNavController()

    Scaffold { innerPadding ->
         NavHost(
            navController = navController,
            startDestination = Screen.Welcome.route, // 默认先进欢迎页
            modifier = Modifier.padding(innerPadding)
        ) {

            // 1. 欢迎页
            composable(Screen.Welcome.route) {
                // 点击欢迎页 -> 跳转菜单
                WelcomeScreen(onStartClicked = {
                    navController.navigate(Screen.Menu.route)
                })
            }

            // 2. 菜单页
            composable(Screen.Menu.route) {
                MenuScreen(
                    onCartClicked = { navController.navigate(Screen.Cart.route) }
                )
            }

            // 3. 购物车
             composable(Screen.Cart.route) {
                 CartScreen(
                     onOrderSuccess = { ticketNum ->
                         // 带着参数跳转到成功页
                         navController.navigate("success/$ticketNum") {
                             // 弹出回退栈，防止用户按返回键回到购物车
                             popUpTo(Screen.Menu.route) { inclusive = false }
                         }
                     }
                 )
             }

            // 4. 成功页
             composable(
                 route = "success/{ticketNum}",
                 arguments = listOf(navArgument("ticketNum") { type = NavType.StringType })
             ) { backStackEntry ->
                 val ticketNum = backStackEntry.arguments?.getString("ticketNum") ?: "???"
                 OrderSuccessScreen(
                     ticketNum = ticketNum,
                     onTimeout = {
                         // 返回菜单页/欢迎页
                         navController.navigate(Screen.Welcome.route) {
                             popUpTo(0) // 清空所有回退栈
                         }
                     }
                 )
             }
        }
    }
}