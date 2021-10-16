package com.example.natlextest.network.DTO

import com.google.gson.annotations.SerializedName

data class WeatherResponseDTO(
    @SerializedName("id") var id: Int?,
    @SerializedName("name") var cityName: String?,
    @SerializedName("main") var main: Main?
)

data class Main(
    @SerializedName("temp") var temp: Double = 0.0,
    @SerializedName("temp_min") var tempMin: Double = 0.0,
    @SerializedName("temp_max") var tempMax: Double = 0.0
)

