package com.example.natlextest.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Weather(
    @PrimaryKey var id: Int?,
    @ColumnInfo(name = "city_name") var cityName: String?,
    var temp: Double?,
    @ColumnInfo(name = "temp_min") var tempMin: Double?,
    @ColumnInfo(name = "temp_max") var tempMax: Double?,
    var time: String?,
    var date: String?,
    var unit: String? = "F"
)
