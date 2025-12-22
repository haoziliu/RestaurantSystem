package com.haoziliu.restaurantsystem.core.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.haoziliu.restaurantsystem.core.domain.model.MenuCategory
import com.haoziliu.restaurantsystem.core.domain.model.MenuModifierGroup

class Converters {
    private val gson = Gson()

     @TypeConverter
    fun fromCategoryList(value: List<MenuCategory>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCategoryList(value: String): List<MenuCategory>? {
        val type = object : TypeToken<List<MenuCategory>>() {}.type
        return gson.fromJson(value, type)
    }

    // 如果你将来决定把 MenuItem 单独存表，这里预留 Modifier 的转换器
    @TypeConverter
    fun fromModifierGroupList(value: List<MenuModifierGroup>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toModifierGroupList(value: String): List<MenuModifierGroup>? {
        val type = object : TypeToken<List<MenuModifierGroup>>() {}.type
        return gson.fromJson(value, type)
    }


}