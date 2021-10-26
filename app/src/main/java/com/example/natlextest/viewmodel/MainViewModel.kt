package com.example.natlextest.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.natlextest.model.RoomRepository
import com.example.natlextest.model.Weather
import com.example.natlextest.network.RemoteRepository
import com.example.natlextest.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val roomRepository: RoomRepository
    ): ViewModel() {

    val data: MutableLiveData<Resource<List<Weather>>> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()
    val allWeather: MutableLiveData<Resource<List<Weather>>> = MutableLiveData()

    suspend fun getWeatherRemote(cityName: String) {
        data.postValue(Resource.loading())
        viewModelScope.launch {
            val response = remoteRepository.getWeatherByName(cityName)
            when (response.status) {
                Resource.Status.SUCCESS -> {
                    roomRepository.addWeatherLocal(response.data)
                    data.postValue(roomRepository.getArrayWeatherLocal())
                }
                Resource.Status.ERROR -> handleError(response.message)
                else -> handleError(response.message)
            }
        }
    }

    suspend fun getWeatherRemote(lat: Double, lon: Double) {
        data.postValue(Resource.loading())
        viewModelScope.launch {
            val response = remoteRepository.getWeatherByCoords(lat, lon)
            when (response.status) {
                Resource.Status.SUCCESS -> {
                    roomRepository.addWeatherLocal(response.data)
                    data.postValue(roomRepository.getArrayWeatherLocal())
                }
                Resource.Status.ERROR -> handleError(response.message)
                else -> handleError(response.message)
            }
        }
    }

    suspend fun getWeatherLocal(cityName: String) {
        data.postValue(Resource.loading())
        viewModelScope.launch {
            val response = roomRepository.getWeatherByNameLocal(cityName)
            when (response.status){
                Resource.Status.SUCCESS -> data.postValue(response)
                Resource.Status.ERROR -> handleError(response.message)
                else -> handleError(response.message)
            }
        }
    }

    suspend fun getArrayWeather() {
        data.postValue(Resource.loading())
        viewModelScope.launch {
            val response = roomRepository.getArrayWeatherLocal()
            when (response.status) {
                Resource.Status.SUCCESS -> data.postValue(response)
                Resource.Status.ERROR -> handleError(response.message)
                else -> handleError(response.message)
            }
        }
    }

    suspend fun getAllWeather() {
        viewModelScope.launch {
            val response = roomRepository.countWeatherByNameLocal()
            when (response.status) {
                Resource.Status.SUCCESS -> allWeather.postValue(response)
                Resource.Status.ERROR -> handleError(response.message)
                else -> handleError(response.message)
            }
        }
    }

    private fun handleError(t: String?) {
        error.postValue(t ?: "Unknown exception")
    }
}