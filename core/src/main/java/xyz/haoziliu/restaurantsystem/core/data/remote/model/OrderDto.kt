package xyz.haoziliu.restaurantsystem.core.data.remote.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import xyz.haoziliu.restaurantsystem.core.domain.model.Order
import xyz.haoziliu.restaurantsystem.core.domain.model.OrderItem
import xyz.haoziliu.restaurantsystem.core.domain.model.OrderOption
import xyz.haoziliu.restaurantsystem.core.domain.model.OrderStatus
import java.util.Date

data class OrderDto(
    @get:PropertyName("id")
    var id: String = "",
    
    @get:PropertyName("kiosk_id")
    var kioskId: String = "",
    
    @get:PropertyName("ticket_num")
    var ticketNum: String = "",
    
    @get:PropertyName("items")
    var items: List<OrderItemDto> = emptyList(),
    
    @get:PropertyName("status")
    var status: String = "AWAITING_PAYMENT", // 存枚举的 name
    
    @get:PropertyName("note")
    var note: String? = null,
    
    @get:PropertyName("total_price")
    var totalPrice: Double = 0.0,
    
    @ServerTimestamp // 让 Firestore 自动记录服务器时间
    @get:PropertyName("created_at")
    var createdAt: Date? = null
)
data class OrderItemDto(
    var id: String = "", // menu item id
    var name: String = "",
    @get:PropertyName("base_price")
    var basePrice: Double = 0.0,
    var quantity: Int = 0,
    var options: List<OrderOptionDto> = emptyList()
)
data class OrderOptionDto(
    var id: String = "",
    var name: String = "",
    @get:PropertyName("price_delta")
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