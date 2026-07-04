package com.example.pc02lira24100302.data.remote

import com.google.gson.annotations.SerializedName

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface UniRateApiService {

    @GET("rates") // Ajustar la ruta exacta según la documentación oficial del endpoint de UniRate
    suspend fun getExchangeRates(
        @Header("Authorization") apiKey: String, // Si usa autenticación por Bearer Token
        @Query("base") base: String
    ): UniRateResponse
}
data class UniRateResponse(
    @SerializedName("base") val baseCurrency: String,
    @SerializedName("rates") val rates: Map<String, Double>,
    @SerializedName("date") val date: String? = null
)
class CurrencyRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.unirateapi.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(UniRateApiService::class.java)

    fun fetchRates() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiKey = "Bearer gFudal4R0OqqJdpVS7q2dnFM8K0MfnbhhwrIj0AtidNO1KycgZUtE0HMKYZfUbqB"
                val response = apiService.getExchangeRates(apiKey, "USD")

                val eurRate = response.rates["EUR"]
                Log.d("UniRateAPI", "El valor del USD a EUR es: $eurRate")

            } catch (e: Exception) {
                Log.e("UniRateAPI", "Error al obtener datos: ${e.message}", e)
            }
        }
    }
}