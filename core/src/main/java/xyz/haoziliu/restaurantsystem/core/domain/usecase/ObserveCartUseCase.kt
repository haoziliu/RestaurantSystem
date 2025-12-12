package xyz.haoziliu.restaurantsystem.core.domain.usecase

import xyz.haoziliu.restaurantsystem.core.domain.repository.CartRepository
import javax.inject.Inject

class ObserveCartUseCase @Inject constructor(private val repository: CartRepository) {
   operator fun invoke() = repository.cartItems
}