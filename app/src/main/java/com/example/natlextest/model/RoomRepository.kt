package com.example.natlextest.model

import com.example.natlextest.network.DTO.WeatherResponseDTO
import com.example.natlextest.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RoomRepository @Inject constructor(private val weatherDao: WeatherDao) {

    suspend fun getWeatherByNameLocal(cityName: String): Resource<List<Weather>> =
        withContext(Dispatchers.IO) {
            try {
                Resource.success(weatherDao.getWeatherByName(cityName))
            } catch(e: Exception) {
                e.printStackTrace()
                Resource.error(e.message ?: e.toString())
        }
    }

    suspend fun addWeatherLocal(weatherResponse: WeatherResponseDTO?) =
        withContext(Dispatchers.IO) {
            try {
                val dateTime: Date = Calendar.getInstance().time
                val tf = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(dateTime)
                val df = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(dateTime)
                val weather = Weather(
                    id =0,
                    cityId = weatherResponse?.id,
                    cityName = weatherResponse?.cityName,
                    temp = weatherResponse?.main?.temp,
                    tempMin = weatherResponse?.main?.tempMin,
                    tempMax = weatherResponse?.main?.tempMax,
                    time = tf,
                    date = df
                )
                weatherDao.addWeather(weather)
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }

    suspend fun getArrayWeatherLocal(): Resource<List<Weather>> =
        withContext(Dispatchers.IO) {
            try {
                Resource.success(weatherDao.getArrayWeather())
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(e.message ?: e.toString())
            }
        }

    suspend fun countWeatherByNameLocal(): Resource<List<Weather>> =
        withContext(Dispatchers.IO) {
            try {
                Resource.success(weatherDao.getAllWeather())
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(e.message ?: e.toString())
            }
        }
}