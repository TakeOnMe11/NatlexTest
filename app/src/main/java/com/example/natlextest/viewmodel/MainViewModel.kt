package com.example.natlextest.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.natlextest.model.RoomRepository
import com.example.natlextest.model.Weather
import com.example.natlextest.network.RemoteRepository
import com.example.natlextest.utils.Response
import kotlinx.coroutines.launch

class MainViewModel(
    private val remoteRepository: RemoteRepository,
    private val roomRepository: RoomRepository
): ViewModel() {

    val data: MutableLiveData<Response<List<Weather>>> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    private fun getWeatherRemote(cityName: String) {
        data.postValue(Response.Loading())
        viewModelScope.launch {
            when (val response = remoteRepository.getWeatherByName(cityName)) {
                /*
                is Response.Success -> {
                    val weather: Weather? = null
                    weather?.id = response.data?.id
                    weather?.cityName = response.data?.cityName
                    weather?.temp = response.data?.main?.temp
                    weather?.tempMin = response.data?.main?.tempMin
                    weather?.tempMax = response.data?.main?.tempMax
                    val weatherList: List<Weather> = listOf(weather)
                    data.postValue(weatherList)

                }
                */
            }
        }
    }

    private fun getWeatherLocal(cityName: String) {
        data.postValue(Response.Loading())
        viewModelScope.launch {
            when (val response = roomRepository.getWeatherByNameLocal(cityName)){
                is Response.Success -> data.postValue(response)
                is Response.Error -> handleError(response.t)
            }
        }
    }

    private fun handleError(t: Throwable?) {
        error.postValue(t?.toString() ?: "Unknown exception")
    }

    private fun refreshRepoList(list: List<Weather>){
        viewModelScope.launch {
            roomRepository.addWeatherLocal(list[0])
        }
    }
}