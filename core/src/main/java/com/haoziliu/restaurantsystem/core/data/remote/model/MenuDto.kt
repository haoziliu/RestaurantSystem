package com.haoziliu.restaurantsystem.core.data.remote.model

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.SerializedName
import com.haoziliu.restaurantsystem.core.data.local.model.MenuCachedEntity
import com.haoziliu.restaurantsystem.core.domain.model.MenuCategory
import com.haoziliu.restaurantsystem.core.domain.model.MenuItem
import com.haoziliu.restaurantsystem.core.domain.model.MenuModifierGroup
import com.haoziliu.restaurantsystem.core.domain.model.MenuModifierOption
import com.haoziliu.restaurantsystem.core.domain.model.ModifierSelectionType

data class MenuDto(
    var categories: List<CategoryDto> = emptyList(),

    @SerializedName("last_updated")
    @get:PropertyName("last_updated")
    @set:PropertyName("last_updated")
    var lastUpdated: Long = 0
)

data class CategoryDto(
    var id: String = "",
    var name: String = "",
    var items: List<MenuItemDto> = emptyList()
)

data class MenuItemDto(
    var id: String = "",
    var name: String = "",
    var description: String? = null,
    var price: Double = 0.0,
    @SerializedName("image_url")
    @get:PropertyName("image_url")
    @set:PropertyName("image_url")
    var imageUrl: String? = null,
    @SerializedName("is_available")
    @get:PropertyName("is_available")
    @set:PropertyName("is_available")
    var isAvailable: Boolean = true,
    @SerializedName("modifier_groups")
    @get:PropertyName("modifier_groups")
    @set:PropertyName("modifier_groups")
    var modifierGroups: List<ModifierGroupDto> = emptyList()
)

data class ModifierGroupDto(
    var id: String = "",
    var title: String = "",
    @SerializedName("selection_type")
    @get:PropertyName("selection_type")
    @set:PropertyName("selection_type")
    var selectionType: String = "SINGLE_SELECT", // Firestore 存 String，代码里转 Enum
    @SerializedName("is_required")
    @get:PropertyName("is_required")
    @set:PropertyName("is_required")
    var isRequired: Boolean = false,
    var options: List<ModifierOptionDto> = emptyList()
)

data class ModifierOptionDto(
    var id: String = "",
    var label: String = "",
    @SerializedName("price_delta")
    @get:PropertyName("price_delta")
    @set:PropertyName("price_delta")
    var priceDelta: Double = 0.0
)

fun MenuDto.toEntity(): MenuCachedEntity {
    return MenuCachedEntity(
        lastUpdated = this.lastUpdated,
        categories = this.categories.map { cat ->
            MenuCategory(
                id = cat.id,
                name = cat.name,
                items = cat.items.map { item ->
                    MenuItem(
                        id = item.id,
                        name = item.name,
                        description = item.description,
                        price = item.price,
                        imageUrl = item.imageUrl,
                        isAvailable = item.isAvailable,
                        modifierGroups = item.modifierGroups.map { group ->
                            MenuModifierGroup(
                                id = group.id,
                                title = group.title,
                                selectionType = try {
                                    ModifierSelectionType.valueOf(group.selectionType)
                                } catch (e: Exception) {
                                    ModifierSelectionType.SINGLE_SELECT
                                },
                                isRequired = group.isRequired,
                                options = group.options.map { opt ->
                                    MenuModifierOption(
                                        id = opt.id,
                                        label = opt.label,
                                        priceDelta = opt.priceDelta
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}