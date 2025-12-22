package com.haoziliu.restaurantsystem.core.domain.usecase

import com.haoziliu.restaurantsystem.core.domain.repository.CartRepository
import javax.inject.Inject

class ClearCartUseCase @Inject constructor(private val repository: CartRepository) {
   suspend operator fun invoke() {
       repository.clearCart()
    }
}