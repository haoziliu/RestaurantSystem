package xyz.haoziliu.restaurantsystem.corecore.domain.model

data class Menu(
    val categories: List<MenuCategory>,
    val lastUpdated: Long //用于检查版本
)
data class MenuCategory(
    val id: String,
    val name: String,
    val items: List<MenuItem>
)

data class MenuItem(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUrl: String?,
    val isAvailable: Boolean = true, // 即使本地读取，这个字段也建议保留，后续sync可单独更新状态
    val modifierGroups: List<MenuModifierGroup> // 关联的可选项配置
)

data class MenuModifierGroup(
    val id: String,
    val title: String, // e.g., "辣度", "加料"
    val selectionType: ModifierSelectionType, // SINGLE, MULTIPLE
    val isRequired: Boolean,
    val options: List<MenuModifierOption>
)

data class MenuModifierOption(
    val id: String,
    val label: String, // e.g., "微辣", "加卤蛋"
    val priceDelta: Double // 0.0 为免费
)

enum class ModifierSelectionType {
    SINGLE_SELECT, // RadioButton
    MULTI_SELECT   // Checkbox
}