package xyz.haoziliu.restaurantsystem.core

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import xyz.haoziliu.restaurantsystem.core.data.remote.model.MenuDto
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@RunWith(AndroidJUnit4::class)
class MenuSeederTest {

    @Test
    @Ignore("运维脚本：仅在需要重置菜单时手动移除此注解并运行") // ✅ 加这把锁
    fun seedMenuDataToFirestore() = runBlocking {
        // 0. 初始化 Firebase (防止测试环境下未自动初始化)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        if (FirebaseApp.getApps(context).isEmpty()) {
            val options = FirebaseOptions.Builder()
                .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
                .setApplicationId(BuildConfig.FIREBASE_APP_ID)
                .setApiKey(BuildConfig.FIREBASE_API_KEY)
                .build()

            FirebaseApp.initializeApp(context, options)
        }

        val firestore = FirebaseFirestore.getInstance()
        val gson = Gson()

        // 1. 解析 JSON
        println("Parsing JSON data...")
        val testContext = InstrumentationRegistry.getInstrumentation().context
        val inputStream = testContext.assets.open("menu_data.json")
        val reader = InputStreamReader(inputStream, StandardCharsets.UTF_8)
        val menuDto = gson.fromJson(reader, MenuDto::class.java)

        // 2. 写入 Firestore
        println("Uploading to Firestore: menus/current ...")

        try {
            firestore.collection("menus")
                .document("current")
                .set(menuDto)
                .await() // 等待上传完成

            println("✅ Success! Menu data has been seeded.")
        } catch (e: Exception) {
            println("❌ Error uploading data: ${e.message}")
            throw e
        }
    }
}