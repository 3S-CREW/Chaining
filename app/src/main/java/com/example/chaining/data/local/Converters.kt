package com.example.chaining.data.local

import androidx.room.TypeConverter
import com.example.chaining.domain.model.Application
import com.example.chaining.domain.model.LanguagePref
import com.example.chaining.domain.model.RecruitPost
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun fromLanguagePrefList(list: List<LanguagePref>): String = Json.encodeToString(list)

    @TypeConverter
    fun toLanguagePrefList(value: String): List<LanguagePref> =
        if (value.isEmpty()) emptyList() else Json.decodeFromString(value)

    @TypeConverter
    fun fromApplicationList(list: List<Application>): String = Json.encodeToString(list)

    @TypeConverter
    fun fromPostMap(map: Map<String, RecruitPost>): String = Json.encodeToString(map)

    @TypeConverter
    fun toApplicationList(value: String): List<Application> =
        if (value.isEmpty()) emptyList() else Json.decodeFromString(value)

    @TypeConverter
    fun toPostMap(value: String): Map<String, RecruitPost> =
        if (value.isEmpty()) emptyMap() else Json.decodeFromString(value)

    @TypeConverter
    fun fromLikedPosts(map: Map<String, Boolean>): String = Json.encodeToString(map)

    @TypeConverter
    fun toLikedPosts(value: String): Map<String, Boolean> =
        if (value.isEmpty()) emptyMap() else Json.decodeFromString(value)
}
