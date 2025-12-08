package xyz.haoziliu.restaurantsystem.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    // 获取当前设备配置的 ID
    val kioskId: Flow<String?>

    // 获取设备的唯一硬件指纹 (用于防拷贝)
    suspend fun getDeviceFingerprint(): String

    // 设置 KioskId (通常在首次启动设置页调用)
    suspend fun setKioskId(id: String)
    
    // 检查设备是否已授权
    suspend fun isDeviceAuthorized(): Boolean
}