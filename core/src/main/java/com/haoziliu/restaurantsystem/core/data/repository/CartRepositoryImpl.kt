package com.haoziliu.restaurantsystem.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.haoziliu.restaurantsystem.core.domain.model.OrderItem
import com.haoziliu.restaurantsystem.core.domain.repository.CartRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // 必须是单例，保证整个 App 只有一辆购物车
class CartRepositoryImpl @Inject constructor() : CartRepository {

    // 内存中的购物车列表
    private val _cartItems = MutableStateFlow<List<OrderItem>>(emptyList())
    override val cartItems: Flow<List<OrderItem>> = _cartItems.asStateFlow()

    override suspend fun addItem(item: OrderItem) {
        _cartItems.update { currentList ->
            // 检查是否有完全相同的商品（ID、口味、加料都一样）
            val existingItem = currentList.find { it.isSameProductAs(item) }

            if (existingItem != null) {
                // 如果有，数量 +1
                currentList.map {
                    if (it == existingItem) it.copy(quantity = it.quantity + item.quantity)
                    else it
                }
            } else {
                // 如果没有，生成一个临时的唯一ID（防止列表Key冲突），加入列表
                // 注意：OrderItem 在 Domain 定义里没有 ID？
                // 如果 OrderItem.id 是指 "menuItemId"，我们需要一个额外的 "cartItemId" 来区分不同口味的同种菜
                // 这里假设我们先不处理复杂的 ID 问题，直接加进去
                currentList + item
            }
        }
    }

    override suspend fun removeItem(itemId: String) {
        _cartItems.update { currentList ->
            // 简单版：直接移除所有该 ID 的菜（MVP）
            // 进阶版应该根据 CartItemId 移除
            currentList.filterNot { it.menuItemId == itemId }
        }
    }

    override suspend fun clearCart() {
        _cartItems.value = emptyList()
    }

    override fun getCurrentTotal(): Double {
        return _cartItems.value.sumOf { it.itemTotal }
    }
    
    // 辅助方法：判断两个订单项是否属于“同一种配置”
    private fun OrderItem.isSameProductAs(other: OrderItem): Boolean {
        // 1. 菜品ID必须一样
        if (this.menuItemId != other.menuItemId) return false
        // 2. 选的 Options 必须完全一样 (Set 比较忽略顺序)
        val thisOptions = this.selectedOptions.map { it.optionId }.toSet()
        val otherOptions = other.selectedOptions.map { it.optionId }.toSet()
        return thisOptions == otherOptions
    }
}