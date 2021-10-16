package com.example.natlextest.network

import okhttp3.Headers

sealed class ApiResponse<out T> {
    data class ApiSuccess<T>(val data: T, val headers: Headers): ApiResponse<T>()
    data class ApiFailure(val t: Throwable): ApiResponse<Nothing>()
}
