package com.example.chaining.data.local

import androidx.room.TypeConverter
import com.example.chaining.domain.model.Application
import com.example.chaining.domain.model.LanguagePref
import com.example.chaining.domain.model.RecruitPost
import com.example.chaining.domain.model.User
import com.example.chaining.domain.model.UserSummary
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromLanguagePrefMap(map: Map<String, LanguagePref>): String = Json.encodeToString(map)

    @TypeConverter
    fun toLanguagePrefMap(value: String): Map<String, LanguagePref> =
        if (value.isEmpty()) emptyMap() else Json.decodeFromString(value)

    @TypeConverter
    fun fromApplicationMap(map: Map<String, Application>): String = Json.encodeToString(map)

    @TypeConverter
    fun fromPostMap(map: Map<String, RecruitPost>): String = Json.encodeToString(map)

    @TypeConverter
    fun toApplicationMap(value: String): Map<String, Application> =
        if (value.isEmpty()) emptyMap() else Json.decodeFromString(value)

    @TypeConverter
    fun toPostMap(value: String): Map<String, RecruitPost> =
        if (value.isEmpty()) emptyMap() else Json.decodeFromString(value)

    @TypeConverter
    fun fromLikedPosts(map: Map<String, Boolean>): String = Json.encodeToString(map)

    @TypeConverter
    fun toLikedPosts(value: String): Map<String, Boolean> =
        if (value.isEmpty()) emptyMap() else Json.decodeFromString(value)

    @TypeConverter
    fun fromFollowMap(map: Map<String, UserSummary>): String = Json.encodeToString(map)

    @TypeConverter
    fun toFollowMap(value: String): Map<String, UserSummary> =
        if (value.isEmpty()) emptyMap() else Json.decodeFromString(value)

    @TypeConverter
    fun fromUserSummary(value: UserSummary?): String = value?.let { Json.encodeToString(it) } ?: ""

    @TypeConverter
    fun toUserSummary(value: String?): UserSummary? =
        if (value.isNullOrEmpty()) null else Json.decodeFromString(value)

    @TypeConverter
    fun fromUser(value: User?): String = value?.let { Json.encodeToString(it) } ?: ""

    @TypeConverter
    fun toUser(value: String?): User? =
        if (value.isNullOrEmpty()) null else Json.decodeFromString(value)
}
