package com.example.natlextest.di

import android.content.Context
import androidx.room.Room
import com.example.natlextest.BuildConfig
import com.example.natlextest.model.*
import com.example.natlextest.network.ApiInterface
import com.example.natlextest.network.RemoteRepository
import com.example.natlextest.view.WeatherListAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApplicationModule {

    private const val TIMEOUT = 20L
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
        connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        readTimeout(TIMEOUT, TimeUnit.SECONDS)
    }.build()

    @Provides
    @Singleton
    fun provideRetrofit(url: String): Retrofit = Retrofit.Builder().apply {
        baseUrl(url)
        addConverterFactory(GsonConverterFactory.create())
        client(provideHttpClient())
    }.build()


    @Provides
    @Singleton
    fun provideWeatherService(): ApiInterface =
        provideRetrofit(BASE_URL).create(ApiInterface::class.java)

    @Provides
    @Singleton
    fun provideWeatherDatabase(@ApplicationContext appContext: Context): WeatherDB =
        Room.databaseBuilder(appContext, WeatherDB::class.java, "weather.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideRepoDao(weatherDB: WeatherDB): WeatherDao =
        weatherDB.weatherDao

    @Provides
    @Singleton
    fun provideRoomRepository(weatherDao: WeatherDao): RoomRepository =
        RoomRepository(weatherDao)

    @Provides
    @Singleton
    fun provideRemoteRepository(apiInterface: ApiInterface): RemoteRepository =
        RemoteRepository(apiInterface)
}