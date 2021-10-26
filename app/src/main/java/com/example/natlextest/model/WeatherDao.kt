package com.example.natlextest.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather WHERE city_name=:cityName")
    suspend fun getWeatherByName(cityName: String): List<Weather>

    @Insert
    suspend fun addWeather(weather: Weather)

    @Query("SELECT * FROM weather GROUP BY city_id")
    suspend fun getArrayWeather(): List<Weather>

    @Query("SELECT * FROM weather")
    suspend fun getAllWeather(): List<Weather>
}