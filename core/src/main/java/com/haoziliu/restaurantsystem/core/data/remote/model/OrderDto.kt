package com.haoziliu.restaurantsystem.core.data.remote.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import com.haoziliu.restaurantsystem.core.domain.model.Order
import com.haoziliu.restaurantsystem.core.domain.model.OrderItem
import com.haoziliu.restaurantsystem.core.domain.model.OrderOption
import com.haoziliu.restaurantsystem.core.domain.model.OrderStatus
import java.util.Date

data class OrderDto(
    var id: String = "",

    @SerializedName("kiosk_id")
    @get:PropertyName("kiosk_id")
    @set:PropertyName("kiosk_id")
    var kioskId: String = "",

    @SerializedName("ticket_num")
    @get:PropertyName("ticket_num")
    @set:PropertyName("ticket_num")
    var ticketNum: String = "",
    
    var items: List<OrderItemDto> = emptyList(),
    
    var status: String = "AWAITING_PAYMENT", // 存枚举的 name
    
    var note: String? = null,

    @SerializedName("total_price")
    @get:PropertyName("total_price")
    @set:PropertyName("total_price")
    var totalPrice: Double = 0.0,

    @ServerTimestamp // 让 Firestore 自动记录服务器时间
    @SerializedName("created_at")
    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Date? = null
)
data class OrderItemDto(
    var id: String = "", // menu item id
    var name: String = "",
    @SerializedName("base_price")
    @get:PropertyName("base_price")
    @set:PropertyName("base_price")
    var basePrice: Double = 0.0,
    var quantity: Int = 0,
    var options: List<OrderOptionDto> = emptyList()
)
data class OrderOptionDto(
    var id: String = "",
    var name: String = "",
    @SerializedName("price_delta")
    @get:PropertyName("price_delta")
    @set:PropertyName("price_delta")
    var priceDelta: Double = 0.0
)
fun Order.toDto(): OrderDto {
    return OrderDto(
        // id 不传，让 Firestore 自动生成后我们再回填，或者用 UseCase 生成的 UUID
        kioskId = this.kioskId,
        ticketNum = this.ticketNum,
        status = this.status.name,
        note = this.note,
        totalPrice = this.totalPrice, // 把计算结果存进去，方便后端统计
        items = this.items.map { item ->
            OrderItemDto(
                id = item.menuItemId,
                name = item.menuItemName,
                basePrice = item.basePrice,
                quantity = item.quantity,
                options = item.selectedOptions.map { opt ->
                    OrderOptionDto(opt.optionId, opt.name, opt.priceDelta)
                }
            )
        }
    )
}
fun OrderDto.toDomain(): Order {
    return Order(
        id = this.id,
        kioskId = this.kioskId,
        ticketNum = this.ticketNum,
        status = try { OrderStatus.valueOf(this.status) } catch (e: Exception) { OrderStatus.AWAITING_PAYMENT },
        note = this.note,
        createdAt = this.createdAt?.time ?: System.currentTimeMillis(),
        items = this.items.map { item ->
            OrderItem(
                menuItemId = item.id,
                menuItemName = item.name,
                basePrice = item.basePrice,
                quantity = item.quantity,
                selectedOptions = item.options.map { opt ->
                    OrderOption(opt.id, opt.name, opt.priceDelta)
                }
            )
        }
        // Domain 里的 totalPrice 是计算属性，不需要从 DTO 映射
    )
}