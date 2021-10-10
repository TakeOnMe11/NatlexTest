package com.example.natlextest.network.DTO

import com.google.gson.annotations.SerializedName

data class WeatherResponseMainDTO(
    @SerializedName("temp") var temp: Double = 0.0,
    @SerializedName("temp_min") var tempMin: Double = 0.0,
    @SerializedName("temp_max") var tempMax: Double = 0.0
)
