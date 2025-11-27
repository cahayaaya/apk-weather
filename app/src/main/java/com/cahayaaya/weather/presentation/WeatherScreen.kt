package com.cahayaaya.weather.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cahayaaya.weather.R
import com.cahayaaya.weather.WeatherViewModel
import com.cahayaaya.weather.ui.theme.WeatherTheme
import androidx.compose.foundation.isSystemInDarkTheme
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WeatherScreen() {
    val viewModel: WeatherViewModel = viewModel()
    var searchQuery by remember { mutableStateOf("") }
    val weatherState by viewModel.weatherState.collectAsState()
    val isDarkTheme = isSystemInDarkTheme()

    // Background gradient yang lebih hidup berdasarkan waktu
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val gradient = getDynamicBackground(currentHour, isDarkTheme)

    // Auto search default
    LaunchedEffect(Unit) {
        viewModel.searchCity("Jakarta")
    }

    WeatherTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
        ) {
            // MATAHARI YANG ELASTIS DAN HIDUP
            AnimatedSunDecoration(currentHour)

            // Konten utama
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                WeatherHeader(
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    onSearch = {
                        if (searchQuery.isNotBlank()) viewModel.searchCity(searchQuery)
                    },
                    isDarkTheme = isDarkTheme
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (weatherState.isLoading) {
                    WeatherLoading()
                }

                weatherState.error?.let { error ->
                    WeatherError(error) { viewModel.searchCity("Jakarta") }
                }

                weatherState.weatherData?.let { weather ->
                    CurrentWeatherSection(weather, isDarkTheme)

                    Spacer(modifier = Modifier.height(24.dp))

                    WeatherSectionTitle(stringResource(id = R.string.prediksi_per_jam), isDarkTheme)
                    HourlyForecastSection(weatherState.hourlyForecast, isDarkTheme)

                    Spacer(modifier = Modifier.height(24.dp))

                    WeatherSectionTitle(stringResource(id = R.string.detail_cuaca), isDarkTheme)
                    WeatherDetailsSection(weather, isDarkTheme)

                    Spacer(modifier = Modifier.height(24.dp))

                    WeatherSectionTitle(stringResource(id = R.string.prediksi_7_hari), isDarkTheme)
                    WeeklyForecastSection(weatherState.weeklyForecast, isDarkTheme)
                }
            }
        }
    }
}

// Fungsi untuk mendapatkan background dinamis berdasarkan waktu - DIUBAH JADI LEBIH SOFT
private fun getDynamicBackground(currentHour: Int, isDarkTheme: Boolean): Brush {
    return when {
        currentHour in 5..10 -> Brush.verticalGradient(  // Pagi (5am - 10am)
            colors = listOf(
                Color(0xFFA7D8FF),  // Light Blue - LEBIH SOFT
                Color(0xFFE6F7FF)   // Very Light Blue
            )
        )
        currentHour in 11..15 -> Brush.verticalGradient( // Siang (11am - 3pm)
            colors = listOf(
                Color(0xFF87CEEB),  // Sky Blue - LEBIH SOFT
                Color(0xFFB3E5FC)   // Light Cyan
            )
        )
        currentHour in 16..18 -> Brush.verticalGradient( // Sore (4pm - 6pm)
            colors = listOf(
                Color(0xFFFFB74D),  // Light Orange - LEBIH SOFT
                Color(0xFFFFE0B2)   // Very Light Orange
            )
        )
        currentHour in 19..21 -> Brush.verticalGradient( // Senja (7pm - 9pm)
            colors = listOf(
                Color(0xFF9575CD),  // Light Purple - LEBIH SOFT
                Color(0xFFD1C4E9)   // Very Light Purple
            )
        )
        else -> Brush.verticalGradient(                  // Malam (10pm - 4am)
            colors = listOf(
                Color(0xFF283593),  // Dark Blue - LEBIH SOFT
                Color(0xFF5C6BC0)   // Medium Blue
            )
        )
    }
}

// MATAHARI YANG ELASTIS DAN HIDUP DENGAN ANIMASI
@Composable
fun AnimatedSunDecoration(currentHour: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "sunAnimation")

    // Animasi untuk sinar matahari
    val rayRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rayRotation"
    )

    // Animasi untuk pulsasi matahari
    val sunPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sunPulse"
    )

    // Hanya tampilkan matahari di siang hari (6 pagi - 6 sore)
    if (currentHour in 6..18) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // MATAHARI YANG ELASTIS DI ATAS KANAN
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-25).dp, y = 70.dp)
                    .drawWithContent {
                        // Apply pulsasi
                        scale(scaleX = sunPulse, scaleY = sunPulse) {
                            // Gradien matahari (kuning ke orange)
                            val sunGradient = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFEB3B),  // Kuning terang
                                    Color(0xFFFFC107),  // Kuning orange
                                    Color(0xFFFF9800)   // Orange
                                ),
                                center = center,
                                radius = size.minDimension / 2
                            )

                            // Lingkaran matahari utama dengan gradien
                            drawCircle(
                                brush = sunGradient,
                                radius = size.minDimension / 2
                            )

                            // Efek glow di sekitar matahari
                            drawCircle(
                                color = Color(0xFFFFF176).copy(alpha = 0.3f),
                                radius = size.minDimension / 2 + 8.dp.toPx(),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 10.dp.toPx()
                                )
                            )

                            // SINAR MATAHARI YANG BERPUTAR - lebih elastis
                            rotate(degrees = rayRotation) {
                                // Sinar panjang (12 sinar utama)
                                for (i in 0 until 12) {
                                    rotate(degrees = i * 30f) {
                                        drawRect(
                                            color = Color(0xFFFFF176),
                                            topLeft = Offset(size.width / 2 - 3.dp.toPx(), -25.dp.toPx()),
                                            size = androidx.compose.ui.geometry.Size(6.dp.toPx(), 35.dp.toPx())
                                        )
                                    }
                                }

                                // Sinar pendek di antara sinar panjang (12 sinar)
                                for (i in 0 until 12) {
                                    rotate(degrees = i * 30f + 15f) {
                                        drawRect(
                                            color = Color(0xFFFFF176).copy(alpha = 0.7f),
                                            topLeft = Offset(size.width / 2 - 2.dp.toPx(), -15.dp.toPx()),
                                            size = androidx.compose.ui.geometry.Size(4.dp.toPx(), 25.dp.toPx())
                                        )
                                    }
                                }
                            }

                            // EFEK TEXTURE MATAHARI - titik-titik untuk efek elastis
                            for (i in 0 until 8) {
                                val angle = i * 45f
                                val distance = size.minDimension / 3
                                val x = center.x + distance * cos(Math.toRadians(angle.toDouble())).toFloat()
                                val y = center.y + distance * sin(Math.toRadians(angle.toDouble())).toFloat()

                                drawCircle(
                                    color = Color(0xFFFFA000).copy(alpha = 0.4f),
                                    center = Offset(x, y),
                                    radius = 4.dp.toPx()
                                )
                            }
                        }
                    }
            )
        }
    } else {
        // MALAM - BULAN DAN BINTANG YANG LEBIH HIDUP
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // BULAN DI ATAS KANAN dengan gradien
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-25).dp, y = 70.dp)
                    .drawWithContent {
                        // Gradien bulan (putih ke silver)
                        val moonGradient = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFAFAFA),  // Putih terang
                                Color(0xFFE0E0E0),  // Silver
                                Color(0xFFBDBDBD)   // Silver gelap
                            ),
                            center = center,
                            radius = size.minDimension / 2
                        )

                        drawCircle(
                            brush = moonGradient,
                            radius = size.minDimension / 2
                        )

                        // Efek kawah bulan
                        drawCircle(
                            color = Color(0xFF9E9E9E).copy(alpha = 0.3f),
                            center = Offset(center.x - 15.dp.toPx(), center.y - 10.dp.toPx()),
                            radius = 8.dp.toPx()
                        )
                        drawCircle(
                            color = Color(0xFF9E9E9E).copy(alpha = 0.4f),
                            center = Offset(center.x + 10.dp.toPx(), center.y + 15.dp.toPx()),
                            radius = 5.dp.toPx()
                        )
                    }
            )

            // BINTANG-BINTANG YANG BERKEDIP
            TwinklingStar(offsetX = 100, offsetY = 120)
            TwinklingStar(offsetX = 280, offsetY = 80)
            TwinklingStar(offsetX = 200, offsetY = 150)
            TwinklingStar(offsetX = 150, offsetY = 180)
            TwinklingStar(offsetX = 300, offsetY = 150)
            TwinklingStar(offsetX = 80, offsetY = 200)
            TwinklingStar(offsetX = 250, offsetY = 220)
        }
    }
}

// Komponen bintang yang berkedip
@Composable
fun TwinklingStar(offsetX: Int, offsetY: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "starTwinkle")

    val starAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000 + (offsetX % 1000), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starAlpha"
    )

    val starSize by infiniteTransition.animateFloat(
        initialValue = 3f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500 + (offsetY % 1000), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starSize"
    )

    Box(
        modifier = Modifier
            .offset(x = offsetX.dp, y = offsetY.dp)
            .size(starSize.dp)
            .drawWithContent {
                // Bintang dengan efek glow
                drawCircle(
                    color = Color.White.copy(alpha = starAlpha),
                    radius = size.minDimension / 2
                )
                drawCircle(
                    color = Color.White.copy(alpha = starAlpha * 0.3f),
                    radius = size.minDimension / 2 + 2.dp.toPx()
                )
            }
    )
}

@Composable
fun WeatherHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSearch: () -> Unit,
    isDarkTheme: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = stringResource(id = R.string.aplikasi_cuaca),
            style = MaterialTheme.typography.headlineLarge,
            color = if (isDarkTheme) Color.White else Color(0xFF1A237E),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // SEARCH BAR dengan tema yang menyesuaikan - DIPERBAIKI
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme)
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                else
                    Color.White.copy(alpha = 0.95f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TextField dengan trailing icon - INI YANG DIPERBAIKI
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.ketik_nama_kota),
                            color = if (isDarkTheme) Color.White.copy(alpha = 0.6f) else Color.Gray
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = if (isDarkTheme) Color.White else Color.Black,
                        unfocusedTextColor = if (isDarkTheme) Color.White else Color.Black,
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = onSearch,
                            modifier = Modifier.size(48.dp) // UKURAN DIBESARKAN
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(id = R.string.search),
                                modifier = Modifier.size(30.dp), // ICON DIBESARKAN
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CurrentWeatherSection(weather: com.cahayaaya.weather.WeatherData, isDarkTheme: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme)
                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            else
                Color.White.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = stringResource(id = R.string.lokasi),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = weather.cityName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
            }

            Text(
                text = "${weather.temperature.toInt()}°",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = weather.description,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp),
                color = if (isDarkTheme) Color.White else Color.Black
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherInfoItem(stringResource(id = R.string.maks), "${(weather.temperature + 4).toInt()}°", "⬆️", isDarkTheme)
                WeatherInfoItem(stringResource(id = R.string.min), "${(weather.temperature - 4).toInt()}°", "⬇️", isDarkTheme)
                WeatherInfoItem(stringResource(id = R.string.terasa), "${weather.feelsLike.toInt()}°", "🌡️", isDarkTheme)
            }
        }
    }
}

@Composable
fun HourlyForecastSection(
    hourlyForecast: List<com.cahayaaya.weather.HourlyForecast>,
    isDarkTheme: Boolean
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(hourlyForecast) { forecast ->
            HourlyForecastItem(forecast, isDarkTheme)
        }
    }
}

@Composable
fun HourlyForecastItem(
    forecast: com.cahayaaya.weather.HourlyForecast,
    isDarkTheme: Boolean
) {
    Card(
        modifier = Modifier.width(80.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) MaterialTheme.colorScheme.surface.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = forecast.time,
                style = MaterialTheme.typography.labelSmall,
                color = if (isDarkTheme) Color.White else Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = forecast.icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${forecast.temperature.toInt()}°",
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${forecast.precipitation}%",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun WeatherDetailsSection(weather: com.cahayaaya.weather.WeatherData, isDarkTheme: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) MaterialTheme.colorScheme.surface.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.95f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetailItem(stringResource(id = R.string.angin), "${weather.windSpeed} km/jam", "💨", isDarkTheme)
                WeatherDetailItem(stringResource(id = R.string.kelembaban), "${weather.humidity}%", "💧", isDarkTheme)
                WeatherDetailItem(stringResource(id = R.string.tekanan), "${weather.pressure} hPa", "📊", isDarkTheme)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetailItem(stringResource(id = R.string.pandang), "${weather.visibility} km", "👁️", isDarkTheme)
                WeatherDetailItem("UV", weather.uvIndex.toString(), "☀️", isDarkTheme)
                WeatherDetailItem(stringResource(id = R.string.terasa), "${weather.feelsLike.toInt()}°", "🌡️", isDarkTheme)
            }
        }
    }
}

@Composable
fun WeeklyForecastSection(
    weeklyForecast: List<com.cahayaaya.weather.DailyForecast>,
    isDarkTheme: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) MaterialTheme.colorScheme.surface.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.95f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            weeklyForecast.forEachIndexed { i, forecast ->
                DailyForecastItem(forecast, isDarkTheme)
                if (i != weeklyForecast.lastIndex) {
                    Divider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = if (isDarkTheme) Color.White.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
fun DailyForecastItem(forecast: com.cahayaaya.weather.DailyForecast, isDarkTheme: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.weight(2f)) {
            Text(
                text = forecast.day,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) Color.White else Color.Black
            )
            Text(
                text = forecast.date,
                fontSize = 12.sp,
                color = if (isDarkTheme) Color.White.copy(alpha = 0.6f) else Color.Gray
            )
        }

        Text(
            text = forecast.icon,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Text(
            text = "${forecast.precipitation}%",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Column(modifier = Modifier.weight(2f), horizontalAlignment = Alignment.End) {
            Text(
                text = "${forecast.maxTemp.toInt()}°",
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color.Black
            )
            Text(
                text = "${forecast.minTemp.toInt()}°",
                color = if (isDarkTheme) Color.White.copy(alpha = 0.6f) else Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun WeatherSectionTitle(title: String, isDarkTheme: Boolean) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = if (isDarkTheme) Color.White else Color(0xFF1A237E),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
}

@Composable
fun WeatherInfoItem(title: String, value: String, emoji: String, isDarkTheme: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            color = if (isDarkTheme) Color.White.copy(alpha = 0.6f) else Color.Gray
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color.White else Color.Black
        )
    }
}

@Composable
fun WeatherDetailItem(title: String, value: String, emoji: String, isDarkTheme: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            color = if (isDarkTheme) Color.White.copy(alpha = 0.6f) else Color.Gray,
            textAlign = TextAlign.Center
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if (isDarkTheme) Color.White else Color.Black
        )
    }
}

@Composable
fun WeatherLoading() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(40.dp)) {
        CircularProgressIndicator(color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(id = R.string.memuat_data_cuaca), color = Color.White)
    }
}

@Composable
fun WeatherError(error: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.8f))
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = error, color = Color.White, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = onRetry) {
                Text(text = stringResource(id = R.string.coba_lagi))
            }
        }
    }
}