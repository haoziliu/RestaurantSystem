package xyz.haoziliu.restaurantsystem.core.domain.usecase

import xyz.haoziliu.restaurantsystem.core.domain.repository.MenuRepository
import javax.inject.Inject

class SyncMenuUseCase @Inject constructor(private val repository: MenuRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.syncMenuFromRemote()
    }
}