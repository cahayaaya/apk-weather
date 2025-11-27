package com.cahayaaya.weather.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    // Cari kota → dapatkan latitude longitude
    @GET("v1/search")
    fun getCity(
        @Query("name") name: String,
        @Query("count") count: Int = 1
    ): Call<GeoResponse>

    // Untuk cuaca, kita butuh service terpisah
}