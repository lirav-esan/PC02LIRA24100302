package com.example.pc02lira24100302.presentation.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pc02lira24100302.data.remote.FirebaseFirestoreManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var monto by remember { mutableStateOf("") }
    var monedaOrigen by remember { mutableStateOf("USD") }
    var monedaDestino by remember { mutableStateOf("EUR") }
    var resultText by remember { mutableStateOf("") }

    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    val currencies = listOf("USD", "EUR", "PEN", "GBP", "JPY")

    // Estado dinámico para las tasas (con un fallback local por si la red falla)
    var exchangeRates by remember {
        mutableStateOf(
            mapOf("USD" to 1.0, "EUR" to 0.925, "PEN" to 3.75, "GBP" to 0.79, "JPY" to 155.0)
        )
    }

    // Cargar las tasas desde Firestore al entrar a la pantalla
    LaunchedEffect(Unit) {
        val result = FirebaseFirestoreManager.getExchangeRates()
        if (result.isSuccess && result.getOrNull()?.isNotEmpty() == true) {
            exchangeRates = result.getOrThrow()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Conversor de Divisas") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- CAMPO: MONTO ---
            OutlinedTextField(
                value = monto,
                onValueChange = { monto = it },
                label = { Text("Monto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // --- SELECCIÓN: DE (FROM) ---
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = monedaOrigen,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("De") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Box(modifier = Modifier.matchParentSize().clickable { fromExpanded = true })
                DropdownMenu(expanded = fromExpanded, onDismissRequest = { fromExpanded = false }) {
                    currencies.forEach { curr ->
                        DropdownMenuItem(text = { Text(curr) }, onClick = { monedaOrigen = curr; fromExpanded = false })
                    }
                }
            }

            IconButton(onClick = {
                val temp = monedaOrigen
                monedaOrigen = monedaDestino
                monedaDestino = temp
            }) {
                Icon(Icons.Default.Refresh, contentDescription = "Invertir")
            }

            // --- SELECCIÓN: A (TO) ---
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = monedaDestino,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("A") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Box(modifier = Modifier.matchParentSize().clickable { toExpanded = true })
                DropdownMenu(expanded = toExpanded, onDismissRequest = { toExpanded = false }) {
                    currencies.forEach { curr ->
                        DropdownMenuItem(text = { Text(curr) }, onClick = { monedaDestino = curr; toExpanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- BOTÓN CONVERTIR Y ALMACENAR ---
            Button(
                onClick = {
                    val parsedAmount = monto.toDoubleOrNull()
                    if (parsedAmount != null) {
                        val rateFrom = exchangeRates[monedaOrigen] ?: 1.0
                        val rateTo = exchangeRates[monedaDestino] ?: 1.0

                        val amountInUSD = parsedAmount / rateFrom
                        val convertedAmount = amountInUSD * rateTo

                        val formattedResult = String.format("%.2f", convertedAmount)
                        resultText = "$monto $monedaOrigen equivalen a $formattedResult $monedaDestino"

                        // Lanzamos una corrutina para guardar de forma asíncrona en Firestore
                        scope.launch {
                            val saveResult = FirebaseFirestoreManager.saveConversion(
                                amount = parsedAmount,
                                fromCurrency = monedaOrigen,
                                toCurrency = monedaDestino,
                                result = convertedAmount
                            )
                            if (saveResult.isFailure) {
                                Toast.makeText(context, "No se guardó el historial en la BD", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        resultText = "Por favor, ingrese un monto válido."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Convertir")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (resultText.isNotEmpty()) {
                Text(text = resultText, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}