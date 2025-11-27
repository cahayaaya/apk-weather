package com.cahayaaya.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

class WeatherViewModel : ViewModel() {

    private val _weatherState = MutableStateFlow(WeatherState())
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()

    fun searchCity(cityName: String) {
        viewModelScope.launch {
            _weatherState.value = WeatherState(isLoading = true)
            try {
                // DATA BERBEDA UNTUK SETIAP KOTA ↓
                val weatherData = getWeatherDataForCity(cityName)

                // Prediksi per jam (berdasarkan kota)
                val hourlyForecast = generateHourlyForecast(weatherData)

                // Prediksi mingguan (berdasarkan kota)
                val weeklyForecast = generateWeeklyForecast(weatherData)

                _weatherState.value = WeatherState(
                    weatherData = weatherData,
                    hourlyForecast = hourlyForecast,
                    weeklyForecast = weeklyForecast
                )
            } catch (e: Exception) {
                _weatherState.value = WeatherState(error = "Gagal: ${e.message}")
            }
        }
    }

    // FUNGSI BARU: Data berbeda untuk setiap kota
    private fun getWeatherDataForCity(cityName: String): WeatherData {
        return when (cityName.lowercase()) {
            "jakarta" -> WeatherData(
                cityName = "Jakarta",
                temperature = 32.0,
                windSpeed = 8.5,
                humidity = 75,
                pressure = 1012,
                description = "Cerah Berawan",
                feelsLike = 34.0,
                visibility = 12,
                uvIndex = 7
            )
            "surabaya" -> WeatherData(
                cityName = "Surabaya",
                temperature = 34.0,
                windSpeed = 6.2,
                humidity = 68,
                pressure = 1010,
                description = "Cerah",
                feelsLike = 36.0,
                visibility = 15,
                uvIndex = 9
            )
            "bandung" -> WeatherData(
                cityName = "Bandung",
                temperature = 24.0,
                windSpeed = 12.5,
                humidity = 82,
                pressure = 1015,
                description = "Hujan Ringan",
                feelsLike = 26.0,
                visibility = 8,
                uvIndex = 4
            )
            "yogyakarta" -> WeatherData(
                cityName = "Yogyakarta",
                temperature = 28.0,
                windSpeed = 5.8,
                humidity = 70,
                pressure = 1013,
                description = "Berawan",
                feelsLike = 30.0,
                visibility = 10,
                uvIndex = 6
            )
            "bali" -> WeatherData(
                cityName = "Bali",
                temperature = 30.0,
                windSpeed = 15.3,
                humidity = 78,
                pressure = 1011,
                description = "Cerah Berangin",
                feelsLike = 32.0,
                visibility = 20,
                uvIndex = 10
            )
            "london" -> WeatherData(
                cityName = "London",
                temperature = 15.0,
                windSpeed = 10.2,
                humidity = 85,
                pressure = 1008,
                description = "Hujan Ringan",
                feelsLike = 13.0,
                visibility = 6,
                uvIndex = 2
            )
            "tokyo" -> WeatherData(
                cityName = "Tokyo",
                temperature = 18.0,
                windSpeed = 7.8,
                humidity = 65,
                pressure = 1014,
                description = "Cerah Berawan",
                feelsLike = 19.0,
                visibility = 12,
                uvIndex = 5
            )
            else -> WeatherData(
                cityName = cityName,
                temperature = 25.0 + (cityName.hashCode() % 10), // Random temp
                windSpeed = 8.0 + (cityName.hashCode() % 7),
                humidity = 60 + (abs(cityName.hashCode()) % 35), // Pakai abs()
                pressure = 1010 + (cityName.hashCode() % 15),
                description = getRandomDescription(cityName),
                feelsLike = 27.0 + (cityName.hashCode() % 8),
                visibility = 5 + (abs(cityName.hashCode()) % 15), // Pakai abs()
                uvIndex = 3 + (abs(cityName.hashCode()) % 7) // Pakai abs()
            )
        }
    }

    // FUNGSI BARU: Deskripsi acak berdasarkan nama kota
    private fun getRandomDescription(cityName: String): String {
        val descriptions = listOf(
            "Cerah", "Cerah Berawan", "Berawan", "Hujan Ringan",
            "Hujan Sedang", "Hujan Lebat", "Badai Petir", "Kabut"
        )
        // PERBAIKAN: Pakai Math.abs() atau kotlin.math.abs
        return descriptions[abs(cityName.hashCode()) % descriptions.size]
    }

    // FUNGSI BARU: Generate prediksi per jam
    private fun generateHourlyForecast(weatherData: WeatherData): List<HourlyForecast> {
        val hours = listOf(
            "Sekarang", "10:00", "11:00", "12:00", "13:00", "14:00",
            "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"
        )

        val icons = when (weatherData.description) {
            "Cerah" -> listOf("☀️", "☀️", "☀️", "☀️", "☀️", "☀️", "☀️", "⛅", "⛅", "🌙", "🌙", "🌙")
            "Cerah Berawan" -> listOf("⛅", "⛅", "⛅", "☀️", "☀️", "⛅", "⛅", "⛅", "🌙", "🌙", "🌙", "🌙")
            "Berawan" -> listOf("☁️", "☁️", "☁️", "☁️", "☁️", "☁️", "☁️", "☁️", "☁️", "☁️", "☁️", "☁️")
            "Hujan Ringan" -> listOf("🌧️", "🌧️", "🌧️", "⛅", "⛅", "⛅", "🌧️", "🌧️", "🌧️", "🌧️", "🌧️", "🌧️")
            "Hujan Sedang" -> listOf("🌧️", "🌧️", "🌧️", "🌧️", "🌧️", "🌧️", "🌧️", "🌧️", "🌧️", "🌧️", "🌧️", "🌧️")
            else -> listOf("🌤️", "🌤️", "🌤️", "🌤️", "🌤️", "🌤️", "🌤️", "🌤️", "🌤️", "🌤️", "🌤️", "🌤️")
        }

        return hours.mapIndexed { index, hour ->
            val tempVariation = when (index) {
                in 0..2 -> 0.0    // Pagi
                in 3..5 -> 2.0    // Siang (terpanas)
                in 6..8 -> 1.0    // Sore
                else -> -1.0       // Malam
            }

            HourlyForecast(
                time = hour,
                temperature = weatherData.temperature + tempVariation,
                icon = icons[index],
                precipitation = when (weatherData.description) {
                    "Cerah" -> (5..15).random()
                    "Cerah Berawan" -> (10..25).random()
                    "Berawan" -> (20..40).random()
                    "Hujan Ringan" -> (60..80).random()
                    "Hujan Sedang" -> (70..90).random()
                    else -> (10..30).random()
                }
            )
        }
    }

    // FUNGSI BARU: Generate prediksi mingguan
    private fun generateWeeklyForecast(weatherData: WeatherData): List<DailyForecast> {
        val days = listOf("Hari Ini", "Besok", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
        val dates = listOf("24 Nov", "25 Nov", "26 Nov", "27 Nov", "28 Nov", "29 Nov", "30 Nov")

        return days.mapIndexed { index, day ->
            DailyForecast(
                day = day,
                date = dates[index],
                maxTemp = weatherData.temperature + 2.0,
                minTemp = weatherData.temperature - 3.0,
                icon = when (weatherData.description) {
                    "Cerah" -> "☀️"
                    "Cerah Berawan" -> "⛅"
                    "Berawan" -> "☁️"
                    "Hujan Ringan" -> "🌧️"
                    "Hujan Sedang" -> "🌧️"
                    "Hujan Lebat" -> "⛈️"
                    else -> "🌤️"
                },
                description = weatherData.description,
                precipitation = when (weatherData.description) {
                    "Cerah" -> 10
                    "Cerah Berawan" -> 20
                    "Berawan" -> 35
                    "Hujan Ringan" -> 65
                    "Hujan Sedang" -> 80
                    "Hujan Lebat" -> 95
                    else -> 25
                }
            )
        }
    }
}

// Data classes tetap di bawah (JANGAN DIUBAH)
data class WeatherState(
    val isLoading: Boolean = false,
    val weatherData: WeatherData? = null,
    val hourlyForecast: List<HourlyForecast> = emptyList(),
    val weeklyForecast: List<DailyForecast> = emptyList(),
    val error: String? = null
)

data class WeatherData(
    val cityName: String = "",
    val temperature: Double = 0.0,
    val windSpeed: Double = 0.0,
    val humidity: Int = 0,
    val pressure: Int = 0,
    val description: String = "",
    val feelsLike: Double = 0.0,
    val visibility: Int = 0,
    val uvIndex: Int = 0
)

data class HourlyForecast(
    val time: String,
    val temperature: Double,
    val icon: String,
    val precipitation: Int
)

data class DailyForecast(
    val day: String,
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val icon: String,
    val description: String,
    val precipitation: Int
)