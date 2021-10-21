package com.example.natlextest.model

import com.example.natlextest.network.DTO.WeatherResponseDTO
import com.example.natlextest.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

interface IRoomRepository {
    suspend fun getWeatherByNameLocal(cityName: String): Resource<List<Weather>>

    suspend fun addWeatherLocal(weatherResponse: WeatherResponseDTO?)

    suspend fun getArrayWeatherLocal(): Resource<List<Weather>>

    suspend fun countWeatherByNameLocal(cityName: String): Resource<Int>
}

class RoomRepository @Inject constructor(private val weatherDao: WeatherDao): IRoomRepository {

    override suspend fun getWeatherByNameLocal(cityName: String): Resource<List<Weather>> =
        withContext(Dispatchers.IO) {
            try {
                Resource.success(weatherDao.getWeatherByName(cityName))
            } catch(e: Exception) {
                e.printStackTrace()
                Resource.error(e.message ?: e.toString())
        }
    }

    override suspend fun addWeatherLocal(weatherResponse: WeatherResponseDTO?) =
        withContext(Dispatchers.IO) {
            try {
                val dateTime: Date = Calendar.getInstance().time
                val tf = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(dateTime)
                val df = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(dateTime)
                val weather = Weather(
                    weatherResponse?.id,
                    weatherResponse?.cityName,
                    weatherResponse?.main?.temp,
                    weatherResponse?.main?.tempMin,
                    weatherResponse?.main?.tempMax,
                    tf,
                    df
                )
                weatherDao.addWeather(weather)
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }

    override suspend fun getArrayWeatherLocal(): Resource<List<Weather>> =
        withContext(Dispatchers.IO) {
            try {
                Resource.success(weatherDao.getArrayWeather())
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(e.message ?: e.toString())
            }
        }

    override suspend fun countWeatherByNameLocal(cityName: String): Resource<Int> =
        withContext(Dispatchers.IO) {
            try {
                Resource.success(weatherDao.countWeatherByName(cityName))
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(e.message ?: e.toString())
            }
        }
}