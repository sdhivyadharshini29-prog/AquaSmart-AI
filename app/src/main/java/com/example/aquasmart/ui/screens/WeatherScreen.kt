package com.example.aquasmart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.aquasmart.data.model.DistrictZone
import com.example.aquasmart.ui.AquaSmartViewModel
import com.example.aquasmart.ui.components.MetricStatCard
import com.example.aquasmart.ui.components.SectionHeader
import com.example.aquasmart.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: AquaSmartViewModel,
    modifier: Modifier = Modifier
) {
    val weather by viewModel.currentWeather.collectAsState()
    val activeFarm by viewModel.activeFarm.collectAsState()

    var showManualOverrideDialog by remember { mutableStateOf(false) }
    var simRainfall by remember { mutableStateOf(weather.rainfallLast24hMm.toString()) }
    var simTemp by remember { mutableStateOf(weather.temperatureC.toString()) }
    var simAlert by remember { mutableStateOf(weather.irrigationAlert ?: "") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("weather_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Current Weather Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weather_main_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AquaBlue.copy(alpha = 0.12f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${weather.districtName} Weather",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = weather.zone,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (weather.isManualOverride) {
                            StatusBadge(text = "SIMULATION / MANUAL MODE", isPositive = false, isWarning = true)
                        } else {
                            StatusBadge(text = "LIVE AGRO-METEOROLOGY", isPositive = true)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(AquaBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (weather.rainfallLast24hMm > 10.0) Icons.Default.WaterDrop else Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "${weather.temperatureC}°C",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = weather.weatherCondition,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Key Weather Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricStatCard(
                            title = "Humidity",
                            value = "${weather.humidityPct}%",
                            subtitle = "Relative",
                            icon = Icons.Default.Opacity,
                            accentColor = AquaBlue,
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatCard(
                            title = "24h Rain",
                            value = "${weather.rainfallLast24hMm} mm",
                            subtitle = "Observed",
                            icon = Icons.Default.Grain,
                            accentColor = ForestGreenPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        MetricStatCard(
                            title = "Wind Speed",
                            value = "${weather.windSpeedKmh} km/h",
                            subtitle = "Speed",
                            icon = Icons.Default.Air,
                            accentColor = GoldenPaddy,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 2. Weather Alert & Agricultural Impact
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weather_impact_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Agricultural Impact & Irrigation Directives",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    weather.irrigationAlert?.let { alertText ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (weather.isAlertUrgent) Color(0xFFFEF3C7) else Color(0xFFE0F2FE)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = if (weather.isAlertUrgent) Icons.Default.Warning else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (weather.isAlertUrgent) Color(0xFFB45309) else Color(0xFF0369A1)
                                )
                                Text(
                                    text = alertText,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = if (weather.isAlertUrgent) Color(0xFF78350F) else Color(0xFF0C4A6E)
                                )
                            }
                        }
                    }

                    // Spraying & Canal Advisory
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Spraying Window", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                Text(
                                    text = if (weather.rainfallLast24hMm > 5 || (weather.irrigationAlert?.contains("Rain", ignoreCase = true) == true))
                                        "UNFAVORABLE: High rain risk; chemical wash-off expected."
                                    else "FAVORABLE: Early morning or late evening.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Canal Inflow Advice", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                Text(
                                    text = if (weather.rainfallLast24hMm > 20) "Hold canal release; let rainwater saturate delta fields."
                                    else "Draw allocated canal turn for AWD wetting.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Manual Override / Simulation Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        simRainfall = weather.rainfallLast24hMm.toString()
                        simTemp = weather.temperatureC.toString()
                        simAlert = weather.irrigationAlert ?: ""
                        showManualOverrideDialog = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_manual_weather_override")
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Manual Input / Test")
                }

                if (weather.isManualOverride) {
                    Button(
                        onClick = { viewModel.resetWeatherToLiveDistrict() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_reset_weather"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset to Live")
                    }
                }
            }
        }
    }

    // Manual Weather Input / Testing Dialog
    if (showManualOverrideDialog) {
        AlertDialog(
            onDismissRequest = { showManualOverrideDialog = false },
            title = { Text("Manual Weather Override / Test") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Enter rain gauge or temperature readings to simulate and evaluate irrigation response:", style = MaterialTheme.typography.bodySmall)

                    OutlinedTextField(
                        value = simRainfall,
                        onValueChange = { simRainfall = it },
                        label = { Text("Rainfall (mm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = simTemp,
                        onValueChange = { simTemp = it },
                        label = { Text("Temperature (°C)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = simAlert,
                        onValueChange = { simAlert = it },
                        label = { Text("Custom Weather Advisory (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val rain = simRainfall.toDoubleOrNull() ?: 0.0
                        val temp = simTemp.toDoubleOrNull() ?: 32.0
                        val alert = simAlert.ifBlank { null }
                        viewModel.updateManualWeather(rain, temp, alert)
                        showManualOverrideDialog = false
                    },
                    modifier = Modifier.testTag("btn_apply_manual_weather")
                ) {
                    Text("Apply Weather")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualOverrideDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
