package com.example.natlextest.network

import com.example.natlextest.network.DTO.WeatherResponseDTO
import com.example.natlextest.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteRepository @Inject constructor(private val apiInterface: ApiInterface): BaseDataSource() {

    private val API_KEY = "b6a9c2b82a3ef16fb32f12de98d2f907"

    suspend fun getWeatherByName(cityName: String) = getResult { apiInterface.getWeatherByName(cityName, API_KEY) }
    suspend fun getWeatherByCoords(lat: Double, lon: Double) = getResult { apiInterface.getWeatherByCoords(lat, lon, API_KEY) }
}

