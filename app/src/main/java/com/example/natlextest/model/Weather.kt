package com.example.natlextest.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Weather(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "city_id") var cityId: Int?,
    @ColumnInfo(name = "city_name") var cityName: String?,
    @ColumnInfo(name = "temp_fahr") var tempFahr: Double?,
    @ColumnInfo(name = "temp_cells") var tempCells: Double?,
    @ColumnInfo(name = "temp_min") var tempMin: Double?,
    @ColumnInfo(name = "temp_max") var tempMax: Double?,
    var time: String?,
    var date: String?
)
