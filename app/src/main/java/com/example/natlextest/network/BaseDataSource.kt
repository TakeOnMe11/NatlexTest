package com.example.natlextest.network

import com.example.natlextest.utils.Resource
import retrofit2.Response
import timber.log.Timber

abstract class BaseDataSource {

    suspend fun <T> getResult (call: suspend () -> Response<T>): Resource<T> {
        try {
            val response = call()
            Timber.d(response.message())
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) return Resource.success(body)
            }
            return error(response.message())
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(e: String): Resource<T> {
        return Resource.error(e)
    }
}