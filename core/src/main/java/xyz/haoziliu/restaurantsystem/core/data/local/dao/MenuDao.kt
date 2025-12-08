package xyz.haoziliu.restaurantsystem.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import xyz.haoziliu.restaurantsystem.core.data.local.model.MenuCachedEntity

@Dao
interface MenuDao {
    @Query("SELECT * FROM menu_cache WHERE id = 'current_menu'")
    fun getMenu(): Flow<MenuCachedEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMenu(menu: MenuCachedEntity)
}