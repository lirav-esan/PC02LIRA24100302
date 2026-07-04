package com.example.pc02lira24100302.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

object FirebaseFirestoreManager {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // rates
    suspend fun getExchangeRates(): Result<Map<String, Double>> {
        return try {
            val snapshot = db.collection("rates").get().await()
            val ratesMap = mutableMapOf<String, Double>()
            for (document in snapshot.documents) {
                val rate = document.getDouble("rate") ?: 1.0
                ratesMap[document.id] = rate
            }
            Result.success(ratesMap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Guardar la conversión en la colección de historial
    suspend fun saveConversion(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
        result: Double
    ): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            val uid = currentUser?.uid ?: "anonymous"

            val conversionData = hashMapOf(
                "uid" to uid,
                "timestamp" to Date(), // Guarda Fecha y Hor    a actual
                "amount" to amount,
                "fromCurrency" to fromCurrency,
                "toCurrency" to toCurrency,
                "result" to result
            )

            db.collection("conversions").add(conversionData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}