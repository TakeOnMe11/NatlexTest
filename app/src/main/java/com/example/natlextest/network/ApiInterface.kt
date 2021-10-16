package com.example.natlextest.network

import com.example.natlextest.BuildConfig
import com.example.natlextest.network.DTO.WeatherResponseDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
    suspend fun getWeatherByName(@Query(value = "q") cityName: String,
                                 @Query("appid") key: String = BuildConfig.API_KEY): ApiResponse<WeatherResponseDTO>

    @GET("weather")
    suspend fun getWeatherByCoords(@Query(value = "lat") lat: Double,
                                   @Query("lon") lon: Double,
                                   @Query("appid") key: String = BuildConfig.API_KEY): ApiResponse<WeatherResponseDTO>
}