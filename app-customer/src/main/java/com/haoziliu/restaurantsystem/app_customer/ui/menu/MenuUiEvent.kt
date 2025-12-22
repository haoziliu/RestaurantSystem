package com.haoziliu.restaurantsystem.app_customer.ui.menu

sealed class MenuUiEvent {
    data class ShowToast(val message: String) : MenuUiEvent()
}