package com.haoziliu.restaurantsystem.core.domain.repository

import kotlinx.coroutines.flow.Flow
import com.haoziliu.restaurantsystem.core.domain.model.Menu

interface MenuRepository {
    // 1. 核心读取：直接从本地 Room 读取，速度快，零流量。
    // 返回 Flow 是因为如果这里有本地缓存更新，UI 应该响应。
    fun getMenu(): Flow<Menu>

    // 2. 手动同步：从 Firestore 拉取最新数据，覆盖本地 Room。
    // 返回 Result 以处理网络错误。
    suspend fun syncMenuFromRemote(): Result<Unit>
}