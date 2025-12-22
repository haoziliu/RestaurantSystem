package com.haoziliu.restaurantsystem.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.haoziliu.restaurantsystem.core.domain.repository.DeviceRepository
import java.util.UUID
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences> // Hilt 注入
) : DeviceRepository {

    private val KEY_KIOSK_ID = stringPreferencesKey("kiosk_id")

    override val kioskId: Flow<String?> = dataStore.data
        .map { preferences -> preferences[KEY_KIOSK_ID] }

    override suspend fun setKioskId(id: String) {
        dataStore.edit { it[KEY_KIOSK_ID] = id }
    }

    override suspend fun getDeviceFingerprint(): String {
        return UUID.randomUUID().toString()
    }

    override suspend fun isDeviceAuthorized(): Boolean {
        return dataStore.data.first()[KEY_KIOSK_ID] != null
    }
}