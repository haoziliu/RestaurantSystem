package xyz.haoziliu.restaurantsystem.core.domain.usecase

import xyz.haoziliu.restaurantsystem.core.domain.model.MenuItem
import xyz.haoziliu.restaurantsystem.core.domain.model.OrderItem
import xyz.haoziliu.restaurantsystem.core.domain.model.OrderOption
import xyz.haoziliu.restaurantsystem.core.domain.repository.CartRepository
import javax.inject.Inject

class AddItemToCartUseCase @Inject constructor(private val repository: CartRepository) {
   suspend operator fun invoke(menuItem: MenuItem, options: List<OrderOption>) {
       val orderItem = OrderItem(
           menuItemId = menuItem.id,
           menuItemName = menuItem.name,
           basePrice = menuItem.price,
           quantity = 1,
           selectedOptions = options
       )
        repository.addItem(orderItem)
    }
}