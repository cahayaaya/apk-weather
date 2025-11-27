package com.cahayaaya.weather.data

data class GeoResponse(
    val results: List<GeoResult>? = null
)

data class GeoResult(
    val name: String?,
    val latitude: Double,
    val longitude: Double,
    val country: String?
)