package com.haoziliu.restaurantsystem.app_admin.ui.dashboard

import com.haoziliu.restaurantsystem.core.domain.model.Order
import com.haoziliu.restaurantsystem.core.domain.model.OrderStatus

data class AdminUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
) {
    // 辅助属性：将订单自动分组，方便 UI 渲染
    val pendingOrders: List<Order> // 待支付
        get() = orders.filter { it.status == OrderStatus.AWAITING_PAYMENT }
    
    val preparingOrders: List<Order> // 制作中 (后厨关注)
        get() = orders.filter { it.status == OrderStatus.PREPARING || it.status == OrderStatus.PAID }
        
    val readyOrders: List<Order> // 待取餐 (前台/叫号屏关注)
        get() = orders.filter { it.status == OrderStatus.READY }
}

