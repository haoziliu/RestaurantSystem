package com.haoziliu.restaurantsystem.app_customer.ui

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")      // 屏保/欢迎页
    object Menu : Screen("menu")            // 点单主页
    object Cart : Screen("cart")            // 购物车
    object OrderSuccess : Screen("success") // 下单成功(显示取餐号)
}