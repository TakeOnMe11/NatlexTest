package com.example.natlextest.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.natlextest.model.IRoomRepository
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
    private val roomRepository: IRoomRepository
    ): ViewModel() {

    val data: MutableLiveData<Resource<List<Weather>>> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    fun getWeatherRemote(cityName: String) {
        data.postValue(Resource.loading())
        viewModelScope.launch {
            val response = remoteRepository.getWeatherByName(cityName)
            Timber.d(response.data.toString())
            when (response.status) {
                Resource.Status.SUCCESS -> {
                    roomRepository.addWeatherLocal(response.data)
                    data.postValue(roomRepository.getWeatherByNameLocal(cityName))
                }
                Resource.Status.ERROR -> handleError(response.message)
                else -> handleError(response.message)
            }
        }
    }

    fun getWeatherRemote(lat: Double, lon: Double) {
        data.postValue(Resource.loading())
        viewModelScope.launch {
            val response = remoteRepository.getWeatherByCoords(lat, lon)
            when (response.status) {
                Resource.Status.SUCCESS -> {
                    roomRepository.addWeatherLocal(response.data)
                    val cityName = response.data?.cityName
                    data.postValue(cityName?.let { roomRepository.getWeatherByNameLocal(it) })
                }
                Resource.Status.ERROR -> handleError(response.message)
                else -> handleError(response.message)
            }
        }
    }

    fun getWeatherLocal(cityName: String) {
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

    fun getArrayWeather() {
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

    private fun handleError(t: String?) {
        error.postValue(t ?: "Unknown exception")
    }
}