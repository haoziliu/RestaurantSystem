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

    private val json = """
    {
      "last_updated": ${System.currentTimeMillis()},
      "categories": [
        {
          "id": "cat_main_01",
          "name": "🍜 招牌面食 / Noodles",
          "items": [
            {
              "id": "item_beef_noodle",
              "name": "私房红烧牛肉面",
              "description": "秘制汤底，大块牛肉，劲道面条。 (Chef's Special Beef Noodles)",
              "price": 12.80,
              "image_url": "https://www.miammiamcool.fr/media/images/gallery/20/big/21.jpg", 
              "is_available": true,
              "modifier_groups": [
                {
                  "id": "mod_spicy_level",
                  "title": "辣度选择 / Spicy Level",
                  "selection_type": "SINGLE_SELECT",
                  "is_required": true,
                  "options": [
                    { "id": "opt_spicy_0", "label": "不辣 / No Spicy", "price_delta": 0.0 },
                    { "id": "opt_spicy_1", "label": "微辣 / Mild", "price_delta": 0.0 },
                    { "id": "opt_spicy_2", "label": "中辣 / Medium", "price_delta": 0.0 },
                    { "id": "opt_spicy_3", "label": "大辣 / Hot", "price_delta": 0.0 }
                  ]
                },
                {
                  "id": "mod_extras",
                  "title": "加料 / Extras",
                  "selection_type": "MULTI_SELECT",
                  "is_required": false,
                  "options": [
                    { "id": "opt_egg", "label": "卤蛋 / Marinated Egg", "price_delta": 1.50 },
                    { "id": "opt_meat", "label": "加肉 / Extra Beef", "price_delta": 4.00 },
                    { "id": "opt_coriander", "label": "不要香菜 / No Coriander", "price_delta": 0.0 }
                  ]
                }
              ]
            },
            {
              "id": "item_dumpling",
              "name": "手工水饺 (12个)",
              "description": "猪肉白菜馅，皮薄馅大。",
              "price": 9.50,
              "image_url": "https://fakeimg.pl/400x400/?text=Dumplings",
              "is_available": true,
              "modifier_groups": []
            }
          ]
        },
        {
          "id": "cat_drinks",
          "name": "🥤 饮料 / Drinks",
          "items": [
            {
              "id": "item_coke",
              "name": "可口可乐",
              "description": "330ml 罐装",
              "price": 2.50,
              "image_url": "https://fakeimg.pl/400x400/?text=Coke",
              "is_available": true,
              "modifier_groups": []
            }
          ]
        }
      ]
    }
    """.trimIndent()

    @Test
//    @Ignore("运维脚本：仅在需要重置菜单时手动移除此注解并运行") // ✅ 加这把锁
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