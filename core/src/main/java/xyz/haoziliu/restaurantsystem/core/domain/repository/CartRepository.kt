package xyz.haoziliu.restaurantsystem.core.domain.repository

import kotlinx.coroutines.flow.Flow
import xyz.haoziliu.restaurantsystem.core.domain.model.OrderItem

interface CartRepository {
    // 监听购物车内容 (流式更新，用于 UI 实时显示红点/总价)
    val cartItems: Flow<List<OrderItem>>

    // 添加商品 (如果已存在相同规格，则增加数量)
    suspend fun addItem(item: OrderItem)

    // 移除/减少商品
    suspend fun removeItem(itemId: String)

    // 清空购物车 (下单成功后调用)
    suspend fun clearCart()
    
    // 获取当前总价
    fun getCurrentTotal(): Double
}