package com.example.natlextest.network

import com.example.natlextest.model.Weather
import com.example.natlextest.network.DTO.WeatherResponseDTO
import com.example.natlextest.utils.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

interface IRemoteRepository {
    suspend fun getWeatherByName(cityName: String): Response<WeatherResponseDTO>
    suspend fun getWeatherByCoords(lat: Double, lon: Double): Response<WeatherResponseDTO>
}

class RemoteRepository(private val retrofit: RetrofitClient): IRemoteRepository {

    override suspend fun getWeatherByName(cityName: String): Response<WeatherResponseDTO> =
        withContext(Dispatchers.IO) {
            when (val response = retrofit.apiInterface.getWeatherByName(cityName)) {
                is ApiResponse.ApiSuccess -> Response.Success(response.data)
                is ApiResponse.ApiFailure -> Response.Error(response.t)
            }
        }

    override suspend fun getWeatherByCoords(
        lat: Double,
        lon: Double
    ): Response<WeatherResponseDTO> =
        withContext(Dispatchers.IO) {
            when (val response = retrofit.apiInterface.getWeatherByCoords(lat, lon)) {
                is ApiResponse.ApiSuccess -> Response.Success(response.data)
                is ApiResponse.ApiFailure -> Response.Error(response.t)
            }
        }

}

