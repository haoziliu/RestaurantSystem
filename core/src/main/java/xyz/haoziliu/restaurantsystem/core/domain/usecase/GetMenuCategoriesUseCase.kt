package xyz.haoziliu.restaurantsystem.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import xyz.haoziliu.restaurantsystem.core.domain.model.MenuCategory
import xyz.haoziliu.restaurantsystem.core.domain.repository.MenuRepository
import javax.inject.Inject

class GetMenuCategoriesUseCase @Inject constructor(private val repository: MenuRepository) {
    operator fun invoke(): Flow<List<MenuCategory>> {
        return repository.getMenu()
            .map { menu -> 
                 // 这里可以加逻辑：比如过滤掉 items 为空的 category
                 menu.categories.filter { it.items.isNotEmpty() }
            }
    }
}