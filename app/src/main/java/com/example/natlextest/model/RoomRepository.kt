package com.example.natlextest.model

import com.example.natlextest.utils.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface IRoomRepository {
    suspend fun getWeatherByNameLocal(cityName: String): Response<List<Weather>>

    suspend fun addWeatherLocal(weather: Weather)

    suspend fun getArrayWeatherLocal(): Response<List<Weather>>

    suspend fun countWeatherByNameLocal(cityName: String): Response<Int>
}

class RoomRepository(private val weatherDao: WeatherDao): IRoomRepository {

    override suspend fun getWeatherByNameLocal(cityName: String): Response<List<Weather>> =
        withContext(Dispatchers.IO) {
            try {
                Response.Success(weatherDao.getWeatherByName(cityName))
            } catch(e: Exception) {
                e.printStackTrace()
                Response.Error(e)
        }
    }

    override suspend fun addWeatherLocal(weather: Weather) =
        withContext(Dispatchers.IO) {
            try {
                weatherDao.addWeather(weather)
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }

    override suspend fun getArrayWeatherLocal(): Response<List<Weather>> =
        withContext(Dispatchers.IO) {
            try {
                Response.Success(weatherDao.getArrayWeather())
            } catch (e: Exception) {
                e.printStackTrace()
                Response.Error(e)
            }
        }

    override suspend fun countWeatherByNameLocal(cityName: String): Response<Int> =
        withContext(Dispatchers.IO) {
            try {
                Response.Success(weatherDao.countWeatherByName(cityName))
            } catch (e: Exception) {
                e.printStackTrace()
                Response.Error(e)
            }
        }
}