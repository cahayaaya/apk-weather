package com.cahayaaya.weather.data

data class WeatherResponse(
    val current_weather: CurrentWeather?
)

data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double,
    val weathercode: Int
)