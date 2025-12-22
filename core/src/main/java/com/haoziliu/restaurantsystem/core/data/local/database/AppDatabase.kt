package com.haoziliu.restaurantsystem.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.haoziliu.restaurantsystem.core.data.local.dao.MenuDao
import com.haoziliu.restaurantsystem.core.data.local.model.MenuCachedEntity

@Database(entities = [MenuCachedEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun menuDao(): MenuDao
}
