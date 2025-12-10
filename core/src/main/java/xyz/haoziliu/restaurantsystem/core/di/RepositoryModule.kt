package xyz.haoziliu.restaurantsystem.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import xyz.haoziliu.restaurantsystem.core.data.repository.DeviceRepositoryImpl
import xyz.haoziliu.restaurantsystem.core.data.repository.MenuRepositoryImpl
import xyz.haoziliu.restaurantsystem.core.data.repository.OrderRepositoryImpl
import xyz.haoziliu.restaurantsystem.core.domain.repository.DeviceRepository
import xyz.haoziliu.restaurantsystem.core.domain.repository.MenuRepository
import xyz.haoziliu.restaurantsystem.core.domain.repository.OrderRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMenuRepository(impl: MenuRepositoryImpl): MenuRepository

    @Binds
    @Singleton
    abstract fun bindDeviceRepository(impl: DeviceRepositoryImpl): DeviceRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository
}