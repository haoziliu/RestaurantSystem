package com.haoziliu.restaurantsystem.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.haoziliu.restaurantsystem.core.data.repository.CartRepositoryImpl
import com.haoziliu.restaurantsystem.core.data.repository.DeviceRepositoryImpl
import com.haoziliu.restaurantsystem.core.data.repository.MenuRepositoryImpl
import com.haoziliu.restaurantsystem.core.data.repository.OrderRepositoryImpl
import com.haoziliu.restaurantsystem.core.domain.repository.CartRepository
import com.haoziliu.restaurantsystem.core.domain.repository.DeviceRepository
import com.haoziliu.restaurantsystem.core.domain.repository.MenuRepository
import com.haoziliu.restaurantsystem.core.domain.repository.OrderRepository
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

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository
}