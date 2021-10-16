package com.example.natlextest.model

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

import androidx.room.Room

@Database(entities = [Weather::class], version = 1)
abstract class WeatherDB: RoomDatabase() {
    abstract val weatherDao: WeatherDao?

    companion object {
        private var instance: WeatherDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also{
                instance = it
            }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, WeatherDB::class.java, "WeatherDB.db").build()
    }
}