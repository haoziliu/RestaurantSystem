package com.haoziliu.restaurantsystem.core.domain.usecase

import com.haoziliu.restaurantsystem.core.domain.model.MenuItem
import com.haoziliu.restaurantsystem.core.domain.model.OrderItem
import com.haoziliu.restaurantsystem.core.domain.model.OrderOption
import com.haoziliu.restaurantsystem.core.domain.repository.CartRepository
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