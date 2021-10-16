package com.example.natlextest.utils

sealed class Response<T>(val data: T? = null, val t: Throwable? = null) {
    class Loading<T> : Response<T>()
    class Success<T>(data: T) : Response<T>(data = data)
    class Error<T>(t: Throwable) : Response<T>(t = t)
}
