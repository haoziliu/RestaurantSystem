package com.haoziliu.restaurantsystem.core.domain.usecase

import com.haoziliu.restaurantsystem.core.domain.model.OrderStatus
import com.haoziliu.restaurantsystem.core.domain.repository.OrderRepository
import javax.inject.Inject

class UpdateOrderStatusUseCase @Inject constructor(private val repository: OrderRepository) {
    suspend operator fun invoke(orderId: String, newStatus: OrderStatus): Result<Unit> {
        return repository.updateOrderStatus(orderId, newStatus)
    }
}