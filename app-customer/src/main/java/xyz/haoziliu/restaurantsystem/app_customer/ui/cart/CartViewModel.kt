package xyz.haoziliu.restaurantsystem.app_customer.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import xyz.haoziliu.restaurantsystem.core.domain.model.Order
import xyz.haoziliu.restaurantsystem.core.domain.model.OrderStatus
import xyz.haoziliu.restaurantsystem.core.domain.repository.DeviceRepository
import xyz.haoziliu.restaurantsystem.core.domain.usecase.ClearCartUseCase
import xyz.haoziliu.restaurantsystem.core.domain.usecase.GenerateTicketNumUseCase
import xyz.haoziliu.restaurantsystem.core.domain.usecase.ObserveCartUseCase
import xyz.haoziliu.restaurantsystem.core.domain.usecase.RemoveItemFromCartUseCase
import xyz.haoziliu.restaurantsystem.core.domain.usecase.SubmitOrderUseCase
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val removeItemFromCartUseCase: RemoveItemFromCartUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val observeCartUseCase: ObserveCartUseCase,
    private val submitOrderUseCase: SubmitOrderUseCase,
    private val generateTicketNumUseCase: GenerateTicketNumUseCase,
    private val deviceRepository: DeviceRepository // 需要获取 KioskId
) : ViewModel() {

    private val _submissionState = MutableStateFlow<SubmissionState>(SubmissionState.Idle)

    // 组合数据流：监听购物车变化，自动计算总价
    // 合并流：购物车数据 + 提交状态
    val uiState: StateFlow<CartUiState> = combine(
        observeCartUseCase(),
        _submissionState
    ) { items, submission ->
        CartUiState(
            items = items,
            total = items.sumOf { it.itemTotal },
            isSubmitting = submission is SubmissionState.Loading,
            orderSuccessTicket = (submission as? SubmissionState.Success)?.ticketNum,
            error = (submission as? SubmissionState.Error)?.msg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CartUiState()
    )

    fun removeItem(itemId: String) {
        viewModelScope.launch { removeItemFromCartUseCase(itemId) }
    }

    fun clearCart() {
        viewModelScope.launch { clearCartUseCase() }
    }

    // 核心：提交订单
    fun submitOrder() {
        viewModelScope.launch {
            val currentItems = uiState.value.items
            if (currentItems.isEmpty()) return@launch

            _submissionState.value = SubmissionState.Loading

            val ticket = generateTicketNumUseCase()
            val order = Order(
                kioskId = "Kiosk-01", // 暂时写死，后续从 DeviceRepo 取
                ticketNum = ticket,
                items = currentItems,
                status = OrderStatus.AWAITING_PAYMENT
            )

            submitOrderUseCase(order)
                .onSuccess {
                    clearCartUseCase()
                    _submissionState.value = SubmissionState.Success(ticket)
                }
                .onFailure { e ->
                    _submissionState.value = SubmissionState.Error(e.message ?: "Unknown error")
                    // 3秒后重置错误显示
                    delay(3000)
                    _submissionState.value = SubmissionState.Idle
                }
        }
    }

    fun onOrderSuccessConsumed() {
        _submissionState.value = SubmissionState.Idle
    }
}