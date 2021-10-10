package com.example.natlextest.model

import androidx.room.Insert
import androidx.room.Query

interface WeatherDao {

    @Query("SELECT * FROM weather WHERE city_name=:cityName")
    fun getWeatherByName(cityName: String): ArrayList<Weather>

    @Insert
    fun addWeather(weather: Weather)

    @Query("SELECT * FROM weather GROUP BY id")
    fun getArrayWeather(): ArrayList<Weather>

    @Query("SELECT COUNT(*) FROM weather WHERE city_name=:cityName")
    fun countWeatherByName(cityName: String): Int
}