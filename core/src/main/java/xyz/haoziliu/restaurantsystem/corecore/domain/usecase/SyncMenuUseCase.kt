package xyz.haoziliu.restaurantsystem.corecore.domain.usecase

import xyz.haoziliu.restaurantsystem.corecore.domain.repository.MenuRepository

class SyncMenuUseCase(private val repository: MenuRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.syncMenuFromRemote()
    }
}