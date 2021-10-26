package com.example.natlextest.model

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

import androidx.room.Room

@Database(entities = [Weather::class], version = 7, exportSchema = false)
abstract class WeatherDB: RoomDatabase() {
    abstract val weatherDao: WeatherDao
}