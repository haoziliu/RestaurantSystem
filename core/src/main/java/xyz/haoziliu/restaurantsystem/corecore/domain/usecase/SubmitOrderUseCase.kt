package xyz.haoziliu.restaurantsystem.corecore.domain.usecase

import xyz.haoziliu.restaurantsystem.corecore.domain.model.Order
import xyz.haoziliu.restaurantsystem.corecore.domain.repository.OrderRepository

class SubmitOrderUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(order: Order): Result<String> {
        if (order.items.isEmpty()) {
            return Result.failure(Exception("Cannot place an empty order"))
        }
        // 这里还可以校验：必选的 Modifier 是否都选了？
        // (虽然 UI 层也会做，但 Domain 层做是兜底)
        
        return repository.createOrder(order)
    }
}