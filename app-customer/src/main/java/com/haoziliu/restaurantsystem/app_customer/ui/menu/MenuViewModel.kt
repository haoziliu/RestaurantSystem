package com.haoziliu.restaurantsystem.app_customer.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.haoziliu.restaurantsystem.core.domain.model.MenuItem
import com.haoziliu.restaurantsystem.core.domain.model.OrderOption
import com.haoziliu.restaurantsystem.core.domain.usecase.AddItemToCartUseCase
import com.haoziliu.restaurantsystem.core.domain.usecase.GetMenuCategoriesUseCase
import com.haoziliu.restaurantsystem.core.domain.usecase.ObserveCartUseCase
import com.haoziliu.restaurantsystem.core.domain.usecase.SyncMenuUseCase
import javax.inject.Inject


@HiltViewModel
class MenuViewModel @Inject constructor(
    getMenuCategoriesUseCase: GetMenuCategoriesUseCase,
    observeCartUseCase: ObserveCartUseCase,
    private val addItemToCartUseCase: AddItemToCartUseCase,
    private val syncMenuUseCase: SyncMenuUseCase
) : ViewModel() {

    // 实时监听数据库变化，转换为 UI State

    val uiState: StateFlow<MenuUiState> = combine(
        getMenuCategoriesUseCase(),
        observeCartUseCase()
    ) { categories, itemsList ->
        MenuUiState(categories = categories, isLoading = false, itemCountInCart = itemsList.size)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MenuUiState(isLoading = true)
        )

    private val _eventFlow = MutableSharedFlow<MenuUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun syncMenu() {
        viewModelScope.launch {
            syncMenuUseCase()
                .onFailure { e ->
                    _eventFlow.emit(MenuUiEvent.ShowToast("菜单更新失败！"))
                    e.printStackTrace()
                }
        }
    }

    fun addToCart(menuItem: MenuItem, options: List<OrderOption>) {
        viewModelScope.launch {
            addItemToCartUseCase(menuItem, options)
            _eventFlow.emit(MenuUiEvent.ShowToast("添加成功！"))
        }
    }
}