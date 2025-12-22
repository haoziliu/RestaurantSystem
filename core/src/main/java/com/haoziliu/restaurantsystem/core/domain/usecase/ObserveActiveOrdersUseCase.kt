package com.haoziliu.restaurantsystem.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import com.haoziliu.restaurantsystem.core.domain.model.Order
import com.haoziliu.restaurantsystem.core.domain.repository.OrderRepository
import javax.inject.Inject

class ObserveActiveOrdersUseCase @Inject constructor(private val repository: OrderRepository) {
    // 监听所有未完结的订单 (给老板/后厨看)
    operator fun invoke(): Flow<List<Order>> = repository.observeActiveOrders()
}