package xyz.haoziliu.restaurantsystem.app_admin.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import xyz.haoziliu.restaurantsystem.core.domain.model.Order
import xyz.haoziliu.restaurantsystem.core.domain.model.OrderStatus
import xyz.haoziliu.restaurantsystem.core.domain.usecase.ObserveActiveOrdersUseCase
import xyz.haoziliu.restaurantsystem.core.domain.usecase.UpdateOrderStatusUseCase
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    observeActiveOrdersUseCase: ObserveActiveOrdersUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase
) : ViewModel() {

    val uiState: StateFlow<AdminUiState> = observeActiveOrdersUseCase()
        .map { orders ->
            AdminUiState(orders = orders, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AdminUiState(isLoading = true)
        )

    // 动作：收款 (Cashier) -> 变为 "制作中"
    fun markAsPaid(order: Order) {
        updateStatus(order.id, OrderStatus.PAID) // 或者直接 PREPARING，看流程定义
    }

    // 动作：开始制作 (Kitchen) -> 变为 "制作中"
    fun markAsPreparing(order: Order) {
        updateStatus(order.id, OrderStatus.PREPARING)
    }

    // 动作：出餐 (Kitchen) -> 变为 "请取餐"
    fun markAsReady(order: Order) {
        updateStatus(order.id, OrderStatus.READY)
    }

    // 动作：完成 (Pick up) -> 归档
    fun markAsCompleted(order: Order) {
        updateStatus(order.id, OrderStatus.COMPLETED)
    }

    private fun updateStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            updateOrderStatusUseCase(orderId, status)
                .onFailure { e -> e.printStackTrace() }
        }
    }
}