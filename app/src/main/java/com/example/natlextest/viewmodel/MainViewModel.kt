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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val roomRepository: RoomRepository
    ): ViewModel() {

    val data = MutableSharedFlow<Resource<List<Weather>>>(replay = 1)
    val error = MutableSharedFlow<String>(replay = 1)
    val allWeather = MutableSharedFlow<Resource<List<Weather>>>(replay = 1)

    fun getWeatherRemote(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            data.emitAll(flow { Resource.loading<Weather>() } )
            val response = remoteRepository.getWeatherByName(cityName)
            when (response.status) {
                Resource.Status.SUCCESS -> {
                    roomRepository.addWeatherLocal(response.data)
                    data.tryEmit(roomRepository.getArrayWeatherLocal())
                    //data.emitAll(flow { roomRepository.getArrayWeatherLocal() })
                }
                Resource.Status.ERROR -> handleError(response.message)
                else -> handleError(response.message)
            }
        }
    }

    fun getWeatherRemote(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            data.emitAll(flow { Resource.loading<Weather>() } )
            val response = remoteRepository.getWeatherByCoords(lat, lon)
            when (response.status) {
                Resource.Status.SUCCESS -> {
                    roomRepository.addWeatherLocal(response.data)
                    data.tryEmit(roomRepository.getArrayWeatherLocal())
                    //data.emitAll(flow { roomRepository.getArrayWeatherLocal() })
                }
                Resource.Status.ERROR -> handleError(response.message)
                else -> handleError(response.message)
            }
        }
    }

    fun getWeatherLocal(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            data.emitAll(flow { Resource.loading<Weather>() } )
            val response = roomRepository.getWeatherByNameLocal(cityName)
            when (response.status){
                Resource.Status.SUCCESS -> data.tryEmit(roomRepository.getArrayWeatherLocal())//data.emitAll(flow { roomRepository.getArrayWeatherLocal() })
                Resource.Status.ERROR -> handleError(response.message)
                else -> handleError(response.message)
            }
        }
    }

    fun getArrayWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            data.emitAll(flow { Resource.loading<Weather>() } )
            val response = roomRepository.getArrayWeatherLocal()
            when (response.status) {
                Resource.Status.SUCCESS -> data.tryEmit(roomRepository.getArrayWeatherLocal())//data.emitAll(flow { roomRepository.getArrayWeatherLocal() })
                Resource.Status.ERROR -> handleError(response.message)
                else -> handleError(response.message)
            }
        }
    }

    fun getAllWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = roomRepository.countWeatherByNameLocal()
            when (response.status) {
                Resource.Status.SUCCESS -> allWeather.tryEmit(response)//allWeather.emitAll( flow { response } )
                Resource.Status.ERROR -> handleError(response.message)
                else -> handleError(response.message)
            }
        }
    }

    private fun handleError(t: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            error.tryEmit(t ?: "Unknown exception")//error.emitAll(flow{ t ?: "Unknown exception" })
        }
    }
}