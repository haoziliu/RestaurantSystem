package xyz.haoziliu.restaurantsystem.corecore.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import xyz.haoziliu.restaurantsystem.corecore.domain.model.MenuCategory
import xyz.haoziliu.restaurantsystem.corecore.domain.repository.MenuRepository

class GetMenuCategoriesUseCase(private val repository: MenuRepository) {
    operator fun invoke(): Flow<List<MenuCategory>> {
        return repository.getMenu()
            .map { menu -> 
                 // 这里可以加逻辑：比如过滤掉 items 为空的 category
                 menu.categories.filter { it.items.isNotEmpty() }
            }
    }
}