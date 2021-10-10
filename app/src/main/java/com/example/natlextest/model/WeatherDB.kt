package com.example.natlextest.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Weather::class], version = 1)
abstract class WeatherDB: RoomDatabase() {
    abstract val weatherDao: WeatherDao
}