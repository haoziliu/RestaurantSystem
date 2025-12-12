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
import xyz.haoziliu.restaurantsystem.core.BuildConfig
import xyz.haoziliu.restaurantsystem.core.data.remote.model.MenuDto

@RunWith(AndroidJUnit4::class)
class MenuSeederTest {

    // 1. 这里填入你的菜单 JSON 数据
    // 这是一个包含两个分类、几个菜品和规格的完整示例
    private val json = """
    {
      "last_updated": 1709876543210,
      "categories": [
        {
          "id": "cat_1",
          "name": "🔥 热门推荐",
          "items": [
            {
              "id": "item_101",
              "name": "招牌安格斯牛肉堡",
              "description": "100% 纯进口安格斯牛肉，搭配特制秘方酱汁。",
              "price": 48.0,
              "image_url": "https://fakeimg.pl/400x400/?text=Burger", 
              "is_available": true,
              "modifier_groups": [
                {
                  "id": "mod_group_1",
                  "title": "口味选择",
                  "selection_type": "SINGLE_SELECT",
                  "is_required": true,
                  "options": [
                    { "id": "opt_1_1", "label": "原味", "price_delta": 0.0 },
                    { "id": "opt_1_2", "label": "黑胡椒", "price_delta": 0.0 },
                    { "id": "opt_1_3", "label": "变态辣", "price_delta": 0.0 }
                  ]
                },
                {
                  "id": "mod_group_2",
                  "title": "加料",
                  "selection_type": "MULTI_SELECT",
                  "is_required": false,
                  "options": [
                    { "id": "opt_2_1", "label": "加芝士片", "price_delta": 3.0 },
                    { "id": "opt_2_2", "label": "加培根", "price_delta": 5.0 }
                  ]
                }
              ]
            },
            {
              "id": "item_102",
              "name": "黄金脆皮炸鸡 (全翅)",
              "description": "外酥里嫩，鲜嫩多汁。",
              "price": 28.5,
              "image_url": "https://fakeimg.pl/400x400/?text=Chicken",
              "is_available": true,
              "modifier_groups": []
            }
          ]
        },
        {
          "id": "cat_2",
          "name": "🥤 快乐肥宅水",
          "items": [
            {
              "id": "item_201",
              "name": "冰镇可乐",
              "description": "加冰才好喝。",
              "price": 8.0,
              "image_url": "https://fakeimg.pl/400x400/?text=Coke",
              "is_available": true,
              "modifier_groups": []
            },
            {
              "id": "item_202",
              "name": "鲜榨橙汁",
              "description": "新鲜橙子现榨，无添加。",
              "price": 18.0,
              "image_url": "https://fakeimg.pl/400x400/?text=Juice",
              "is_available": true,
              "modifier_groups": []
            }
          ]
        }
      ]
    }
    """.trimIndent()

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
        val menuDto = gson.fromJson(json, MenuDto::class.java)

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