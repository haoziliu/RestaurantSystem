package xyz.haoziliu.restaurantsystem.corecore.domain.repository

import kotlinx.coroutines.flow.Flow
import xyz.haoziliu.restaurantsystem.corecore.domain.model.Order
import xyz.haoziliu.restaurantsystem.corecore.domain.model.OrderStatus

interface OrderRepository {
    // 顾客端：只负责创建订单
    suspend fun createOrder(order: Order): Result<String>

    // 老板端：需要监听所有“进行中”的订单 (未支付、制作中、待取餐)
    // 这里的 Flow 会实时吐出 List<Order>，老板界面会自动刷新
    fun observeActiveOrders(): Flow<List<Order>>

    // 老板端：更新状态 (收款 -> PAID, 做完 -> READY)
    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit>
}