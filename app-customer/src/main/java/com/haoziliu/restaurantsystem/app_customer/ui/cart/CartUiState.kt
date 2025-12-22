package com.haoziliu.restaurantsystem.app_customer.ui.cart

import com.haoziliu.restaurantsystem.core.domain.model.OrderItem

data class CartUiState(
    val items: List<OrderItem> = emptyList(),
    val total: Double = 0.0,
    val isSubmitting: Boolean = false, // 防止重复点击
    val orderSuccessTicket: String? = null, // 下单成功后拿到的取餐号
    val error: String? = null
)