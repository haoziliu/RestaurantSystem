package com.haoziliu.restaurantsystem.core.domain.model

data class Order(
    val id: String = "",       // Firestore 的 UUID，用于系统唯一索引
    val kioskId: String,       // 设备ID (如 "Kiosk-01")，用于老板排查问题
    val ticketNum: String,     // 取餐号 (如 "A-12", "8821")，展示给顾客和老板看
    val items: List<OrderItem>,
    val status: OrderStatus = OrderStatus.AWAITING_PAYMENT, // 默认初始状态
    val note: String? = null,  // 整单备注
    val createdAt: Long = System.currentTimeMillis()
) {
    // 纯业务逻辑：计算总价
    val totalPrice: Double
        get() = items.sumOf { it.itemTotal }

    // 业务逻辑：计算总数量
    val totalQuantity: Int
        get() = items.sumOf { it.quantity }
}

data class OrderItem(
    val menuItemId: String,
    val menuItemName: String,
    val basePrice: Double,
    val quantity: Int,
    val selectedOptions: List<OrderOption>
) {
    val itemTotal: Double
        get() = (basePrice + selectedOptions.sumOf { it.priceDelta }) * quantity
}

data class OrderOption(
    val optionId: String, // 对应 MenuModifierOption.id
    val name: String,     // 对应 MenuModifierOption.label
    val priceDelta: Double
)

enum class OrderStatus {
    AWAITING_PAYMENT, // 下单成功，但未付钱。老板端能看到，但厨房不一定要做。
    PAID,             // 已付款。此时进入制作队列。
    PREPARING,        // 厨房开始制作。
    READY,            // 制作完成，叫号取餐。
    COMPLETED,        // 顾客已取走/订单结束。
    CANCELLED         // 弃单（比如顾客下单后没去付钱，直接走了）。
}