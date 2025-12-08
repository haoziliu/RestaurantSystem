package xyz.haoziliu.restaurantsystem.core.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import xyz.haoziliu.restaurantsystem.core.data.local.dao.MenuDao
import xyz.haoziliu.restaurantsystem.core.data.local.model.toDomain
import xyz.haoziliu.restaurantsystem.core.data.remote.model.MenuDto
import xyz.haoziliu.restaurantsystem.core.data.remote.model.toEntity
import xyz.haoziliu.restaurantsystem.core.domain.model.Menu
import xyz.haoziliu.restaurantsystem.core.domain.repository.MenuRepository
import javax.inject.Inject

class MenuRepositoryImpl @Inject constructor(
    private val menuDao: MenuDao,
    private val firestore: FirebaseFirestore
) : MenuRepository {

    // 1. 读取：只从本地 Room 读，保证极速体验和离线可用
    override fun getMenu(): Flow<Menu> {
        return menuDao.getMenu().map { entity ->
            // 如果本地还没数据，返回一个空菜单或者 Loading 状态（这里简化为空）
            entity?.toDomain() ?: Menu(emptyList(), 0)
        }
    }

    // 2. 同步：从 Firestore 拉取 -> 覆盖 Room -> UI 自动更新
    override suspend fun syncMenuFromRemote(): Result<Unit> {
        return try {
            // 假设你在 Firestore 有一个 collection 叫 "restaurants"，里面有一个 document 叫 "default_menu"
            // 或者直接用 menus/current 结构
            val snapshot = firestore.collection("menus")
                .document("current") // 假设我们就这一个菜单文件
                .get()
                .await()

            val remoteMenu = snapshot.toObject(MenuDto::class.java)

            if (remoteMenu != null) {
                // 关键时刻：把远程数据转成 Entity，存入 Room
                menuDao.saveMenu(remoteMenu.toEntity())
                Result.success(Unit)
            } else {
                Result.failure(Exception("Remote menu not found"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}