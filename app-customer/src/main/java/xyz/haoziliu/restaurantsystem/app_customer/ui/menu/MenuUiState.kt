package xyz.haoziliu.restaurantsystem.app_customer.ui.menu

import xyz.haoziliu.restaurantsystem.core.domain.model.MenuCategory

data class MenuUiState(
    val categories: List<MenuCategory> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)