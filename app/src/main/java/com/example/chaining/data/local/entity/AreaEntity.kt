package com.example.chaining.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "area_codes")
data class AreaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "region_code")
    val regionCode: String,
    @ColumnInfo(name = "region_name")
    val regionName: String,
    @ColumnInfo(name = "sub_region_code")
    val subRegionCode: String,
    @ColumnInfo(name = "sub_region_name")
    val subRegionName: String,
    @ColumnInfo(name = "row_num")
    val rowNum: Int,
)
