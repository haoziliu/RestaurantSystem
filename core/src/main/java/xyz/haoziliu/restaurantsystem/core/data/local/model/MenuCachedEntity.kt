package xyz.haoziliu.restaurantsystem.core.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import xyz.haoziliu.restaurantsystem.core.domain.model.Menu
import xyz.haoziliu.restaurantsystem.core.domain.model.MenuCategory

@Entity(tableName = "menu_cache")
data class MenuCachedEntity(
    @PrimaryKey
    val id: String = "current_menu", // 我们只需要一行记录
    val categories: List<MenuCategory>, // 使用上面的 TypeConverter 存成 JSON
    val lastUpdated: Long
)

// 扩展函数：Entity -> Domain
fun MenuCachedEntity.toDomain(): Menu {
    return Menu(
        categories = this.categories,
        lastUpdated = this.lastUpdated
    )
}

// 扩展函数：Domain -> Entity
fun Menu.toEntity(): MenuCachedEntity {
    return MenuCachedEntity(
        categories = this.categories,
        lastUpdated = this.lastUpdated
    )
}